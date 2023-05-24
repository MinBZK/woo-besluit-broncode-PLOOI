
# Aanleveren core
Een library waar functionaliteit wordt geplaatst die hergebruikt kan worden over meerdere microservices.

## Aanleveren security
Aanleveren security bevat functionaliteit om binnenkomende requests te autoriseren op basis van de "Aanlever-Token".
Er wordt gecontroleerd op aanwezigheid en wanneer aanwezig wordt de Aanlever-Token doorgestuurd naar de authentication
service om te verifiëren op geldigheid. De aanleveren security heeft dus een afhankelijkheid met de authentication
service.

### Gebruik
Om de aanleveren security module in je project te gebruiken kan je de onderstaan dependency opnemen in je pom.xml.

```xml
<dependency>
    <groupId>nl.overheid.koop.plooi.aanleveren</groupId>
    <artifactId>aanleveren-security</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Vervolgens kan de annotatie @SecureWithAanleverToken boven de applicatie klasse worden geplaatst. Wil je de autorisatie
tijdelijk uitzetten, bijvoorbeeld voor testen of het lokaal draaien van de applicatie kan de volgende configuratie in
de application.properties worden opgenomen.

```properties
aanleveren.security.enabled=false
```

## Aanleveren Models
In de aanleveren models zijn modellen opgenomen die in meerdere services en projecten worden hergebruikt. 
