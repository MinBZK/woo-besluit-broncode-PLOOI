import { render } from "@testing-library/react";
import { SmallAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  text: "Small tekst",
};

it("Renders the SmallAtom", () => {
  const { getByText } = render(<SmallAtom {...props} />);

  const small = getByText(/small tekst/i);
  expect(small).toBeInTheDocument();
});

it('Has the class small', () => {
   const { getByText } = render(<SmallAtom {...props} />);

   const small =  getByText(/small tekst/i);
   expect(small).toHaveClass('small');
});