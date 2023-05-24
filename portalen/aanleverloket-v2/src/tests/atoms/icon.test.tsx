import { fireEvent, render, screen } from "@testing-library/react";
import { IconAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  icon: "con-arrow-up",
  size: "xxLarge",
  alt: "icon",
};

it("Renders the IconAtom", () => {
  const { getByRole } = render(<IconAtom {...props} size={"xxLarge"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).toBeInTheDocument();
});

it("Has the class icon", () => {
  const { getByRole } = render(<IconAtom {...props} size={"xxLarge"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).toHaveClass("icon");
});

it("Has the class icon--small", () => {
  const { getByRole } = render(<IconAtom {...props} size={"small"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).toHaveClass("icon--small");
  expect(icon).not.toHaveClass("icon--medium");
  expect(icon).not.toHaveClass("icon--large");
  expect(icon).not.toHaveClass("icon--xLarge");
  expect(icon).not.toHaveClass("icon--xxLarge");
});

it("Has the class icon--medium", () => {
  const { getByRole } = render(<IconAtom {...props} size={"medium"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).not.toHaveClass("icon--small");
  expect(icon).toHaveClass("icon--medium");
  expect(icon).not.toHaveClass("icon--large");
  expect(icon).not.toHaveClass("icon--xLarge");
  expect(icon).not.toHaveClass("icon--xxLarge");
});

it("Has the class icon--large", () => {
  const { getByRole } = render(<IconAtom {...props} size={"large"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).not.toHaveClass("icon--small");
  expect(icon).not.toHaveClass("icon--medium");
  expect(icon).toHaveClass("icon--large");
  expect(icon).not.toHaveClass("icon--xLarge");
  expect(icon).not.toHaveClass("icon--xxLarge");
});

it("Has the class icon--xLarge", () => {
  const { getByRole } = render(<IconAtom {...props} size={"xLarge"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).not.toHaveClass("icon--small");
  expect(icon).not.toHaveClass("icon--medium");
  expect(icon).not.toHaveClass("icon--large");
  expect(icon).toHaveClass("icon--xLarge");
  expect(icon).not.toHaveClass("icon--xxLarge");
});

it("Has the class icon--xxLarge", () => {
  const { getByRole } = render(<IconAtom {...props} size={"xxLarge"} />);

  const icon = getByRole("img", {
    name: /icon/i,
  });
  expect(icon).not.toHaveClass("icon--small");
  expect(icon).not.toHaveClass("icon--medium");
  expect(icon).not.toHaveClass("icon--large");
  expect(icon).not.toHaveClass("icon--xLarge");
  expect(icon).toHaveClass("icon--xxLarge");
});
