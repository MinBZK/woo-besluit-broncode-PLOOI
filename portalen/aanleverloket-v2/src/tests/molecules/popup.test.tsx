import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { PopupMolecule } from "../../ui/molecules/popup";


const Props = {
  title: "title",
  text: "popup text",
  cancelText: "Cancel",
  // okeText: "Oke",
  cancelButton: () => {},
  okeButton: () => {},
  extraInfo: "extra info",
}

it("Renders the PopupMolecule", () => {
  const { getByRole } = render(<PopupMolecule {...Props}/>);

  const title = getByRole('heading', {
    name: /title/i
  })
  expect(title.parentElement?.parentElement?.parentElement).toBeInTheDocument();
});

it("okeText is default oke", () => {
  const { getByRole } = render(<PopupMolecule {...Props}/>);

  const okeText = getByRole('button', {
    name: /oke/i
  })
  expect(okeText.textContent).toBe("Oke");
});

