package nl.overheid.koop.plooi.dcn.admin;

public record DocumentStatistics (String sourceName, Long processingErrorCount, Long mappingErrorCount,Long mappingWarningCount) {
}
