package nl.overheid.koop.plooi.openapi.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class OpenapiConfiguration implements WebMvcConfigurer {

    @Value("${plooi.openapi.directory:openapi}")
    private String openapiDir;

    /**
     * Expose the OpenAPI specification(s) as static resources.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + this.openapiDir + "/**").addResourceLocations("classpath:/" + this.openapiDir + "/");
    }
}
