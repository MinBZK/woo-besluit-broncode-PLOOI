import { render } from "@testing-library/react";
import { TableAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  theads: ["nr", "naam"],
  rows: [
    ["1.", "test1"],
    ["2.", "test2"],
    ["3.", "test3"],
  ],
  columnWidth:["45%", "50%"],
  OnEndOfScroll:()=>{}
};

it("Renders the TableAtom", () => {
  const { getByRole } = render(<TableAtom {...props} />);

  const table = getByRole("table");
  expect(table).toBeInTheDocument();
});


it("table column to have style", () => {
  const { getByRole } = render(<TableAtom {...props} columnWidth={["10px", "20px"]}  />);

  const header1 = getByRole("columnheader", {
    name: props.theads[0],
  });

  const header2 = getByRole("columnheader", {
    name: props.theads[1],
  });

  expect(header1).toHaveStyle(`width: 10px`);
  expect(header2).toHaveStyle(`width: 20px`);
});

it("table column to have style", () => {
  const { getByRole } = render(<TableAtom {...props} columnWidth={undefined}  />);

  const table = getByRole("table");
  expect(table).toBeInTheDocument();
});

it("columnheader ", () => {
  const { getByRole } = render(<TableAtom {...props} />);

  const header1 = getByRole("columnheader", {
    name: props.theads[0],
  });
  expect(header1).toBeInTheDocument();

  const header2 = getByRole("columnheader", {
    name: props.theads[1],
  });
  expect(header2).toBeInTheDocument();
});


// it("Rows ", () => {
//   const { getByText } = render(<TableAtom {...props} />);

//   const row1 = getByText(/test1/i)
//   expect(row1).toBeInTheDocument();

// });
