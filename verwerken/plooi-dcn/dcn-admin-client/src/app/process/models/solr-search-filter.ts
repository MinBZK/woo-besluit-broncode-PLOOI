import {DateRangeField} from "./date-range-field";
import {SimpleField} from "../../search-document/models/simple-field";
import {OptionField} from "../../search-document/models/option-field";

export class SolrSearchFilter {
    query: string;
    textFields   : SimpleField [] = [];
    filterFields : OptionField [] = [];
    dateRangeField : DateRangeField;


}