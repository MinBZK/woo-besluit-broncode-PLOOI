import { fireEvent, render, screen } from "@testing-library/react";
import { FooterAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the FooterAtom", () => {
  const { getByRole } = render(<FooterAtom />);

  const footer = getByRole("contentinfo");
  expect(footer).toBeInTheDocument();
});

it("Renders the FooterAtom child", () => {
  const { getByRole } = render(<FooterAtom ><p>test</p></FooterAtom>);

  const footer = getByRole("contentinfo");
  expect(footer.childElementCount).toBe(1);
  expect(footer.hasChildNodes()).toBe(true);
});

it('Has the class footer', () => {
   const {getByRole} = render(<FooterAtom />);

   const footer = getByRole("contentinfo");
   expect(footer).toHaveClass('footer');
});