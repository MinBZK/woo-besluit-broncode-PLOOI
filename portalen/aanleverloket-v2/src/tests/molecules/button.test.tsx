import { fireEvent, render, waitFor } from "@testing-library/react";
import { ButtonMolecule, IconButtonMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";

const props = {
  onClick: () => {},
  disabled: false,
  loading: false,
  // type: "default" | "primary" | "orange",
  text: "buttonText",
  title: "button"
};

it("Renders the ButtonMolecule default", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"default"} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("Renders the ButtonMolecule primary", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"primary"} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("Renders the ButtonMolecule orange", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"orange"} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("Has the class button", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"default"} />);

  const button = getByRole("button");
  expect(button).toHaveClass("button");
});

it("Has the class button__content", () => {
  const { getByText } = render(<ButtonMolecule id={""} {...props} type={"default"} text="Verwijderen" />);

  const button = getByText(/verwijderen/i);
   
  expect(button.parentElement).toHaveClass("button__content");
});

it("onClick the ButtonMolecule", async () => {
  const func = jest.fn();
  const { getByRole } = render(
    <ButtonMolecule id={""} {...props} type={"default"} onClick={func} />
  );

  const button = getByRole("button");
  await waitFor(() => fireEvent.click(button));
  expect(func).toHaveBeenCalled();
});

it("Renders the child of ButtonMolecule", () => {
  const { getByRole } = render(
    <ButtonMolecule id={""} {...props} type={"default"} loading />
  );

  const button = getByRole("button");
  expect(button?.childElementCount).toBe(2);
  expect(button.hasChildNodes()).toBe(true);
});

it("Renders the IconButtonMoleculeRTL", () => {
  const { getByRole } = render(<IconButtonMolecule rtl icon={"icon-cross-blue"} id={""} {...props} type={"default"} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("Renders the IconButtonMoleculeRTL icon", () => {
  const { getByRole } = render(<IconButtonMolecule rtl icon={"icon-cross-blue"} id={""} {...props} type={"default"} />);

  const icon = getByRole('img', {
    name: /icon\-cross\-blue/i
  })
  expect(icon).toBeInTheDocument();
});

it("Renders the IconButtonMoleculeRTL text", () => {
  const { getByText } = render(<IconButtonMolecule rtl icon={"icon-cross-blue"} id={""} {...props} type={"default"}  text={"Verwijderen"}/>);

  const text = getByText(/verwijderen/i)
  expect(text).toBeInTheDocument();
});


it("IconButtonMoleculeRTL has the class button__content--small", () => {
  const { getByText } = render(<IconButtonMolecule rtl icon={"icon-cross-blue"} id={""} {...props} type={"default"} size={"small"} text={"Verwijderen"} />);

  const button = getByText(/verwijderen/i);
  expect(button.parentElement).toHaveClass("button__content--small");
});

it("Renders the IconButtonMolecule", () => {
  const { getByRole } = render(<IconButtonMolecule icon={"icon-cross-blue"} id={""} {...props} type={"default"} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("IconButtonMolecule has the class button__content--small", () => {
  const { getByText } = render(<IconButtonMolecule icon={"icon-cross-blue"} id={""} {...props} type={"default"} size={"small"} text={"Verwijderen"} />);

  const button = getByText(/verwijderen/i);
  expect(button.parentElement).toHaveClass("button__content--small");
});

it("Renders the ButtonMolecule overlay to have class ", () => {
  const { getByRole } = render(<IconButtonMolecule rtl icon={"icon-cross-blue"} id={""} {...props} type={"default"} text={"Verwijderen"} loading={true} />);

  const button = getByRole("button");
  expect(button.childNodes[0]).toHaveClass("button__spinner__container");
});

it("Renders the ButtonMolecule overlay to have class ", () => {
  const { getByRole } = render(<IconButtonMolecule icon={"icon-cross-blue"} id={""} {...props} type={"default"} text={"Verwijderen"} loading={true} />);

  const button = getByRole("button");
  expect(button.childNodes[0]).toHaveClass("button__spinner__container");
});

it("Renders the ButtonMolecule overlay to have class ", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"default"} loading={true} />);

  const button = getByRole("button");
  expect(button.childNodes[0]).toHaveClass("button__spinner__container");
});

it("Renders the ButtonMolecule overlay to have class button__spinner__container__primary", () => {
  const { getByRole } = render(<ButtonMolecule id={""} {...props} type={"primary"} loading={true} />);

  const button = getByRole("button");
  expect(button.childNodes[0]).toHaveClass("button__spinner__container__primary");
});
