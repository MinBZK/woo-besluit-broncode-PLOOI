import { fireEvent, getByAltText, render, screen } from "@testing-library/react";
import { LogoAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  alt: "Logo",
  size: "large",
  src: "https://open.overheid.nl/cb-common/2.0.0/images/logo.svg",
  className: "",
};

it("Renders the LogoAtom", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"icon"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toBeInTheDocument();
});

it("Has the class logo__icon", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"icon"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toHaveClass("logo__icon");
});

it("Has the class logo__small", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"small"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toHaveClass("logo__small");
});

it("Has the class logo__medium", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"medium"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toHaveClass("logo__medium");
});

it("Has the class logo__large", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"large"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toHaveClass("logo__large");
});

it("Has the class test", () => {
  const { getByRole } = render(<LogoAtom {...props} size={"icon"} className={"test"} />);

  const logo = getByRole("img", {
    name: /logo/i,
  });
  expect(logo).toHaveClass("test");
});

it("Has the src", () => {
  const { getByAltText } = render(<LogoAtom {...props} size={"icon"} />);

  const logo = getByAltText(props.alt);
  expect(logo).toHaveAttribute('src', props.src)
});


