import { fireEvent, render, screen } from "@testing-library/react";
import { DividerAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the DividerAtom", () => {
  const { container } = render(<DividerAtom type={"primary"} />);
  expect(container).toBeInTheDocument()
});

it("Has the class divider", () => {
  const { container } = render(<DividerAtom type={"primary"} />);

  expect(container.firstChild).toHaveClass("divider");
});

it("Has the class divider--verticaal", () => {
  const { container } = render(<DividerAtom type={"primary"} verticaal/>);

  expect(container.firstChild).toHaveClass("divider--verticaal");
});

it("Has the class divider--primary", () => {
  const { container } = render(<DividerAtom type={"primary"} />);

  expect(container.firstChild).toHaveClass("divider--primary");
});

it("Has NOT the class divider--primary", () => {
  const { container } = render(<DividerAtom />);

  expect(container.firstChild).not.toHaveClass("divider--primary");
});
