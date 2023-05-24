package nl.overheid.koop.plooi.aanleveren.metadata.web.filter;

import nl.overheid.koop.plooi.aanleveren.httpheader.HttpHeadersBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class AfterFilter implements Filter {

    final HttpHeaders mapOfHeaders = HttpHeadersBuilder.builder()
            .apiVersion()
            .contentLanguage()
            .xFrameOptions()
            .xContentTypeOptions()
            .contentSecurityPolicy()
            .referrerPolicy()
            .cacheControl()
            .build();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        mapOfHeaders.toSingleValueMap().forEach(res::addHeader);

        chain.doFilter(req, res);
    }

}
