
# PLOOI IAM Service

De IAM service is de plek waar na succesvolle authenticatie een JWT wordt uitgegeven. 

## Aanvragen JWT

De IAM service heeft verschillende authenticatie manieren ingebouwd. Elk van deze manieren is hieronder beschreven:

### SAML

1. Om requests te kunnen uitvoere moet je zijn verbonden met 2 VPN's 
   1. Zet de volgende configuratie in je host file <br> ```10.55.70.26 cam-tst.kc-wetgeving.nl ```
2. Ga op test naar https://SSSSSSSSSSSSSSSSSS/token/saml
3. Voeg hier een query param aan toe waar de applicatie naar redirect na succesvolle authenticatie ?redirect-uri=<uri>
4. Er zijn 2 mogelijkheden om als redirect in te stellen (http://localhsot:3000 & https://SSSSSSSSSSSSSSSSSS). 
5. Klik op overige organisaties
6. Vul hier de credentials in.
7. Vervolgens authenticeert CAM het SAML request en stuurt de response door naar de authorization-server. Wanneer de SAML response correct is wordt een JWT gegenereerd.

## Valideren JWT

1. Een JWT kan worden gevalideerd door het endpoint /check_token te gebruiken
2. Voeg een Authorization header toe met de jwt als bearer token.
