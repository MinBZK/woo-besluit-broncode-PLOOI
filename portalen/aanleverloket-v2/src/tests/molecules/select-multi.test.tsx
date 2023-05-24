import { fireEvent, render, waitFor } from "@testing-library/react";
import { SelectMultiMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";
import { RequiredListValidator } from "../../validations/requiredValidator";

const props = {
  id: "select",
  label: "Select options",
  placeholder: "Kies een of meerdere documentsoorten",
  tooltip: "Select options",
  required: true,
  validations: [new RequiredListValidator()],
  categories: [
    {
      title: "Title",
      options: [
        {
          id: "Moties1",
          checked: true,
          disabled: true,
          bold: false,
          onClick: () => {},
          label: "Moties1",
        },
        {
          id: "Moties2",
          checked: false,
          disabled: true,
          bold: false,
          onClick: () => {},
          label: "Moties2",
        },
      ],
      sublist: [
        {
          title: "SubMenu Title",
          options: [
            {
              id: "Moties3",
              checked: false,
              disabled: true,
              bold: false,
              onClick: () => {},
              label: "Moties3",
            },
            {
              id: "Moties4",
              checked: true,
              disabled: false,
              bold: false,
              onClick: () => {},
              label: "Moties4",
            },
          ],
          sublist:[],
        },
      ],
    },
  ],
};

it("Renders the SelectMultiMolecule", () => {
  const { getByRole } = render(<SelectMultiMolecule {...props} />);

  const textbox = getByRole("textbox");
  expect(
    textbox.parentElement?.parentElement?.parentElement
  ).toBeInTheDocument();
});

it("Renders the category options SelectMultiMolecule", () => {
  const { getByRole, getByText } = render(<SelectMultiMolecule {...props} />);

  const textbox = getByRole("textbox");
  expect(textbox).toBeInTheDocument();
  fireEvent.click(textbox);
  fireEvent.focus(textbox);

  // const moties1 = getByText(/moties1/i); //ook als chip vindbaar omdat deze checked is
  // expect(moties1).toBeInTheDocument();
  const moties2 = getByText(/moties2/i);
  expect(moties2).toBeInTheDocument();
  const moties3 = getByText(/moties3/i); 
  expect(moties3).toBeInTheDocument();
  // const moties4 = getByText(/moties4/i);// ^
  // expect(moties4).toBeInTheDocument();
});

it("Has the class container", () => {
  const { getByRole } = render(<SelectMultiMolecule {...props} />);

  const textbox = getByRole("textbox");
  expect(textbox.parentElement?.parentElement?.parentElement).toHaveClass(
    "container"
  );
});

it("input required SelectMultiMolecule has class select--alert", async () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} required />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  await waitFor(() => fireEvent.blur(textbox));
  expect(textbox.parentElement?.parentElement).toHaveClass("select--alert");
});

it("input required results has class results--alert", async () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} required />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  await waitFor(() => fireEvent.blur(textbox));
  expect(textbox.parentElement?.parentElement?.childNodes[1]).toHaveClass("results--alert");
});

it("input onFocus SelectMultiMolecule has class select--expand", () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  expect(textbox.parentElement?.parentElement).toHaveClass("select--expand");
});

it("input not focused SelectMultiMolecule has class select--expand not", () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  expect(textbox.parentElement?.parentElement).not.toHaveClass("select--expand");
});

it("input onFocus results has class results--expand", () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  fireEvent.click(textbox);
  fireEvent.focus(textbox);

  expect(textbox.parentElement?.parentElement?.childNodes[1]).toHaveClass("results--expand");
});

it("input not focused results has not class results--expand", () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  expect(textbox.parentElement?.parentElement?.childNodes[1]).not.toHaveClass("results--expand");
});

it("Renders the subitemlist checked item chip", () => {
  const { getAllByText } = render(<SelectMultiMolecule {...props} />);

  const textbox = getAllByText(/Moties4/i);
  expect(textbox.length).toBe(2);
});

