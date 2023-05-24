import { fireEvent, render, screen } from "@testing-library/react";
import { ListAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  unstyled: false,
  linked: false,
};

it("Renders the ListAtom", () => {
  const { getByRole } = render(<ListAtom {...props}>Lege lijst</ListAtom>);

  const list = getByRole("list");
  expect(list).toBeInTheDocument();
});

it("Renders the child of ListAtom", () => {
  const { getByRole, getByText } = render(<ListAtom {...props}> <p>child</p> </ListAtom>);

  const list = getByRole('list');
  expect(list?.childElementCount).toBe(1);
  expect(list.hasChildNodes()).toBe(true);

  const child = getByText("child");
  expect(child).toBeInTheDocument();
});

it('Has the class list', () => {
   const {getByRole} = render(<ListAtom {...props} />);

   const list = getByRole("list");
   expect(list).toHaveClass('list');
});

it('Has the class list--linked', () => {
   const {getByRole} = render(<ListAtom {...props} linked />);

   const list = getByRole("list");
   expect(list).toHaveClass('list--linked');
});

it('Has the class list--unstyled', () => {
   const {getByRole} = render(<ListAtom {...props} unstyled />);

   const list = getByRole("list");
   expect(list).toHaveClass('list--unstyled');
});
