import { render } from "@testing-library/react";
import { IconLabelMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";


const Props = {
  icon: "icon-alert-red",
  label: "Ongeldig e-mailadres",
  // type: "default" | "alert";
}

it("Renders the IconLabelMolecule", () => {
  const { getByRole } = render(<IconLabelMolecule {...Props} type={"default"}/>);

  const image =  getByRole('img', { name: /icon\-alert\-red/i })
  expect(image.parentElement).toBeInTheDocument();
});

it("Renders image", () => {
  const { getByRole } = render(<IconLabelMolecule {...Props} type={"default"}/>);

  const image =  getByRole('img', { name: /icon\-alert\-red/i })
  expect(image).toBeInTheDocument();
});

it("Renders text", () => {
  const { getByText } = render(<IconLabelMolecule {...Props} type={"default"}/>);

  const text =  getByText(/ongeldig e\-mailadres/i)
  expect(text).toBeInTheDocument();
});

it("IconLabelMolecule has class container", () => {
  const { getByRole } = render(<IconLabelMolecule {...Props} type={"default"}/>);

  const image =  getByRole('img', { name: /icon\-alert\-red/i })
  expect(image.parentElement).toHaveClass("container");
});

it("IconLabelMolecule has class container-alert", () => {
  const { getByRole } = render(<IconLabelMolecule {...Props} type={"alert"}/>);

  const image =  getByRole('img', { name: /icon\-alert\-red/i })
  expect(image.parentElement).toHaveClass("container--alert");
});