// it("Has empty placeholder", () => {
//   const { getByRole } = render(<SelectMultiMolecule {...props} />);

//   const textbox = getByRole("textbox", {
//     name: /select options/i,
//   });
//   expect(textbox).toHaveAttribute("placeholder", "");
// });

it("Has render placeholder", () => {
  const cat = props.categories;
  cat[0].options[0].checked = false;
  cat[0].sublist[0].options[1].checked = false;
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={cat} />
  );

  const textbox = getByRole("textbox");
  expect(textbox).toHaveAttribute(
    "placeholder",
    "Kies een of meerdere documentsoorten"
  );
});

it("input SelectMultiMolecule", () => {
  const { getByRole } = render(<SelectMultiMolecule {...props} />);

  const textbox = getByRole("textbox");
  fireEvent.click(textbox);
  fireEvent.focus(textbox);
  fireEvent.change(textbox, { target: { value: "1" } });
  expect(textbox).toHaveValue("1");
});

it("input SelectMultiMolecule show only Moties3", () => {
  const { getByRole, getByText, queryByText } = render(
    <SelectMultiMolecule {...props} />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  fireEvent.change(textbox, { target: { value: "3" } });
  expect(textbox).toHaveValue("3");

  const moties3 = getByText(/moties3/i);
  expect(moties3).toBeInTheDocument();

  const moties4 = queryByText(/moties4/i);
  expect(moties4).not.toBeInTheDocument();
});

it("input SelectMultiMolecule title result", () => {
  const { getByRole, queryByText, getByText } = render(<SelectMultiMolecule {...props} />);

  const textbox = getByRole("textbox");
  fireEvent.click(textbox);
  fireEvent.focus(textbox);
  fireEvent.change(textbox, { target: { value: "SubMenu" } });
  expect(textbox).toHaveValue("SubMenu");

  // const moties1 = queryByText(/moties1/i);
  // expect(moties1).not.toBeInTheDocument();

  const moties3 = getByText(/moties3/i);
  expect(moties3).toBeInTheDocument();

  const results = queryByText(/Geen resultaten/i);
  expect(results).not.toBeInTheDocument();
});

it("SelectMultiMolecule no categories", () => {
  const { getByText } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const results = getByText(/Geen resultaten/i);
  expect(results).toBeInTheDocument();
});

it("input SelectMultiMolecule no categories", () => {
  const { getByText, getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  fireEvent.click(textbox);
  fireEvent.focus(textbox);
  fireEvent.change(textbox, { target: { value: "1" } });
  expect(textbox).toHaveValue("1");

  const results = getByText(/Geen resultaten/i);
  expect(results).toBeInTheDocument();
});

it("input required shows warning", async () => {
  const { getByRole, getByText } = render(
    <SelectMultiMolecule {...props} categories={[]} required />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  await waitFor(() => fireEvent.blur(textbox));
  const warning = getByText("Dit is een verplicht veld");

  expect(warning).toBeInTheDocument();
});

it("input not required shows no warning", async () => {
  const { getByRole, queryByText } = render(
    <SelectMultiMolecule {...props} categories={[]} required={false} validations={undefined} />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  await waitFor(() => fireEvent.blur(textbox));
  const warning = queryByText("Dit is een verplicht veld");

  expect(warning).not.toBeInTheDocument();
});

it("input SelectMultiMolecule has not class select--expand after escape press ", async () => {
  const { getByRole } = render(
    <SelectMultiMolecule {...props} categories={[]} />
  );

  const textbox = getByRole("textbox");
  fireEvent.focus(textbox);
  expect(textbox.parentElement?.parentElement).toHaveClass("select--expand");
  await fireEvent.keyDown(getByRole("textbox"), { 
    key: "Escape",
    code: "Escape",
    keyCode: 27,
    charCode: 27 
  });
  expect(textbox.parentElement?.parentElement).not.toHaveClass("select--expand");
});
