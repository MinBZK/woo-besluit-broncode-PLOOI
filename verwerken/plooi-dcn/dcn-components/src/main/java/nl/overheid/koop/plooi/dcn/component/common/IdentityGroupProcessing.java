package nl.overheid.koop.plooi.dcn.component.common;

import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.model.IdentityGroup;
import nl.overheid.koop.plooi.dcn.model.IdentityGroupRepository;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityGroupProcessing implements EnvelopeProcessing<PlooiEnvelope> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IdentityGroupRepository identityGroupRepository;

    public IdentityGroupProcessing(IdentityGroupRepository identityGroupRepository) {
        this.identityGroupRepository = identityGroupRepository;
    }

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        this.logger.debug("Duplication Detection for {}", target);
        var latest = AggregatedVersion.aggregateLatestVersion(target.getPlooi().getVersies());
        if (latest.isEmpty()) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, "TODO");
        } else {
            latest.get()
                    .toVersie()
                    .getBestanden()
                    .stream()
                    .filter(Bestand::isGepubliceerd)
                    .map(Bestand::getHash)
                    .filter(Objects::nonNull)
                    .map(hash -> this.identityGroupRepository.save(new IdentityGroup(hash)))
                    .map(identityGroup -> new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri()).relation(identityGroup.getId()))
                    .findAny()
                    .ifPresent(target::addRelation);
        }
        return target;
    }
}
