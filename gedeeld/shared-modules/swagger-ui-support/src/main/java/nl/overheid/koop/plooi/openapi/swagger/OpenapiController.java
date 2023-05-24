package nl.overheid.koop.plooi.openapi.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Home redirection to OpenAPI api documentation
 */
@Controller
public class OpenapiController {

    private static final String PRE = """
            window.onload = function() {
              window.ui = SwaggerUIBundle({
                url: "/""";
    private static final String POST = """
                ",
                dom_id: '#swagger-ui',
                deepLinking: true,
                presets: [
                  SwaggerUIBundle. presets.apis,
                  SwaggerUIStandalonePreset
                ],
                plugins: [
                  SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout"
              });
            };
            """;

    @Value("${plooi.openapi.directory:openapi}")
    private String openapiDir;
    @Value("${plooi.openapi.specification:api.yaml}")
    private String openapiSpec;

    /**
     * The service roo URL opens the Swagger UI
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:/webjars/swagger-ui/index.html";
    }

    /**
     * Configure Swagger UI with our OpenAPI specification, instead of the Petstore.
     *
     * @see https://github.com/webjars/swagger-ui/issues/156
     */
    @RequestMapping("/webjars/swagger-ui/swagger-initializer.js")
    @ResponseBody
    public String swaggerInitializer() {
        return PRE + this.openapiDir + "/" + this.openapiSpec + POST;
    }
}
