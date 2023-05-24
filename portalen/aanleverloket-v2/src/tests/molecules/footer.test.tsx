import { render } from "@testing-library/react";
import { FooterMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";

const menu = [
  {
    text: "Open Data",
    href: "https://data.overheid.nl",
  },
  {
    text: "Linked Data Overheid",
    href: "http://linkeddata.overheid.nl",
  },
  {
    text: "PUC Open Data",
    href: "https://puc.overheid.nl",
    external: true,
    lang: "NL",
    newWindow: true,
    selected: true,
  },
];

const props = {
  menus: [menu],
};

it("Renders the FooterMolecule", () => {
  const { getByRole } = render(<FooterMolecule {...props} />);

  const footer = getByRole("list");
  expect(footer).toBeInTheDocument();
});
