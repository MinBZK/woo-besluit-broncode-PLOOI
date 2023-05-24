package nl.overheid.koop.plooi.repository.idmapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdentifierMapping {

    private static final Map<String, String> TO_DCN;
    private static final Map<String, String> TO_PID;

    static {
        try (var propStream = IdentifierMapping.class.getResourceAsStream("IdentifierMapping.properties")) {
            var mappings = new Properties();
            mappings.load(propStream);
            TO_PID = mappings.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
            TO_DCN = mappings.entrySet().stream().collect(Collectors.toMap(e -> e.getValue().toString(), e -> e.getKey().toString()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private final String pidPrefix;

    public IdentifierMapping(@Value("${open.overheid.url:}") String ooUrl) {
        this.pidPrefix = StringUtils.defaultIfBlank(ooUrl, "https://open.overheid.nl/documenten/");
    }

    public String toDcn(String id) {
        var dcnId = DcnIdentifierUtil.toDcn(id);
        return TO_DCN.getOrDefault(dcnId, dcnId);
    }

    public Plooi mapPid(Plooi plooi) {
        if (plooi.getDocument() != null) {
            plooi.getDocument().pid(toPid(plooi.getPlooiIntern().getDcnId()));
            mapPid(plooi.getDocumentrelaties());
        }
        return plooi;
    }

    public List<Relatie> mapPid(List<Relatie> relaties) {
        if (relaties != null) {
            relaties.stream()
                    .filter(rel -> RelationType.Type.DIRECT == RelationType.forUri(rel.getRole()).getType())
                    .forEach(rel -> rel.relation(toPid(rel.getRelation())));
        }
        return relaties;
    }

    private String toPid(String dcnId) {
        return this.pidPrefix + (dcnId.startsWith(DcnIdentifierUtil.PLOOI_API_SRC)
                ? dcnId.substring(DcnIdentifierUtil.PLOOI_API_SRC.length() + 1)
                : TO_PID.getOrDefault(dcnId, dcnId));
    }
}
