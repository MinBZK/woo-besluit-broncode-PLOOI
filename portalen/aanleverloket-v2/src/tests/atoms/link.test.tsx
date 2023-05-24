import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { LinkAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  href: "https://google.com",
  text: "Naar google",
  lang: "en",
  newWindow: true,
  selected: false,
};

it("Renders the LinkAtom", () => {
  const { getByRole } = render(<LinkAtom {...props} />);

  const link = getByRole("link", {
    name: /naar google/i,
  });
  expect(link).toBeInTheDocument();
});

it("Has the class LinkAtom", () => {
  const { getByRole } = render(<LinkAtom {...props} />);

  const link = getByRole("link", {
    name: /naar google/i,
  });
  expect(link).toHaveClass("link");
});

it("has attribute href", () => {
  const { getByRole } = render(<LinkAtom {...props} />);

  const link = getByRole("link", {
    name: /naar google/i,
  });
  expect(link.closest("a")).toHaveAttribute("href", "https://google.com");
});
