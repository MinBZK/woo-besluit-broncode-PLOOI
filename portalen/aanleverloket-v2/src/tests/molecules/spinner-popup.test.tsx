import { getByText, render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { SpinnerPopupMolecule } from "../../ui/molecules";


it("Renders the SpinnerPopupMolecule", () => {
  const { getByText } = render(<SpinnerPopupMolecule/>);

  const title = getByText(/Eén moment geduld, document wordt geüpload/i);
  expect(title.parentElement?.parentElement).toBeInTheDocument();
});

it("Renders the SpinnerPopupMolecule with custom tekst", () => {
  const { getByText } = render(<SpinnerPopupMolecule text="test" />);

  const title = getByText(/test/i);
  expect(title.parentElement?.parentElement).toBeInTheDocument();
});
