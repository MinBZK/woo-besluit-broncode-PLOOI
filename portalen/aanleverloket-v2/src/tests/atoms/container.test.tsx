import { render } from "@testing-library/react";
import { ContainerAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";
// import renderer from "react-test-renderer";

const props = {
  width: 100,
  height: 100,
  centered: true,
};

// it("Renders the ContainerAtom", () => {
//   const tree = renderer
//     .create(
//       <ContainerAtom {...props} type={"columns"}>
//         <p>test</p>
//       </ContainerAtom>
//     )
//     .toJSON();

//   expect(tree).toMatchSnapshot();
// });

it("Has the class container children ", () => {
  const { container } = render(<ContainerAtom {...props}></ContainerAtom>);

  expect(container.childElementCount).toBe(1);
  expect(container.hasChildNodes()).toBe(true); 
});

it("Has the class container ", () => {
  const { container } = render(<ContainerAtom {...props}></ContainerAtom>);

  expect(container.firstChild).toHaveClass("container");
});

it("Has the class container--centered", () => {
  const { container } = render(<ContainerAtom {...props}></ContainerAtom>);

  expect(container.firstChild).toHaveClass("container--centered");
});

it("Has the class container--centered", () => {
  const { container } = render(
    <ContainerAtom {...props} centered={false}></ContainerAtom>
  );

  expect(container.firstChild).not.toHaveClass("container--centered");
});

it("Has the class columns", () => {
  const { container } = render(
    <ContainerAtom {...props} type={"columns"}></ContainerAtom>
  );

  expect(container.firstChild).toHaveClass("columns");
});

it("Has the class container--row", () => {
  const { container } = render(
    <ContainerAtom {...props} type={"row"}></ContainerAtom>
  );

  expect(container.firstChild).toHaveClass("container--row");
});

it("Has the class container--flex", () => {
  const { container } = render(
    <ContainerAtom {...props} type={"flex"}></ContainerAtom>
  );

  expect(container.firstChild).toHaveClass("container--flex");
});

it("Should not have columns class when row type", () => {
  const { container } = render(<ContainerAtom {...props} type={"row"}></ContainerAtom>);

  expect(container.firstChild).not.toHaveClass("columns");
  expect(container.firstChild).not.toHaveClass("container--flex");
});

it("Should not have container--flex class when columns type", () => {
  const { container } = render(<ContainerAtom {...props} type={"columns"}></ContainerAtom>);

  expect(container.firstChild).not.toHaveClass("container--flex");
  expect(container.firstChild).not.toHaveClass("container--row");
});

it("Should not have columns class when row type", () => {
  const { container } = render(<ContainerAtom {...props} type={"row"}></ContainerAtom>);

  expect(container.firstChild).not.toHaveClass("columns");
  expect(container.firstChild).not.toHaveClass("container--flex");
});

it("Should not have container--flex class when columns type", () => {
  const { container } = render(<ContainerAtom {...props} type={"columns"}></ContainerAtom>);

  expect(container.firstChild).not.toHaveClass("container--flex");
  expect(container.firstChild).not.toHaveClass("container--row");
});