import { fireEvent, render, screen } from "@testing-library/react";
import {
  HeadingH1Atom,
  HeadingH2Atom,
  HeadingH3Atom,
  HeadingH4Atom,
  HeadingPreviewAtom,
} from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the HeadingH1Atom", () => {
  const { getByRole } = render(<HeadingH1Atom>heading h1</HeadingH1Atom>);

  const text = getByRole("heading", {
    name: /heading h1/i,
  });
  expect(text).toBeInTheDocument();
});

it("Renders the HeadingH2Atom", () => {
  const { getByRole } = render(<HeadingH2Atom>heading h2</HeadingH2Atom>);

  const text = getByRole("heading", {
    name: /heading h2/i,
  });
  expect(text).toBeInTheDocument();
});

it("Renders the HeadingH3Atom", () => {
  const { getByRole } = render(<HeadingH3Atom>heading h3</HeadingH3Atom>);

  const text = getByRole("heading", {
    name: /heading h3/i,
  });
  expect(text).toBeInTheDocument();
});

it("Renders the HeadingH4Atom", () => {
  const { getByRole } = render(<HeadingH4Atom>heading h4</HeadingH4Atom>);

  const text = getByRole("heading", {
    name: /heading h4/i,
  });
  expect(text).toBeInTheDocument();
});

it("Renders the HeadingPreviewAtom", () => {
  const { getByText } = render(
    <HeadingPreviewAtom>heading preview</HeadingPreviewAtom>
  );

  const text = getByText(/heading preview/i);
  expect(text).toBeInTheDocument();
});




it("Has the class heading 1", async () => {
  const { getByRole } = render(<HeadingH1Atom>heading h1</HeadingH1Atom>);

  const text = getByRole("heading", {
    name: /heading h1/i,
  });
  expect(text).toHaveClass("heading");
});

it("Has the class heading 2", async () => {
  const { getByRole } = render(<HeadingH2Atom>heading h2</HeadingH2Atom>);

  const text = getByRole("heading", {
    name: /heading h2/i,
  });
  expect(text).toHaveClass("heading");
});

it("Has the class heading 3", async () => {
  const { getByRole } = render(<HeadingH3Atom>heading h3</HeadingH3Atom>);

  const text = getByRole("heading", {
    name: /heading h3/i,
  });
  expect(text).toHaveClass("heading");
});

it("Has the class heading 4", async () => {
  const { getByRole } = render(<HeadingH4Atom>heading h4</HeadingH4Atom>);

  const text = getByRole("heading", {
    name: /heading h4/i,
  });
  expect(text).toHaveClass("heading");
});

it("Has the class heading preview", async () => {
  const { getByText } = render(<HeadingPreviewAtom>heading preview</HeadingPreviewAtom>);

  const text = getByText(/heading preview/i);
  expect(text).toHaveClass("heading");
});






it("Has the class heading__h1", async () => {
  const { getByRole } = render(<HeadingH1Atom>heading h1</HeadingH1Atom>);

  const text = getByRole("heading", {
    name: /heading h1/i,
  });
  expect(text).toHaveClass("heading__h1");
});

it("Has the class heading__h2", async () => {
  const { getByRole } = render(<HeadingH2Atom>heading h2</HeadingH2Atom>);

  const text = getByRole("heading", {
    name: /heading h2/i,
  });
  expect(text).toHaveClass("heading__h2");
});

it("Has the class heading__h3", async () => {
  const { getByRole } = render(<HeadingH3Atom>heading h3</HeadingH3Atom>);

  const text = getByRole("heading", {
    name: /heading h3/i,
  });
  expect(text).toHaveClass("heading__h3");
});

it("Has the class heading__h4", async () => {
  const { getByRole } = render(<HeadingH4Atom>heading h4</HeadingH4Atom>);

  const text = getByRole("heading", {
    name: /heading h4/i,
  });
  expect(text).toHaveClass("heading__h4");
});

it("Has the class heading__h1", async () => {
  const { getByText } = render(<HeadingPreviewAtom>heading preview</HeadingPreviewAtom>);

  const text = getByText(/heading preview/i);
  expect(text).toHaveClass("heading__preview");
});
