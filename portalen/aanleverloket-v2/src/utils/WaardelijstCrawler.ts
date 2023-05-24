const col = require("../assets/waardelijsten/rwc_caribische_openbare_lichamen_compleet_1.json");
const gemeenten = require("../assets/waardelijsten/rwc_gemeenten_compleet_4.json");
const ministeries = require("../assets/waardelijsten/rwc_ministeries_compleet_1.json");
const overige = require("../assets/waardelijsten/rwc_overige_overheidsorganisaties_compleet_3.json");
const provincies = require("../assets/waardelijsten/rwc_provincies_compleet_1.json");
const waterschappen = require("../assets/waardelijsten/rwc_waterschappen_compleet_1.json");

export class WaardelijstCrawler {

    public static searchOrganisationLabel = (organisationCode: string) => {
        const waardelijst = this.findWaardelijst(organisationCode);
        return this.searchWaardelijst(waardelijst, organisationCode);
    }

    private static findWaardelijst = (organisationCode: string) => {
        switch (organisationCode.replace(/[0-9]/g, '')) {
            case "col": return col;
            case "gm": return gemeenten;
            case "mnre": return ministeries;
            case "oorg": return overige;
            case "pv": return provincies;
            case "ws": return waterschappen;
            default: return null;
        }
    }

    private static searchWaardelijst = (waardelijst: any, organisationCode: string) => {
        const organisation = waardelijst.find((org:any) => org["@id"].includes(organisationCode));

        const id = organisation["@id"];
        const label = organisation["http://www.w3.org/2000/01/rdf-schema#label"][0]["@value"];

        return {id, label};
    }
}