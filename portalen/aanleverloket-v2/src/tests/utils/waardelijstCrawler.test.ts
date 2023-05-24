import {WaardelijstCrawler} from "../../utils/WaardelijstCrawler";


it.each([
    ["col01", {"id": "https://identifier.overheid.nl/tooi/id/col/col01", "label": "openbaar lichaam Bonaire"}],
    ["gm0285", {"id": "https://identifier.overheid.nl/tooi/id/gemeente/gm0285", "label": "gemeente Voorst"}],
    ["mnre1018", {"id": "https://identifier.overheid.nl/tooi/id/ministerie/mnre1018", "label": "ministerie van Defensie"}],
    ["oorg10007", {"id": "https://identifier.overheid.nl/tooi/id/oorg/oorg10007", "label": "ProRail"}],
    ["pv23", {"id": "https://identifier.overheid.nl/tooi/id/provincie/pv23", "label": "provincie Overijssel"}],
    ["ws0652", {"id": "https://identifier.overheid.nl/tooi/id/waterschap/ws0652", "label": "waterschap Brabantse Delta"}],
    ["qwe", null]
])('Finds a corresponding label from an organisation code', (organisationCode, expectedLabel) => {
    const org = WaardelijstCrawler.searchOrganisationLabel(organisationCode);

    expect(org).toStrictEqual(expectedLabel);
});