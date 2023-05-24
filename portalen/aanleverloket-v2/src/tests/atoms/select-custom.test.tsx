import { fireEvent, render, screen } from "@testing-library/react";
import { SelectCustomAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  id: "selectId",
  ariaLabel: "selectAriaLabel",
  options: ["Option 1", "Option 2", "Option 123456789"],
  disabled: false,
  selectedValue: "Option 1",
  onChange: () => {},
};

it("Renders the SelectCustomAtom", () => {
  const { getByRole } = render(<SelectCustomAtom {...props} />);

  const select = getByRole("combobox", {
    name: /select/i,
  });
  expect(select).toBeInTheDocument();
});

it("Has the class select", () => {
  const { getByRole } = render(<SelectCustomAtom {...props} />);

  const select = getByRole("combobox", {
    name: /select/i,
  });
  expect(select.parentElement).toHaveClass("select");
});

it("Has id", () => {
  const { getByRole } = render(<SelectCustomAtom {...props} />);

  const select = getByRole("combobox", {
    name: /select/i,
  });
  expect(select.id).toBe(props.id);
});

it("Has attribute name", () => {
  const { getByRole } = render(<SelectCustomAtom {...props} />);

  const select = getByRole("combobox", {
    name: /select/i,
  });
  expect(select).toHaveAttribute('name', props.id);
});

it("Has attribute aria-label", () => {
  const { getByRole } = render(<SelectCustomAtom {...props} />);

  const select = getByRole("combobox", {
    name: /select/i,
  });
  expect(select).toHaveAttribute('aria-label', props.ariaLabel);
});

it('Renders the disabled SelectCustomAtom', () => {
   const {getByRole} = render(<SelectCustomAtom {...props} disabled={true} />);

   const select = getByRole("combobox", {
    name: /select/i,
  });
   expect(select).toHaveProperty('disabled', true);
});

it('Renders the disabled SelectCustomAtom', () => {
   const {getByRole} = render(<SelectCustomAtom {...props} disabled={true} />);

   const select = getByRole("combobox", {
    name: /select/i,
  });
   expect(select).toHaveProperty('value', props.selectedValue);
});

it('Clicks the SelectCustomAtom', () => {
   const {getByRole} = render(<SelectCustomAtom {...props} onChange={() => props.selectedValue = props.options[1]} />);

   const select = getByRole("combobox", {
    name: /select/i,
  });
   fireEvent.change(select);
   expect(props.selectedValue).toBe(props.options[1]);
});