package nl.overheid.koop.plooi.dcn.route.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import javax.xml.bind.ValidationException;
import nl.overheid.koop.plooi.dcn.route.common.httpexception.HttpNotRecoverableException;
import nl.overheid.koop.plooi.dcn.route.common.httpexception.HttpRecoverableException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * This contains the error handlers that all Routes should apply. Per exception type number of re-deliveries and
 * re-delivery delay can be tuned. The handlers all log and send the error message to the default
 * {@link CommonRoute#COMMON_ERROR_ROUTE}.
 */
public class ErrorHandling {

    public static final int DEFAULT_RETRIES = 5;
    public static final long DEFAULT_WAIT = 5_000L;
    public static final String DEFAULT_LONG_REDELIVERY_PATTERN = "0:5000;1:10000;2:30000;3:60000;4:180000";

    private final RouteBuilder subject;
    private final String errorRouteId;

    private int httpRedeliveries = DEFAULT_RETRIES;
    private String longRedeliveryPattern = DEFAULT_LONG_REDELIVERY_PATTERN;

    public ErrorHandling withHttpRedeliveries(int redeliveries) {
        this.httpRedeliveries = redeliveries;
        return this;
    }

    public ErrorHandling withHttpRedeliveryPattern(String longRedeliveryPattern) {
        this.longRedeliveryPattern = StringUtils.defaultIfBlank(longRedeliveryPattern, DEFAULT_LONG_REDELIVERY_PATTERN);

        return this;
    }

    public ErrorHandling(RouteBuilder subj) {
        this.subject = subj;
        this.errorRouteId = this.subject.getClass().getSimpleName().concat("-ErrorHandler");
    }

    @SuppressWarnings("unchecked")
    public void configure() {
        this.subject.onException(Exception.class)
                .id(this.errorRouteId + "-GenericException")
                .handled(true)
                .maximumRedeliveries(0)
                .redeliveryDelay(0)
                .to(Routing.internal(CommonRoute.COMMON_ERROR_ROUTE));

        this.subject.onException(ValidationException.class)
                .id(this.errorRouteId + "-ValidationException")
                .handled(true)
                .maximumRedeliveries(0)
                .redeliveryDelay(0)
                .to(Routing.internal(CommonRoute.COMMON_ERROR_ROUTE));

        this.subject.onException(NoSuchFileException.class, FileNotFoundException.class)
                .id(this.errorRouteId + "-FileException")
                .handled(true)
                .maximumRedeliveries(0)
                .redeliveryDelay(0)
                .to(Routing.internal(CommonRoute.COMMON_ERROR_ROUTE));

        this.subject.onException(IOException.class, HttpRecoverableException.class)
                .id(this.errorRouteId + "-HTTPRecoverableExceptionAndIOOperationException")
                .handled(true)
                .maximumRedeliveries(this.httpRedeliveries)
                .delayPattern(this.longRedeliveryPattern)
                .to(Routing.internal(CommonRoute.COMMON_ERROR_ROUTE));

        this.subject.onException(HttpNotRecoverableException.class)
                .id(this.errorRouteId + "-HTTPNotRecoverableException")
                .handled(true)
                .maximumRedeliveries(0)
                .redeliveryDelay(0)
                .to(Routing.internal(CommonRoute.COMMON_ERROR_ROUTE));

    }
}
