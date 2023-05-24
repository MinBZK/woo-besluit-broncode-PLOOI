import { fireEvent, queryByRole, render, waitFor } from "@testing-library/react";
import { GoToTopButtonMolecule, } from "../../ui/molecules";
import "@testing-library/jest-dom/extend-expect";

it("Renders not the go-to-top-button default", () => {
  const { queryByRole } = render(<GoToTopButtonMolecule id={"to-top"} text={"Omhoog"} icon={"icon-arrow-up-blue"} type={"default"}/>);

  const button = queryByRole('button', { name: /omhoog icon\-arrow\-up\-blue/i });
  expect(button).not.toBeInTheDocument();
});

it("Renders the go-to-top-button default", () => {
  const { getByRole } = render(<GoToTopButtonMolecule id={"to-top"} text={"Omhoog"} icon={"icon-arrow-up-blue"} type={"default"}/>);

  fireEvent.scroll(window, { target: { scrollY: 350 } });
  const button = getByRole('button', { name: /omhoog icon\-arrow\-up\-blue/i })
  expect(button).toBeInTheDocument();
});

it("on click go-to-top-button", async () => {
  const { getByRole } = render(<GoToTopButtonMolecule id={"to-top"} text={"Omhoog"} icon={"icon-arrow-up-blue"} type={"default"}/>);

  window.scrollTo = jest.fn();
  fireEvent.scroll(window, { target: { scrollY: 350 } });
  const button = getByRole('button', { name: /omhoog icon\-arrow\-up\-blue/i })
  expect(window.scrollY).toBe(350);
  await waitFor(() => fireEvent.click(button));
  expect(window.scrollTo).toBeCalledWith({behavior: "smooth", left: 0, top: 0});
});

it("hide go-to-top-button when scrolly < 300", async () => {
  const { getByRole } = render(<GoToTopButtonMolecule id={"to-top"} text={"Omhoog"} icon={"icon-arrow-up-blue"} type={"default"}/>);

  fireEvent.scroll(window, { target: { scrollY: 350 } });
  const button = getByRole('button', { name: /omhoog icon\-arrow\-up\-blue/i })
  expect(button).toBeInTheDocument();
  fireEvent.scroll(window, { target: { scrollY: 0 } });
  expect(button).not.toBeInTheDocument();
});

