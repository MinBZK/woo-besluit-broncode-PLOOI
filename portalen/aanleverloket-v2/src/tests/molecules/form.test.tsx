import { render } from "@testing-library/react";
import { ButtonMolecule, FormMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";


it("Renders the FormMolecule", () => {
  const { getByText } = render(<FormMolecule ><p>Form</p></FormMolecule>);

  const form = getByText(/form/i) 
  expect(form).toBeInTheDocument();
});

it("Renders child FormMolecule", () => {
  const { getByText } = render(<FormMolecule ><p>Form</p></FormMolecule>);

  const form = getByText(/form/i) 
  expect(form.parentElement?.childElementCount).toBe(1);
  expect(form.hasChildNodes()).toBe(true);
  expect(form).toBeInTheDocument();
});

it("form button container has class form__button_container", () => {
  const bl = <ButtonMolecule id='rightButton' title="button" onClick={() => { }} text={"Previous"} type={"default"} />
  const br = <ButtonMolecule id='leftButton' title="button" onClick={() => { }} text={"Complete"} type={"primary"} />

  const { getByRole } = render(<FormMolecule leftButton={bl} rightButton={br}><p>Form</p></FormMolecule>);

  const buttonRight = getByRole('button', { name: /complete/i });
   
  expect(buttonRight.parentElement).toHaveClass("form__button_container");
});