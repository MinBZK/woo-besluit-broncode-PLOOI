import {
  fireEvent,
  render,
  waitFor,
} from "@testing-library/react";
import { FileInputMolecule } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";
import { MaxFileSizeValidator } from "../../validations/maxSizeValidator";
import { RequiredFileValidator } from "../../validations/requiredValidator";

const props = {
  onSelectFile: (f?: File) => {},
  selectedFileName: "",
  label: "upload file label",
  tooltip: "tooltip text",
  required: true,
  maxFileSize: 2000000000,
  validations: [new MaxFileSizeValidator(2000000000), new RequiredFileValidator()],
};

it("Renders the FileInputMolecule", () => {
  const { getByRole } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input");
  expect(input).toBeInTheDocument();
});

it("Renders the FileInputMolecule without label", () => {
  const { getByRole } = render(<FileInputMolecule {...props} label={undefined} />);

  const input = getByRole("input");
  expect(input).toBeInTheDocument();
});

it("FileInputMolecule input has class", () => {
  const { getByRole } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input");
  expect(input.parentElement?.parentElement).toHaveClass("input_container");
});

it("Renders the FileInputMolecule when no file is selected", () => {
  const { getByText } = render(<FileInputMolecule {...props} />);

  const input = getByText(/selecteer een bestand/i);
  expect(input).toBeInTheDocument();
});

it("className FileInputMolecule when no file is selected", () => {
  const { getByText } = render(<FileInputMolecule {...props} />);

  const input = getByText(/selecteer een bestand/i);
  expect(input.parentElement).toHaveClass("input_container__inner");
});

it("Renders the FileInputMolecule when file is selected", () => {
  props.selectedFileName = "Bestand.pdf";
  const { getByText } = render(<FileInputMolecule {...props} />);

  const input = getByText(/Selecteer een ander bestand/i);
  expect(input).toBeInTheDocument();
});

it("className FileInputMolecule when file is selected", () => {
  props.selectedFileName = "Bestand.pdf";
  const { getByText } = render(<FileInputMolecule {...props} />);

  const input = getByText(/Selecteer een ander bestand/i);
  expect(input.parentElement).toHaveClass(
    "input_container__selectedFileContainer"
  );
});

it("FileInputMolecule upload file", async () => {
  const { getByRole } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input") as HTMLInputElement;
  let file = new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" });

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [file] },
    })
  );

  expect(input?.files?.length).toBe(1);

  if (input?.files != null && input?.files?.length > 0)
    expect(input?.files[0]?.name).toBe("chucknorris.png");
});

it("FileInputMolecule upload file with correct size", async () => {
  const { getByRole } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input") as HTMLInputElement;
  let file = new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" });
  Object.defineProperty(file, "size", { value: 1024 * 1024 + 1 });

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [file] },
    })
  );

  expect(input?.files?.length).toBe(1);

  if (input?.files != null && input?.files?.length > 0)
    expect(input?.files[0]?.name).toBe("chucknorris.png");
});

it("FileInputMolecule upload file with more then and less then 2gb size with error label test", async () => {
  const { getByRole, queryByRole } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input") as HTMLInputElement;
  const file = new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" });
  Object.defineProperty(file, "size", { value: props.maxFileSize - 1 });

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [file] },
    })
  );

  let errorIcon = queryByRole("img", {
    name: /icon\-alert\-red/i,
  });

  expect(input?.files?.length).toBe(1);
  expect(errorIcon).toBe(null);

  if (input?.files != null && input?.files?.length > 0)
    expect(input?.files[0]?.name).toBe("chucknorris.png");

  const file2 = new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" });
  Object.defineProperty(file2, "size", { value: props.maxFileSize + 1 });

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [file2] },
    })
  );

  errorIcon = getByRole("img", {
    name: /icon\-alert\-red/i,
  });

  expect(errorIcon).toBeInTheDocument();
  expect(input?.files?.length).toBe(1);

});

it("FileInputMolecule upload without a file and required", async () => { 
  const { getByRole, getByText } = render(<FileInputMolecule {...props} required />);

  const input = getByRole("input") as HTMLInputElement;

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [undefined] },
    })
  );

  const errorText = getByText(/Selecteer een bestand/i);
  expect(errorText).toBeInTheDocument();

});

it("FileInputMolecule upload without a file", async () => { 
  const { getByRole, getByText } = render(<FileInputMolecule {...props} />);

  const input = getByRole("input") as HTMLInputElement;

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: [undefined] },
    })
  );

  const errorText = getByText(/Selecteer een bestand/i);
  expect(errorText).toBeInTheDocument();

});

it("Renders the Label", () => {
  const { getByText } = render(<FileInputMolecule {...props} />);

  const label = getByText(/upload file label/i);
  expect(label).toBeInTheDocument();
});

it("Renders the tooltip button", () => {
  const { getByRole } = render(<FileInputMolecule {...props} />);

  const tooltip = getByRole("button", { name: /info/i });
  expect(tooltip).toBeInTheDocument();
});

it("Renders the verplicht veld text", () => {
  const { getByText } = render(<FileInputMolecule {...props} />);

  const verplicht = getByText(/verplicht veld/i);
  expect(verplicht).toBeInTheDocument();
});

it("onMouseEnter tooltip", async () => {
  const { getByRole, queryByText } = render(<FileInputMolecule {...props} />);

  let tooltipText = queryByText(/tooltip text/i);

  expect(tooltipText).not.toBeInTheDocument();
  expect(tooltipText).toBe(null);

  const tooltip = getByRole("button", { name: /info/i });
  await waitFor(() => fireEvent.mouseEnter(tooltip));

  tooltipText = queryByText(/tooltip text/i);
  expect(tooltipText).toBeInTheDocument();
});

it("onMouseLeave tooltip", async () => {
  const { getByRole, queryByText } = render(<FileInputMolecule {...props} />);

  const tooltip = getByRole("button", { name: /info/i });
  await waitFor(() => fireEvent.mouseLeave(tooltip));
  const tooltipText = queryByText(/tooltip text/i);

  expect(tooltipText).not.toBeInTheDocument();
  expect(tooltipText).toBe(null);
});
