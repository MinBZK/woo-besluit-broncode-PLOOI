
# plooi-solr-config

Bevat de PLOOI Solr configuratie en bouwt een container die deze deployed naar de PLOOI collectie in Solr Cloud.

De container kan ook gebruikt worden om een lokale Solr Cloud te configureren:

```
docker build -t plooi-solr-config -t SSSSSSSSSSSSSSSSSS/koop-plooi/plooi-solr-config:latest .
docker run --network host plooi-solr-config
```

De container herkent 3 environment variabelen:
* ZK_HOST; hostname van een Zookeer node, default "localhost:9983"
* SOLR_HOST; hostname van een Solr node, default "localhost:8983"
* SOLR_COLLECTION; de naam van de collectie in Solr, default "plooi"

Voorbeeld om een collectie "plooi_2" te maken;

```
docker run --network host --env SOLR_COLLECTION=plooi_2 plooi-solr-config
```

Naast de collectie creeert de container ook een alias genaamd "plooi" naar deze collectie (als de alias niet al
bestaat).

Voor deployment in Kubernetes bevat deze module een helm chart met default values voor het Standaard Platform.
Het bestand `solr-local-microk8s.yaml` bevat values voor een lokale micork8s deployment.

## Collectie schaling

Deze module maakt een plooi collectie met een enkele shard en een replica. Voor productie is niet niet afdoende
en dus moeten we handmatig meerdere shards en replica's toevoegen.


Split de shard in tweeen (Controleer eerst of de shard naam hieronder correct is en pas zonodig de collectie
naam aan):

```
http://SSSSSSSSSSSSSSSSSS/solr/admin/collections?action=SPLITSHARD&collection=plooi_2&shard=shard1
```

Creeer daarna, indien het cluster op meerdere nodes draait, replicas voor beide shards. Dit kan eenvoudig via de
[Solr admin console](http://SSSSSSSSSSSSSSSSSS/solr/#/~collections), maar het kan ook met de
[Solr Collections API](https://solr.apache.org/guide/solr/latest/deployment-guide/replica-management.html#addreplica).
Zie de README in plooi-solr-deployment voor een voorbeeld.
