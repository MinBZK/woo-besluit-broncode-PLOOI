package nl.overheid.koop.plooi.document.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.tuple.Pair;

public class PlooiFileMapperConfig {

    private ConfigurableFileMapper parentMapper;

    public PlooiFileMapperConfig setParentMapper(ConfigurableFileMapper parent) {
        this.parentMapper = parent;
        return this;
    }

    public ConfigurableFileMapper mapper() {
        return Objects.requireNonNull(this.parentMapper, "parent mapper is required");
    }

    String urlPath;
    String idPath;
    DateMapping timestampMapping;
    TextMapping titelMapping;
    String bestandsnaamPath;
    String mimeType;
    String mimeTypePath;
    String label;
    String labelPath;
    boolean publish;
    List<Pair<String, PlooiFileMapperConfig>> childMappers = new ArrayList<>();
    boolean discardContent;

    public PlooiFileMapperConfig mapUrl(String path) {
        this.urlPath = path;
        return this;
    }

    public PlooiFileMapperConfig mapIdentifier(String path) {
        this.idPath = path;
        return this;
    }

    public PlooiFileMapperConfig mapTimestamp(DateMapping mapping) {
        this.timestampMapping = mapping;
        return this;
    }

    public PlooiFileMapperConfig mapTitel(TextMapping mapping) {
        this.titelMapping = mapping;
        return this;
    }

    public PlooiFileMapperConfig mapBestandsnaam(String path) {
        this.bestandsnaamPath = path;
        return this;
    }

    public PlooiFileMapperConfig setMimeType(String value) {
        this.mimeType = value;
        return this;
    }

    public PlooiFileMapperConfig mapMimeType(String path) {
        this.mimeTypePath = path;
        return this;
    }

    public PlooiFileMapperConfig setLabel(String value) {
        this.label = value;
        return this;
    }

    public PlooiFileMapperConfig mapLabel(String path) {
        this.labelPath = path;
        return this;
    }

    public PlooiFileMapperConfig publish() {
        this.publish = true;
        return this;
    }

    public PlooiFileMapperConfig addChildMapper(String path, PlooiFileMapperConfig mapperConfig) {
        this.childMappers.add(Pair.of(path, mapperConfig));
        return this;
    }

    public PlooiFileMapperConfig discardContent() {
        this.discardContent = true;
        return this;
    }
}
