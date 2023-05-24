import json

#tooi = open('../resources/tooi/ccw_plooi_documentsoorten_portaal_1.json')
#tooi = open('../resources/tooi/ccw_plooi_documentsoorten_aanlevering_1.json')
tooi = open('../resources/tooi/scw_toplijst_1.json')
concepts = json.load(tooi)
tooi.close()

#mapping = open('../../../../dcn-modules/rijksoverheidnl/src/main/resources/nl/overheid/koop/plooi/dcn/ronl/documentsoorten_mapping.json')
#mapping = open('../../../../dcn-modules/aanleverloket/src/main/resources/nl/overheid/koop/plooi/dcn/aanleverloket/documentsoorten_mapping.json')
mapping = open('../../../../dcn-modules/rijksoverheidnl/src/main/resources/nl/overheid/koop/plooi/dcn/ronl/toplijst_mapping.json')
mappings = json.load(mapping)
mapping.close()

conceptmap = {}
for concept in concepts:
    terms = []
    if "http://www.w3.org/2004/02/skos/core#prefLabel" in concept:
        for term in concept['http://www.w3.org/2004/02/skos/core#prefLabel']:
            terms.append(term['@value'])
    if "http://www.w3.org/2004/02/skos/core#altLabel" in concept:
        for term in concept['http://www.w3.org/2004/02/skos/core#altLabel']:
            terms.append(term['@value'])
    conceptmap[concept['@id']]=terms

for mapped in mappings:
    if mapped['@id'] in conceptmap:
        print("id: " + mapped['@id'])
        if "http://www.w3.org/2004/02/skos/core#altLabel" in mapped:
            for alt in mapped['http://www.w3.org/2004/02/skos/core#altLabel']:
                if alt['@value'] in conceptmap[mapped['@id']]:
                    print("   " + mapped['@id'] + " has known term " + alt['@value'])
    else:
        print("ERROR: id not in PLOOI: " + mapped['@id'])
