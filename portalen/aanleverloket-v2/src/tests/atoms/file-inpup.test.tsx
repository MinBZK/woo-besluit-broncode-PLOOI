import { fireEvent, getByText, render, waitFor } from "@testing-library/react";
import { FileInputAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

const props = {
  onFileChanged: (file?: File) => {},
  accept: ".pdf, .zip",
};

it("Renders the FileInputAtom", async () => {
  const { findByRole } = render(
    <FileInputAtom {...props}>
      <p>test</p>
    </FileInputAtom>
  );

  const input = await findByRole("input");
  expect(input).toBeInTheDocument();
});

it("Clicks the FileInputAtom", async () => {
  const { findByRole } = render(
    <FileInputAtom {...props}>
      <p>test</p>
    </FileInputAtom>
  );

  const input = await findByRole("input");
  input.click();
  expect(input).toBeInTheDocument();
});

it("Renders the FileInputAtom childs", () => {
  const { getByRole, getByText } = render(
    <FileInputAtom {...props}>
      <p>test</p>
    </FileInputAtom>
  );

  const input = getByRole("input");
  expect(input.parentElement?.childElementCount).toBe(2);

  const child = getByText("test");
  expect(child).toBeTruthy();
});

it("FileInputAtom upload file", async () => {
  const { getByRole } = render(
    <FileInputAtom {...props}>
      <p>test</p>
    </FileInputAtom>
  );
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

it("FileInputAtom upload file undifined", async () => {
  const { getByRole } = render(
    <FileInputAtom {...props}>
      <p>test</p>
    </FileInputAtom>
  );
  const input = getByRole("input") as HTMLInputElement;

  await waitFor(() =>
    fireEvent.change(input, {
      target: { files: null },
    })
  );

  expect(input?.files?.length).toBe(undefined);
});

