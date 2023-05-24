package nl.overheid.koop.plooi.document.map;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.FileProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Maps input to a {@link PlooiFile}. */
public final class ConfigurableFileMapper implements FileProcessing {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PlooiFileMapperConfig cfg;
    private final PlooiMapping plooiMappingFact;

    /**
     * Factory method, producing an instance of this mapper class with accompanying {@link PlooiFileMapperConfig}
     *
     * @param  mappingFact The {@link ConfigurableFileMapper} factory to use by this mapper
     * @return             The {@link PlooiFileMapperConfig} for this mapper
     */
    public static PlooiFileMapperConfig configureWith(PlooiMapping mappingFact) {
        var mapper = new ConfigurableFileMapper(mappingFact, new PlooiFileMapperConfig());
        return mapper.cfg.setParentMapper(mapper);
    }

    /**
     * Produces a PlooiFileMapperConfig for child mappers.
     * <p>
     * These configs are not yet bound to a ConfigurableFileMapper, that happens when the child ConfigurableFileMapper is
     * created.
     */
    public static PlooiFileMapperConfig childConfiguration() {
        return new PlooiFileMapperConfig();
    }

    /** Use {@link #configureWith()} to instantiate this mapper. */
    private ConfigurableFileMapper(PlooiMapping mappingFact, PlooiFileMapperConfig mappingConfig) {
        this.plooiMappingFact = mappingFact;
        this.cfg = mappingConfig;
    }

    /**
     * Populate a {@link PlooiFile} from its {@link PlooiFile#getContent() content} bytes.
     *
     * @param  target The PlooiFile to populate
     * @return        The populated PlooiFile
     */
    @Override
    public PlooiFile process(PlooiFile target) {
        try (InputStream contentStrm = new ByteArrayInputStream(target.getContent())) {
            PlooiMappingInstance fileMapper = Objects.requireNonNull(this.plooiMappingFact, "Cannot do this on a child mapper").getInstance(contentStrm);
            populatePlooiFile(fileMapper, target);
            for (var childMapper : this.cfg.childMappers) {
                for (var childMapping : fileMapper.childMapping(childMapper.getKey())) {
                    var child = new ConfigurableFileMapper(null, childMapper.getValue()).populatePlooiFile(childMapping, target.newChild());
                    this.logger.trace(" - Added child {}", child);
                }
            }
            this.logger.debug("Populated {}", target);
            return target;
        } catch (PlooiMappingException e) {
            throw e;
        } catch (Exception e) {
            throw new PlooiMappingException(e);
        }
    }

    private PlooiFile populatePlooiFile(PlooiMappingInstance pm, PlooiFile target) {
        var bestand = target.getFile();
        pm.mapString(this.cfg.urlPath, bestand::setUrl);
        pm.mapString(this.cfg.bestandsnaamPath, bestand::setBestandsnaam);
        pm.mapString(this.cfg.idPath, bestand::id);
        pm.mapDate(this.cfg.timestampMapping, ts -> bestand.mutatiedatumtijd(OffsetDateTime.from(ts)));
        pm.mapString(TextMapping.getPath(this.cfg.titelMapping), t -> bestand.setTitel(TextMapping.getText(this.cfg.titelMapping, t)));
        pm.mapString(this.cfg.mimeTypePath, bestand::setMimeType);
        if (this.cfg.mimeType != null) {
            bestand.setMimeType(this.cfg.mimeType);
        }
        pm.mapString(this.cfg.labelPath, bestand::setLabel);
        if (this.cfg.label != null) {
            bestand.setLabel(this.cfg.label);
        }
        if (this.cfg.publish) {
            bestand.gepubliceerd(true);
        }
        if (this.cfg.discardContent) {
            target.content(null);
        }
        return target.postPopulate();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " applying " + this.plooiMappingFact;
    }
}
