import { render } from "@testing-library/react";
import { ButtonRemoveMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";


it("Renders the ButtonRemoveMolecule", () => {
  const { getByRole } = render(<ButtonRemoveMolecule id={""} onClick={()=>{} }/>);

  const form = getByRole('button', {
    name: /verwijderen/i
  })
  expect(form).toBeInTheDocument();
});

it("Renders child ButtonRemoveMolecule", () => {
  const { getByRole } = render(<ButtonRemoveMolecule id={""} onClick={()=>{} }/>);

  const form = getByRole('button', {
    name: /verwijderen/i
  })
  expect(form.parentElement?.childElementCount).toBe(1);
  expect(form.hasChildNodes()).toBe(true);
  expect(form).toBeInTheDocument();
});
