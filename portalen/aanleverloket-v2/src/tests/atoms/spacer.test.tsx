import { render } from "@testing-library/react";
import { SpacerAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

// it("Renders the SpacerAtom", () => {
//   const { getByRole } = render(<SpacerAtom space={1} />);

//   const spacer = getByRole("spacer");
//   expect(spacer).toBeInTheDocument();
// });

it("Has the class spacer__1", () => {
  const { getByTestId } = render(<SpacerAtom space={1} />);

  const spacer = getByTestId("spacer");
  expect(spacer).toHaveClass("spacer__1");
});

// it("Has the class spacer__2", () => {
//   const { getByRole } = render(<SpacerAtom space={2} />);

//   const spacer = getByRole("spacer");
//   expect(spacer).toHaveClass("spacer__2");
// });

// it("Has the class spacer__4", () => {
//   const { getByRole } = render(<SpacerAtom space={4} />);

//   const spacer = getByRole("spacer");
//   expect(spacer).toHaveClass("spacer__4");
// });

// it("Has the class spacer__8", () => {
//   const { getByRole } = render(<SpacerAtom space={8} />);

//   const spacer = getByRole("spacer");
//   expect(spacer).toHaveClass("spacer__8");
// });
