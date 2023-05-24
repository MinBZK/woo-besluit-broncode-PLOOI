package nl.overheid.koop.plooi.dcn.route.configuration;

import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HttpConfiguration {

    public static final String HEADER_X_API_KEY = "X-API-Key";
    public static final String HEADER_IBABS_SITE_NAME = "ibabs-site-name";
    public static final String HEADER_CONNECTION = "Connection";

    /**
     * This filter strategy is to applied to all http calls going out in order to prevent internal headers from being sent
     * all over the place. So, if other headers need to be used, they need to be added here.
     *
     * @return DefaultHeaderFilterStrategy that is set to only allow certain headers to go out
     */
    @Bean
    public HeaderFilterStrategy internalHeaderFilter() {
        var hfs = new DefaultHeaderFilterStrategy();
        hfs.setInFilter(Set.of(
                Exchange.CONTENT_TYPE));
        hfs.setOutFilter(Set.of(
                Exchange.HTTP_METHOD,
                Exchange.HTTP_QUERY,
                Exchange.CONTENT_TYPE,
                HEADER_CONNECTION,
                HEADER_X_API_KEY,
                HEADER_IBABS_SITE_NAME));
        hfs.setFilterOnMatch(false);
        return hfs;
    }
}
