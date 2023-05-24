package nl.overheid.koop.plooi.dcn.route.prep;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Objects;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Makes sure paging headers and a {@value Prepping#DCN_SINCE_HEADER} header are set.
 */
public class PageQueryPrep implements Prepping {

    public static final String HTTP_URI_WITH_PROXY = "http://localhost"
            + "?httpMethod=GET"
            + "&throwExceptionOnFailure=false"
            + "&connectionClose=true"
            + "&headerFilterStrategy=#internalHeaderFilter"
            + "{{dcn.proxySettings:}}";
    public static final String PREPARE_NEXT_METHOD = "prepareNext(${headers})";
    public static final String FINISH_METHOD = "finish(${headers})";
    public static final String DCN_OFFSET_VALUE_HEADER = "dcn_offset";

    private static final String MOCK_SERVICE = System.getenv("MOCK_SERVICE");
    private static final int DEFAULT_PAGE_SIZE = 25;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String baseUrl = "";
    private int size = DEFAULT_PAGE_SIZE;
    private String sizeParam = "rows";
    private String offsetParam = "start";
    private DateTimeFormatter sinceFmt = DateTimeFormatter.BASIC_ISO_DATE;
    private String sinceParam;
    private Period sinceOffset = Period.ZERO;

    public PageQueryPrep setBaseUrl(String url) {
        if (StringUtils.isNotBlank(MOCK_SERVICE)) {
            url = mockUrl(url, MOCK_SERVICE);
        }
        this.baseUrl = url.endsWith("?") || url.endsWith("&") ? url : url.concat("?");
        return this;
    }

    String mockUrl(String oldUrl, String mockService) {
        try {
            var oldUri = new URI(oldUrl);
            int portSep = mockService.indexOf(':');
            return new URI("http",
                    null,
                    portSep < 0 ? mockService : mockService.substring(0, mockService.indexOf(':')),
                    portSep < 0 ? 80 : Integer.valueOf(mockService.substring(mockService.indexOf(':') + 1)),
                    "/" + oldUri.getHost() + oldUri.getPath(),
                    oldUri.getQuery(),
                    oldUri.getFragment()).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Troubling with " + oldUrl, e);
        }
    }

    public PageQueryPrep setPageSize(int pageSize) {
        this.size = pageSize;
        return this;
    }

    public PageQueryPrep setPageSizeParam(String pageSizeParam) {
        this.sizeParam = pageSizeParam;
        return this;
    }

    public PageQueryPrep setPageOffsetParam(String pageOffsetParam) {
        this.offsetParam = pageOffsetParam;
        return this;
    }

    public PageQueryPrep setSinceDateParam(String sinceDateParam) {
        this.sinceParam = sinceDateParam;
        return this;
    }

    public PageQueryPrep setSinceDateFormat(String sinceDateFormat) {
        this.sinceFmt = new DateTimeFormatterBuilder().appendPattern(sinceDateFormat).toFormatter();
        return this;
    }

    public PageQueryPrep setSinceDateOffset(Period sinceDateOffset) {
        this.sinceOffset = sinceDateOffset;
        return this;
    }

    @Override
    public Map<String, Object> prepare(Map<String, Object> headers) {
        return prepareQuery(headers, false);
    }

    public Map<String, Object> prepareNext(Map<String, Object> headers) {
        prepareQuery(headers, true);
        headers.compute(DCN_OFFSET_VALUE_HEADER, (key, val) -> Integer.valueOf(((Integer) val).intValue() + this.size));
        return headers;
    }

    private Map<String, Object> prepareQuery(Map<String, Object> headers, boolean initOffset) {
        var offset = (Integer) headers.get(DCN_OFFSET_VALUE_HEADER);
        var query = new StringBuilder(this.baseUrl)
                .append(Objects.requireNonNull(this.sizeParam, "pageSizeParam is required"))
                .append("=")
                .append(this.size);
        if (offset != null && offset.intValue() >= 0) {
            query.append("&")
                    .append(Objects.requireNonNull(this.offsetParam, "pageOffsetParam is required"))
                    .append("=")
                    .append(offset);
        } else if (initOffset) {
            headers.put(DCN_OFFSET_VALUE_HEADER, Integer.valueOf(0));
        }
        if (this.sinceParam != null) {
            var since = headers.computeIfAbsent(DCN_SINCE_HEADER, key -> LocalDate.now());
            query.append("&")
                    .append(this.sinceParam)
                    .append("=")
                    .append(since instanceof Temporal sinceTemporal
                            ? this.sinceFmt.format(sinceTemporal.minus(this.sinceOffset))
                            : since.toString());
        }
        headers.put(this.baseUrl.isEmpty() ? Exchange.HTTP_QUERY : Exchange.HTTP_URI, query.toString());
        this.logger.trace("Prepared HTTP query {}", query);
        return headers;
    }

    public void finish(Map<String, Object> headers) {
        this.logger.debug("No more documents found after position {}", headers.get(DCN_OFFSET_VALUE_HEADER));
        headers.remove(DCN_OFFSET_VALUE_HEADER);
        headers.remove(this.baseUrl.isEmpty() ? Exchange.HTTP_QUERY : Exchange.HTTP_URI);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
