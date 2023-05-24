package nl.overheid.koop.plooi.aanleveren.metadata.web.response;

import java.util.List;

public record ValidatieBericht(List<String> schemaPointers, List<String> dataPointers, String content) {
}
