import "@testing-library/jest-dom/extend-expect";
import { fireEvent, getByRole, render } from "@testing-library/react";
import { StarteScreen } from "../../ui/screens";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { HashRouter } from "react-router-dom";

it("Renders the Create screen", () => {
  const { getByText } = render(
    <Provider store={setupStore()}>
      <HashRouter>
        <StarteScreen />
      </HashRouter>
    </Provider>
  );

  const title = getByText("Laatste nieuws");
  expect(title.parentElement).toBeInTheDocument();
});

it("nav to aanleveren", () => {
  const { getByRole, getByText } = render(
    <Provider store={setupStore()}>
      <HashRouter>
        <StarteScreen />
      </HashRouter>
    </Provider>
  );

  const button = getByRole('button', {
    name: /Document uploaden/i
  })
  expect(button).toBeInTheDocument();

  fireEvent.click(button);
  const text = getByText("Document uploaden");
  expect(text).toBeInTheDocument();

});