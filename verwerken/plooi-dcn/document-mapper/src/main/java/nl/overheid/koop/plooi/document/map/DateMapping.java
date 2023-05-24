package nl.overheid.koop.plooi.document.map;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQueries;
import javax.annotation.Nonnull;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record DateMapping(String path, @Nonnull DateTimeFormatter formatter) {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateMapping.class);

    public static DateMapping isoDate(String path) {
        return new DateMapping(path, DateTimeFormatter.ISO_DATE);
    }

    public static DateMapping isoDateTime(String path) {
        return new DateMapping(path, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static DateMapping withFormat(String path, DateTimeFormatter formatter) {
        return new DateMapping(path, formatter);
    }

    public static DateMapping withFormat(String path, String format) {
        return new DateMapping(path, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Maps a mapping with path expression and format definition to a timestamp ({@link ZonedDateTime}) object.
     *
     * @param  mapping               The mapping to evaluate for the {@link ZonedDateTime timestamp}
     * @param  setter                Optional supplier to set the {@link ZonedDateTime timestamp} value on an object
     * @return                       The {@link IdentifiedResource} value or null
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    public ZonedDateTime mapDate(String dateStr) {
        if (dateStr != null) {
            try {
                var temporal = this.formatter().parse(dateStr);
                var offset = temporal.query(TemporalQueries.offset());
                var date = temporal.query(TemporalQueries.localDate());
                var time = temporal.query(TemporalQueries.localTime());
                if (date == null) {
                    LOGGER.info("Date string '{}' does not contain a date", dateStr);
                } else {
                    return ZonedDateTime.of(
                            date,
                            time == null ? LocalTime.MIDNIGHT : time,
                            offset == null ? ZoneId.systemDefault() : offset);
                }
            } catch (DateTimeParseException e) {
                LOGGER.info("Cannot parse date string '{}' with format {}", dateStr, this.formatter());
            }
        }
        return null;
    }
}
