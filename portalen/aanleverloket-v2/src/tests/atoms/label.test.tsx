import { fireEvent, render, screen } from "@testing-library/react";
import { LabelAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  // children: string;
  // for?: string;
  type: "default",
  bold: false,
  underlined: false,
  italic: false,
};

it("Renders the LabelAtom", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"}>
      label tekst
    </LabelAtom>
  );

  const label = getByText(/label tekst/i);
  expect(label).toBeInTheDocument();
});

it("Renders the child of LabelAtom", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"}>
      label tekst
    </LabelAtom>
  );

  const label = getByText(/label tekst/i);
  expect(label?.parentElement?.childElementCount).toBe(1);
  expect(label.hasChildNodes()).toBe(true);
});

it("Has the class label", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).toHaveClass("label");
});

it("Has the class label--default", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--blue", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"blue"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--primary", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"primary"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--orange", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"orange"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--success", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"success"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--warning", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"warning"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--danger", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"danger"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--large", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"large"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).toHaveClass("label--large");
  expect(label).not.toHaveClass("label--form");
});

it("Has the class label--form", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"form"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--default");
  expect(label).not.toHaveClass("label--blue");
  expect(label).not.toHaveClass("label--primary");
  expect(label).not.toHaveClass("label--orange");
  expect(label).not.toHaveClass("label--success");
  expect(label).not.toHaveClass("label--warning");
  expect(label).not.toHaveClass("label--danger");
  expect(label).not.toHaveClass("label--large");
  expect(label).toHaveClass("label--form");
});

it("Has the class label--bold", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"} bold>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).toHaveClass("label--bold");
  expect(label).not.toHaveClass("label--underlined");
  expect(label).not.toHaveClass("label--italic");
});

it("Has the class label--bold", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"} underlined>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--bold");
  expect(label).toHaveClass("label--underlined");
  expect(label).not.toHaveClass("label--italic");
});

it("Has the class label--bold", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"} italic>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).not.toHaveClass("label--bold");
  expect(label).not.toHaveClass("label--underlined");
  expect(label).toHaveClass("label--italic");
});

it("Has the class label--bold label--underlined label--italic", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"} bold underlined italic>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).toHaveClass("label--bold");
  expect(label).toHaveClass("label--underlined");
  expect(label).toHaveClass("label--italic");
});

it("Has the class label--default", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"}>
      label tekst
    </LabelAtom>
  );
  const label = getByText(/label tekst/i);
  expect(label).toHaveClass("label--default");
  expect(label).not.toHaveClass("label--bold");
  expect(label).not.toHaveClass("label--underlined");
  expect(label).not.toHaveClass("label--italic");
});

it("Has multiple children", () => {
  const { getByText } = render(
    <LabelAtom {...props} type={"default"} >{[
      "text1", "text2"
    ]}
    </LabelAtom>
  );
  const text1 = getByText(/text1/i);
  expect(text1).toBeInTheDocument();
  const text2 = getByText(/text2/i);
  expect(text2).toBeInTheDocument();
});
