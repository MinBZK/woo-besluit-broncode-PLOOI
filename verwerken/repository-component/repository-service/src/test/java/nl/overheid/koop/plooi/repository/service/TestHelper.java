package nl.overheid.koop.plooi.repository.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import org.apache.commons.io.FileUtils;

final class TestHelper {

    static final String REPOS_DIR = "target/repos";
    static final String DEFAULT_SRC = "plooi-dcn";
    static final String DEFAULT_EXTID = "9495beca-ff69-4005-bb29-111111111111";

    private static String INTERN = """
            {
              "plooiIntern" : {
                    "dcnId" : "%s",
                    "extId" : [ "%s" ],
                    "sourceLabel" : "%s"
              },
                """;
    private static String MINIMAL_DOCUMENT = """
              "document" : {
                "titelcollectie" : {
                  "officieleTitel" : "Test"
                }
              }
            }
            """;
    private static String PREFIX = """
            "versies" : [ """;
    private static List<String> VERSIONS = List.of("""
            {
              "nummer" : 1,
              "oorzaak" : "aanlevering",
              "openbaarmakingsdatum" : "2023-01-01",
              "mutatiedatumtijd" : "2023-01-01T12:00Z",
              "bestanden" : [ {
                "gepubliceerd" : false,
                "label" : "meta",
                "mime-type" : "application/json",
                "bestandsnaam" : "metadata.json",
                "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
              }, {
                "gepubliceerd" : true,
                "label" : "document",
                "mime-type" : "application/pdf",
                "bestandsnaam" : "document.pdf",
                "hash" : "0000000000000000000000000000000000000000"
              } ]
            }""", """
            {
              "nummer" : 2,
              "oorzaak" : "wijziging",
              "wijzigingsdatum" : "2023-01-02",
              "mutatiedatumtijd" : "2023-01-02T12:00Z",
              "bestanden" : [ {
                "gepubliceerd" : true,
                "label" : "document",
                "mime-type" : "application/pdf",
                "bestandsnaam" : "gewijzigd.pdf",
                "hash" : "0000000000000000000000000000000000000001"
              } ]
            }""", """
            {
              "nummer" : 3,
              "oorzaak" : "wijziging",
              "wijzigingsdatum" : "2023-01-03",
              "mutatiedatumtijd" : "2023-01-03T12:00Z",
              "bestanden" : [ {
                "gepubliceerd" : false,
                "label" : "meta",
                "mime-type" : "application/json",
                "bestandsnaam" : "gewijzigd.json",
                "hash" : "0000000000000000000000000000000000000002"
              } ]
            }""", """
            {
              "oorzaak" : "intrekking",
              "wijzigingsdatum" : "2023-01-04",
              "mutatiedatumtijd" : "2023-01-04T12:00Z"
            }""",
            // This herpublicatie is valid for posts, but invalid for requests, since the bestanden are set when receiving it.
            // However, AggregatedVersion is forgiving and reconstructs the correct bestanden anyway
            """
                    {
                      "oorzaak" : "herpublicatie",
                      "wijzigingsdatum" : "2023-01-05",
                      "mutatiedatumtijd" : "2023-01-05T12:00Z"
                    }""");
    private static String SUFFIX = " ] }";

    private TestHelper() {
    }

    static InputStream getFirstManifest() {
        return getManifest(1, DEFAULT_SRC, DEFAULT_EXTID);
    }

    static InputStream getDeletedManifest() {
        return getManifest(4, DEFAULT_SRC, DEFAULT_EXTID);
    }

    static InputStream getManifest(Integer version, String sourceLabel, String extId) {
        return new ByteArrayInputStream((String.format(INTERN, DcnIdentifierUtil.generateDcnId(sourceLabel, extId), extId, sourceLabel)
                + PREFIX
                + String.join(", ", VERSIONS.subList(0, version))
                + SUFFIX).getBytes(StandardCharsets.UTF_8));
    }

    static InputStream getPlooi(Integer version, String sourceLabel, String extId) {
        return new ByteArrayInputStream((String.format(INTERN, DcnIdentifierUtil.generateDcnId(sourceLabel, extId), extId, sourceLabel)
                + MINIMAL_DOCUMENT).getBytes(StandardCharsets.UTF_8));
    }

    static String reposUpdates() throws IOException {
        var dir = new File(REPOS_DIR);
        return dir.exists()
                ? FileUtils.streamFiles(dir, true, null)
                        .map(f -> (f.getParent() + "/" + f.getName()).replaceFirst(REPOS_DIR + "/[0-9a-f]{2}/[0-9a-f]{4}/[-a-z]+-[-0-9a-f]{36,40}/", ""))
                        .sorted()
                        .collect(Collectors.joining(", "))
                : "";
    }
}
