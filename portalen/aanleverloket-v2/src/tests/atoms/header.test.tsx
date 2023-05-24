import { fireEvent, getByText, render, screen } from "@testing-library/react";
import { HeaderAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the HeaderAtom", () => {
  const { getByRole } = render(<HeaderAtom>test</HeaderAtom>);

  const text = getByRole('banner')
  expect(text.parentNode).toBeInTheDocument();
});
// it("Renders the HeaderAtom", () => {
//   const { getByText } = render(<HeaderAtom>test</HeaderAtom>);

//   const text = getByText("test");
//   expect(text.parentNode).toBeInTheDocument();
// });

it("Has the class HeaderAtom", () => {
  const { getByRole } = render(<HeaderAtom>test</HeaderAtom>);

  const text = getByRole('banner')
  expect(text).toHaveClass("header");
});

it("Renders the HeaderAtom child", async () => {
  const { getByRole } = render(<HeaderAtom>test</HeaderAtom>);

  const text = getByRole('banner')
  expect(text?.parentElement?.childElementCount).toBe(1);
  expect(text.hasChildNodes()).toBe(true);
});
