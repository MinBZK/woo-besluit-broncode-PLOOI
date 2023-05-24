import { render } from "@testing-library/react";
import { TextAreaMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";

const props = {
  value: "",
  onChange: (text: string) => {},
  tooltip: "Tooltip",
  id: 'area-id',
  label: 'Label',
  placeholder: 'Placeholder',
  required: true
};

it("Renders the TextAreaMolecule", () => {
  const { getByRole } = render(<TextAreaMolecule {...props} />);

  const textbox = getByRole('textbox', { name: /label/i })
  expect(textbox).toBeInTheDocument();
});

it("Renders the TextAreaMolecule undifined label", () => {
  const { getByRole } = render(<TextAreaMolecule {...props} label={undefined} />);

  const textbox = getByRole('textbox')
  expect(textbox).toBeInTheDocument();
});
