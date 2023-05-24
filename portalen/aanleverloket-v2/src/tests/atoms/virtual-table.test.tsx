import { fireEvent, render, waitFor } from "@testing-library/react";
import { ContainerAtom, VirtualTableAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";
import { CenteredLayout } from "../../ui/layouts";

const props = {
  theads: ["nr", "naam"],
  rows: [
    ["1.", "test1"],
    ["2.", "test2"],
    ["3.", "test3"],
  ],
  columnWidth: ["45%", "50%"],
  OnEndOfScroll: () => {},
};

it("Renders the VirtualTableAtom", () => {
  const { getByRole } = render(<VirtualTableAtom {...props} />);

  const table = getByRole("grid");
  expect(table).toBeInTheDocument();
});

it("Has the class table", () => {
  const { getByRole } = render(<VirtualTableAtom {...props} />);

  const table = getByRole("grid");
  expect(table).toHaveClass("ReactVirtualized__Table");
});

// it("table column to have style", () => {
//   const { getByRole } = render(<VirtualTableAtom {...props} columnWidth={["10px", "20px"]}  />);

//   const header1 = getByRole("columnheader", {
//     name: props.theads[0],
//   });

//   const header2 = getByRole("columnheader", {
//     name: props.theads[1],
//   });

//   expect(header1).toHaveStyle(`width: 10px`);
//   expect(header2).toHaveStyle(`width: 20px`);
// });

it("columnheader ", () => {
  const { getByText } = render(<VirtualTableAtom {...props} />);

  const header1 = getByText(/nr/i);
  expect(header1).toBeInTheDocument();

  const header2 = getByText(/naam/i);
  expect(header2).toBeInTheDocument();
});

// it("Rows ", () => {
//   const { getByText } = render(
//     <ContainerAtom centered type="flex">
//       <VirtualTableAtom {...props} />
//     </ContainerAtom>
//   );

//   const row1 = getByText(/1\./i);
//   expect(row1).toBeInTheDocument();
// });

// it("end scroll ", async () => {
//   const testObject = {
//     counter: 0,
//   };
//   const { getByRole } = render(
//     <ContainerAtom centered type="flex">
//       <VirtualTableAtom {...props} OnEndOfScroll={() => testObject.counter++} />
//     </ContainerAtom>
//   );

//   const table = getByRole("grid");
//   await waitFor(() =>
//     fireEvent.scroll(window, { target: { scrollY: 1000000 } })
//   );

//   expect(testObject.counter).toBe(1);
// });
