import { fireEvent, render, waitFor } from "@testing-library/react";
import { TooltipAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the TooltipAtom", () => {
  const { getByText } = render(
    <TooltipAtom onMouseEnter={() => {}} onMouseLeave={() => {}}>
      tooltip atom
    </TooltipAtom>
  );

  const tooltip = getByText(/tooltip atom/i);

  expect(tooltip).toBeInTheDocument();
});

it("Has the class tooltip", () => {
  const { getByText } = render(
    <TooltipAtom onMouseEnter={() => {}} onMouseLeave={() => {}}>
      tooltip atom
    </TooltipAtom>
  );
  const tooltip = getByText(/tooltip atom/i);
  expect(tooltip).toHaveClass("tooltip");
});

it("Renders the child of TooltipAtom", () => {
  const { getByText } = render(
    <TooltipAtom onMouseEnter={() => {}} onMouseLeave={() => {}}>
      tooltip atom
    </TooltipAtom>
  );
  const tooltip = getByText(/tooltip atom/i);
  expect(tooltip?.parentElement?.childElementCount).toBe(1);
  expect(tooltip.hasChildNodes()).toBe(true);

  expect(tooltip).toBeInTheDocument();
});

it("onMouseEnter the TextInputAtom", async () => {
  const func = jest.fn();
  const { getByText } = render(
    <TooltipAtom onMouseEnter={func} onMouseLeave={() => {}}>
      tooltip atom
    </TooltipAtom>
  );

  const tooltip = getByText(/tooltip atom/i);

  await waitFor(() => fireEvent.mouseEnter(tooltip));
  expect(func).toHaveBeenCalled();
});

it("onMouseLeave the TextInputAtom", async () => {
  const func = jest.fn();
  const { getByText } = render(
    <TooltipAtom onMouseEnter={() => {}} onMouseLeave={func}>
      tooltip atom
    </TooltipAtom>
  );

  const tooltip = getByText(/tooltip atom/i);

  await waitFor(() => fireEvent.mouseLeave(tooltip));
  expect(func).toHaveBeenCalled();
});
