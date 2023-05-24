import { fireEvent, render, waitFor } from "@testing-library/react";
import { TextInputAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  id: "testId",
  inputRef: (ref: HTMLInputElement) => {},
  placeholder: "placeholder",
  // onFocus?: (e: FocusEvent<HTMLInputElement, Element>) => {},
  // onBlur?: () => {},
  // onChange?: (value: string) => {},
  // onEnter?: () => {},
  // onEscape?: () => {},
  value: "testValue",
  // type: "text" | "email" | "password" | "numeric",
  disabled: false,
  error: false,
};

it("Renders the TextInputAtom", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"text"} />);

  const input = getByRole("textbox");
  expect(input).toBeInTheDocument();
});

it("Has the class input--text", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"text"} />);

  const input = getByRole("textbox");
  expect(input).toHaveClass("input--text");
});

it("Has the class input--alert", () => {
  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} error />
  );

  const input = getByRole("textbox");
  expect(input).toHaveClass("input--alert");
});

it("Has type text", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"text"} />);

  const input = getByRole("textbox");
  expect(input).toHaveAttribute("type", "text");
});

it("Has type email", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"email"} />);

  const input = getByRole("textbox");
  expect(input).toHaveAttribute("type", "email");
});

it("Has type password", () => {
  const { container } = render(<TextInputAtom {...props} type={"password"} />);

  const input = container.querySelector("#testId");
  expect(input).toHaveAttribute("type", "password");
});

it("Has type numeric", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"numeric"} />);

  const input = getByRole("textbox");
  expect(input).toHaveAttribute("type", "numeric");
});

it("onChange the TextInputAtom", async () => {
  const func = jest.fn();
  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} onChange={func} />
  );

  const input = getByRole("textbox");

  expect(input).toHaveAttribute("value", "testValue");
  await waitFor(() =>
    fireEvent.change(input, {
      target: { value: "newValue" },
    })
  );
  expect(func).toHaveBeenCalledWith("newValue");
});

it("onFocus the TextInputAtom", async () => {
  const func = jest.fn();
  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} onFocus={func} />
  );

  const input = getByRole("textbox");

  await waitFor(() => fireEvent.focus(input));
  expect(func).toHaveBeenCalled();
});

it("onBlur the TextInputAtom", async () => {
  const func = jest.fn();
  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} onBlur={func} />
  );

  const input = getByRole("textbox");

  await waitFor(() => fireEvent.blur(input));
  expect(func).toHaveBeenCalled();
});

it("onEnter the TextInputAtom", async () => {
  const func = jest.fn();

  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} onEnter={func} />
  );

  const input = getByRole("textbox");
  await waitFor(() => fireEvent.keyDown(input, { key: "Enter", keyCode: 13 }));
  expect(func).toHaveBeenCalled();
});

it("onEscape the TextInputAtom", async () => {
  const func = jest.fn();

  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} onEscape={func} />
  );

  const input = getByRole("textbox");
  await waitFor(() => fireEvent.keyDown(input, { key: "Escape", keyCode: 27 }));
  expect(func).toHaveBeenCalled();
});

it("Renders the disabled ", () => {
  const { getByRole } = render(
    <TextInputAtom {...props} type={"text"} disabled />
  );

  const input = getByRole("textbox");
  expect(input).toHaveProperty("disabled", true);
});

it("Has attribute name", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"text"} />);
  const input = getByRole("textbox");
  expect(input).toHaveAttribute("name", props.id);
});

it("Has attribute placeholder", () => {
  const { getByRole } = render(<TextInputAtom {...props} type={"text"} />);
  const input = getByRole("textbox");
  expect(input).toHaveAttribute("placeholder", props.placeholder);
});
