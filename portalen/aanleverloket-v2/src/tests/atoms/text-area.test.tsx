import { fireEvent, render, waitFor } from "@testing-library/react";
import { TextAreaAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  placeholder: "placeholder",
  rows: 2,
  readonly: false,
  id: "testId",
  value: "startValue",

//   onChange: (text: string) => {},
};

it("Renders the TextAreaAtom", () => {
  const { getByRole } = render(<TextAreaAtom {...props} />);

  const textArea = getByRole("textbox");
  expect(textArea).toBeInTheDocument();
});

it("Has the class textarea", () => {
  const { getByRole } = render(<TextAreaAtom {...props} />);

  const textArea = getByRole("textbox");
  expect(textArea).toHaveClass("textarea");
});

it('Has attribute value', () => {
   const {getByRole} = render(<TextAreaAtom {...props} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveProperty('value', "startValue");
});

it('Has attribute placeholder', () => {
   const {getByRole} = render(<TextAreaAtom {...props} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveAttribute('placeholder', "placeholder");
});

it('Has attribute id', () => {
   const {getByRole} = render(<TextAreaAtom {...props} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveAttribute('id', "testId");
});

it('Has attribute rows', () => {
   const {getByRole} = render(<TextAreaAtom {...props} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveProperty('rows', 2);
});

it('Has attribute rows', () => {
   const {getByRole} = render(<TextAreaAtom {...props} rows={undefined} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveProperty('rows', 5);
});

it('Has attribute readOnly', () => {
   const {getByRole} = render(<TextAreaAtom {...props} />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveProperty('readOnly', false);
});

it('Has attribute readOnly', () => {
   const {getByRole} = render(<TextAreaAtom {...props} readonly />);

   const textArea = getByRole("textbox");
   expect(textArea).toHaveProperty('readOnly', true);
});

it("onChange the TextAreaAtom", async () => {
  const func = jest.fn();
  const { getByRole } = render(<TextAreaAtom {...props} value={"startValue"} onChange={func} />);

  const textArea = getByRole("textbox");
//   expect(textArea).toHaveProperty("value", "startValue");

  await waitFor(() =>
    fireEvent.change(textArea, {
      target: { value: "newValue" },
    })
  );
  expect(func).toHaveBeenCalledWith("newValue");
});

it("onChange the TextAreaAtom without onchange param", async () => {
  const func = jest.fn();
  const { getByRole } = render(<TextAreaAtom {...props} value={"startValue"} />);

  const textArea = getByRole("textbox");
//   expect(textArea).toHaveProperty("value", "startValue");

  await waitFor(() =>
    fireEvent.change(textArea, {
      target: { value: "newValue" },
    })
  );
  expect(func).not.toHaveBeenCalledWith("newValue");
});
