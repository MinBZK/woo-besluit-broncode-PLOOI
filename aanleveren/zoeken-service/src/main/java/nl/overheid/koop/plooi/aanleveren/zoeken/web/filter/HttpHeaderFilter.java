package nl.overheid.koop.plooi.aanleveren.zoeken.web.filter;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.httpheader.HttpHeadersBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        val req = (HttpServletRequest) request;
        val res = (HttpServletResponse) response;

        HttpHeadersBuilder.builder()
                .apiVersion()
                .cacheControl()
                .contentLanguage()
                .xFrameOptions()
                .xContentTypeOptions()
                .contentSecurityPolicy()
                .referrerPolicy()
                .build()
                .toSingleValueMap()
                .forEach(res::addHeader);


        chain.doFilter(req, res);
    }
}