import { fireEvent, render } from "@testing-library/react";
import { ChipAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  text: "Chip",
  onRemove: () => {},
};

it("Renders the ChipAtom", () => {
  const { getByRole } = render(<ChipAtom {...props} />);

  const button = getByRole("button");
  expect(button).toBeInTheDocument();
});

it("Renders the Label", () => {
  const labelText = "RANDOM-TEXT";
  const { getByText } = render(<ChipAtom {...props} text={labelText} />);
  const button = getByText(labelText);
  expect(button).toBeInTheDocument();
});

it("Has the class chip", () => {
  const { getByRole } = render(<ChipAtom {...props} />);

  const chip = getByRole("button");
  expect(chip.parentElement).toHaveClass("chip");
});

it("Clicks the ChipAtom", () => {
  const testObject = { counter: 0 };
  const { getByRole } = render(
    <ChipAtom {...props} onRemove={() => testObject.counter++} />
  );

  const button = getByRole("button");
  fireEvent.click(button);
  expect(testObject.counter).toBe(1);
});

it("Has the class button", () => {
  const { getByRole } = render(<ChipAtom {...props} />);

  const button = getByRole("button");
  expect(button).toHaveClass("chip__button");
});

