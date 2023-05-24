import { render } from "@testing-library/react";
import { ToastAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the ToastAtom", () => {
  const { getByText } = render(
    <ToastAtom type={"info"}>child</ToastAtom>
  );

  const toast = getByText(/child/i);
  expect(toast).toBeInTheDocument();
});

it("Renders the child of ToastAtom", () => {
  const { getByText } = render(
    <ToastAtom type={"info"}>
      {" "}
      <p>child</p>{" "}
    </ToastAtom>
  );

  const child = getByText("child");
  expect(child?.parentElement?.childElementCount).toBe(1);
  expect(child.hasChildNodes()).toBe(true);

  expect(child).toBeInTheDocument();
});

it("Has the class toaster", () => {
  const { getByText } = render(
    <ToastAtom type={"info"}>Some Message</ToastAtom>
  );

  const toast = getByText(/some message/i);
  expect(toast).toHaveClass("toaster");
});

it("Has the class toaster--info", () => {
  const { getByText } = render(
    <ToastAtom type={"info"}>Some Message</ToastAtom>
  );

  const toast = getByText(/some message/i);
  expect(toast).toHaveClass("toaster--info");
});

it("Has the class toaster--error", () => {
  const { getByText } = render(
    <ToastAtom type={"error"}>Some Message</ToastAtom>
  );

  const toast = getByText(/some message/i);
  expect(toast).toHaveClass("toaster--error");
});

it("Has the class toaster--success", () => {
  const { getByText } = render(
    <ToastAtom type={"success"}>Some Message</ToastAtom>
  );

  const toast = getByText(/some message/i);
  expect(toast).toHaveClass("toaster--success");
});
