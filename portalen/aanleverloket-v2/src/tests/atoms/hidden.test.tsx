import {
  act,
  fireEvent,
  render,
} from "@testing-library/react";
import { MobileHiddenAtom, DesktopHiddenAtom } from "../../ui/atoms";
import "@testing-library/jest-dom/extend-expect";

it("Renders the MobileHiddenAtom", () => {
  const { getByText } = render(
    <MobileHiddenAtom>Mobile Hidden</MobileHiddenAtom>
  );

  act(() => {
    global.window.innerWidth = 900
    global.window.innerHeight = 900

    fireEvent(global.window, new Event("resize"))
  })

  expect(global.window.innerWidth).toBe(900);
  expect(global.window.innerHeight).toBe(900);

  const hidden = getByText(/mobile hidden/i);
  expect(hidden).toBeInTheDocument();

  act(() => {
    global.window.innerWidth = 700
    global.window.innerHeight = 700

    fireEvent(global.window, new Event("resize"))
  })

  expect(global.window.innerWidth).toBe(700);
  expect(global.window.innerHeight).toBe(700);

  expect(hidden.childElementCount).toBe(0);
});

it("Renders the DesktopHiddenAtom", () => {
  const { getByText } = render(
    <DesktopHiddenAtom>Desktop Hidden</DesktopHiddenAtom>
  );

  act(() => {
    global.window.innerWidth = 700
    global.window.innerHeight = 700

    fireEvent(global.window, new Event("resize"))
  })

  expect(global.window.innerWidth).toBe(700);
  expect(global.window.innerHeight).toBe(700);

  const hidden = getByText(/desktop hidden/i);
  expect(hidden).toBeInTheDocument();

  act(() => {
    global.window.innerWidth = 900
    global.window.innerHeight = 900

    fireEvent(global.window, new Event("resize"))
  })

  expect(global.window.innerWidth).toBe(900);
  expect(global.window.innerHeight).toBe(900);

  expect(hidden.childElementCount).toBe(0);
});
