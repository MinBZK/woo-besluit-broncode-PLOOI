import {
  fireEvent,
  getAllByRole,
  render,
  waitFor,
} from "@testing-library/react";
import { CheckboxListSelectAllMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";

const props = {
  title: "testTitle",
  onSelectAll: () => {},
  selectAllLabel: "select all",
  options: [
    {
      id: "Options 1 id",
      checked: false,
      disabled: false,
      bold: false,
      onClick: () => {},
      label: "Option 1 label",
    },
    {
      id: "Options 2 id",
      checked: true,
      disabled: true,
      bold: true,
      onClick: () => {},
      label: "Option 2 label",
    },
  ],
};

it("Renders the CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const checkboxList = getByText(/testTitle/i);
  expect(checkboxList).toBeInTheDocument();
});

it("Has the class list", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const checkboxList = getByText(/testTitle/i);
  expect(checkboxList.parentElement).toHaveClass("list");
});

it("Renders the children of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(
    <CheckboxListSelectAllMolecule {...props}></CheckboxListSelectAllMolecule>
  );

  const checkboxList = getByText(/testTitle/i);
  expect(checkboxList?.parentElement?.childElementCount).toBe(3); //title - spacer - options
  expect(checkboxList.hasChildNodes()).toBe(true);
});

it("Renders the options of CheckboxListSelectAllMolecule", () => {
  const { getByRole, getAllByRole } = render(
    <CheckboxListSelectAllMolecule {...props} />
  );

  const optionsList = getByRole("list");
  expect(optionsList?.childElementCount).toBe(3); //childs
  expect(optionsList.hasChildNodes()).toBe(true);

  const options = getAllByRole("listitem"); //get all options
  expect(options?.length).toBe(3);
});

it("Renders option select all of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const selectAll = getByText(/select all/i);
  expect(selectAll).toBeInTheDocument();
});

it("Renders option 1 of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/Option 1 label/i);
  expect(option1).toBeInTheDocument();
});

it("Renders option 2 of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/Option 2 label/i);
  expect(option1).toBeInTheDocument();
});

it("option 1 is not checked of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/Option 1 label/i);
  expect(option1.parentElement).not.toHaveClass("checkbox--checked");
});

it("option 2 is checked of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option2 = getByText(/Option 2 label/i);
  expect(option2.parentElement).toHaveClass("checkbox--checked");
});

it("option 1 is not disabled of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/Option 1 label/i);
  expect(option1.parentElement?.childNodes[0]).not.toHaveClass(
    "checkbox--disabled"
  );
});

it("option 2 is disabled of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option2 = getByText(/Option 2 label/i);
  expect(option2.parentElement?.childNodes[0]).toHaveClass(
    "checkbox--disabled"
  );
});

it("select all is bold of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/select all/i);
  expect(option1.parentElement?.parentElement).toHaveClass("list_item__bold");
});

it("option 1 is not bold of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option1 = getByText(/Option 1 label/i);
  expect(option1.parentElement?.parentElement).not.toHaveClass(
    "list_item__bold"
  );
});

it("option 2 is bold of CheckboxListSelectAllMolecule", () => {
  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const option2 = getByText(/Option 2 label/i);
  expect(option2.parentElement?.parentElement).toHaveClass("list_item__bold");
});

it("CheckboxListSelectAllMolecule option 1 click", async () => {
  const handleClick = jest.fn();

  const options = props.options;
  options[0].onClick = handleClick;

  const { getByText } = render(
    <CheckboxListSelectAllMolecule {...props} options={options} />
  );

  const option1 = getByText(/Option 1 label/i);

  await waitFor(() => fireEvent.click(option1));

  expect(handleClick).toHaveBeenCalledTimes(1);
});

it("CheckboxListSelectAllMolecule option 2 click and disabled", async () => {
  const handleClick = jest.fn();

  const options = props.options;
  options[1].onClick = handleClick;

  const { getByText } = render(
    <CheckboxListSelectAllMolecule {...props} options={options} />
  );

  const option2 = getByText(/Option 2 label/i);

  await waitFor(() => fireEvent.click(option2));

  expect(handleClick).toHaveBeenCalledTimes(0);
});

it("CheckboxListSelectAllMolecule option 2 click", async () => {
  const handleClick = jest.fn();

  const options = props.options;
  options[1].disabled = false;
  options[1].onClick = handleClick;

  const { getByText } = render(
    <CheckboxListSelectAllMolecule {...props} options={options} />
  );

  const option2 = getByText(/Option 2 label/i);

  await waitFor(() => fireEvent.click(option2));

  expect(handleClick).toHaveBeenCalledTimes(1);
});

it("CheckboxListSelectAllMolecule option 2 click", async () => {
  const handleClick = jest.fn();

  const options = props.options;
  options[1].disabled = false;
  options[1].onClick = handleClick;

  const { getByText } = render(
    <CheckboxListSelectAllMolecule {...props} options={options} />
  );

  const option2 = getByText(/Option 2 label/i);

  await waitFor(() => fireEvent.click(option2));

  expect(handleClick).toHaveBeenCalledTimes(1);
  expect(option2.parentElement).toHaveClass("checkbox--checked");
});

it("CheckboxListSelectAllMolecule select all click", async () => {
  const handleClick = jest.fn();

  props.onSelectAll = handleClick;

  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const selectAll = getByText(/select all/i);

  await waitFor(() => fireEvent.click(selectAll));

  expect(handleClick).toHaveBeenCalledTimes(1);
});

it("CheckboxListSelectAllMolecule select all checked when all options are checked", async () => {
  const options = props.options;
  options[0].checked = true;

  const { getByText } = render(<CheckboxListSelectAllMolecule {...props} />);

  const selectAll = getByText(/select all/i);
  expect(selectAll.parentElement).toHaveClass("checkbox--checked");
});
