import { fireEvent, render, waitFor } from "@testing-library/react";
import {
  EmailInputMolecule,
  PasswordInputMolecule,
  TextInputMolecule,
  DateInputMolecule,
} from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";
import { RequiredValidator } from "../../validations/requiredValidator";

const props = {
  id: "test",
  placeholder: "placeholder",
  onChange: (value: string) => {},
  onEnter: () => {},
  //   value?: string;
  label: "Email",
  tooltip: "Tooltip",
  required: true,
  disabled: false,
  validations: [new RequiredValidator()]
};
// const inputValidations = [new RequiredValidator()];

it("Renders the EmailInputMolecule", () => {
  const { getByRole } = render(<EmailInputMolecule {...props} />);

  const input = getByRole("textbox", { name: /email/i });
  expect(input).toBeInTheDocument();
});

it("Renders the EmailInputMolecule with value", () => {
  const { getByRole } = render(
    <EmailInputMolecule {...props} value={"test"} />
  );

  const input = getByRole("textbox", { name: /email/i });
  expect(input).toBeInTheDocument();
});

it("Renders the EmailInputMolecule no label", () => {
  const { getByRole } = render(
    <EmailInputMolecule {...props} label={undefined} />
  );

  const input = getByRole("textbox");
  expect(input).toBeInTheDocument();
});

it("Renders the PasswordInputMolecule", () => {
  const { getByLabelText } = render(
    <PasswordInputMolecule {...props} label={"password"} />
  );

  const input = getByLabelText(/password/i);
  expect(input).toBeInTheDocument();
});

it("Renders the TextInputMolecule", () => {
  const { getByRole } = render(<TextInputMolecule {...props} label={"text"} />);

  const input = getByRole("textbox", { name: /text/i });
  expect(input).toBeInTheDocument();
});

it("Renders the DateInputMolecule", () => {
  const { getByLabelText } = render(
    <DateInputMolecule {...props} label={"date"} />
  );

  const input = getByLabelText(/date/i);
  expect(input).toBeInTheDocument();
});

it("OnBlur TextInputMolecule", async () => {
  const { getByRole, getByText } = render(
    <TextInputMolecule {...props} label={"text"} value={undefined} />
  );

  const input = getByRole("textbox");

  fireEvent.focus(input);
  await waitFor(() => fireEvent.blur(input));
  const warning = getByText(/Dit is een verplicht veld/i);

  expect(warning).toBeInTheDocument();
});

it("OnBlur TextInputMolecule with value is blank", async () => {
  const { getByRole, getByText } = render(
    <TextInputMolecule {...props} label={"text"} value={""} />
  );

  const input = getByRole("textbox");

  fireEvent.focus(input);
  await waitFor(() => fireEvent.blur(input));
  const warning = getByText(/Dit is een verplicht veld/i);

  expect(warning).toBeInTheDocument();
});
