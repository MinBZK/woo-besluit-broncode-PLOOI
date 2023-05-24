
# PLOOI Security
Dit project is een generiek projct die als dependency kan worden toegevoegd aan een microservice. Met 
het toevoegen van dit project wordt je service beveiligd. Het project filtert de het request op de benodigde
headers die aan een request zijn toegevoegd. Er zijn 3 mogelijke opties:

1. JWT versturen in de "Authorization" header.
2. CA JWT versturen in de "X-JWT-Token" header.
3. Token voor de sandbox in de "Aanlever-Token" header.

## Gebruik

### Service beveiligen
Voeg onderstaande dependency toe in je pom.xml

```xml
<dependency>
    <groupId>nl.overheid.koop.plooi</groupId>
    <artifactId>plooi-security</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Neem de volgende configuratie op in je application.yaml.

```yaml
plooi:
  security:
    type: JWT || SDB
    plooi-iam-service-url: | 
      https://SSSSSSSSSSSSSSSSSS || 
      https://SSSSSSSSSSSSSSSSSS
```

### Token propageren
Wanneer de services zijn beveiligd is het nodig om bij service-to-service communicatie
de token te propageren zodat ook dit request geautoriseerd is om door te gaan. Dit kun 
je eenvoudig doen door de volgende configuratie op te nemen bij het registreren van een 
WebClient bean:

```java
@Configuration
public class RestConfiguration {
    private final RequestFilter requestFilter;
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://open.overheid.nl")
                .filter(ExchangeFilterFunction.ofRequestProcessor(requestFilter::propagateAuthorization))
                .build();
    }
}
```
