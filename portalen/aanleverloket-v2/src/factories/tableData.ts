import { SearchResults } from "../models/search-result";

export class TableDataFactory {
  public create = (
    data: SearchResults
  ): string[][] => {
    return data._embedded.resultaten.map((result, index) => [
      `${index + 1}`,
      result.officieleTitel,
      "-",
      "-",
      "-",
      "-",
      result._links.metadata.href,
    ]);
  };
}
