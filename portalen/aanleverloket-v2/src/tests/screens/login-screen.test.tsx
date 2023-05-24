import "@testing-library/jest-dom/extend-expect";
import { render } from "@testing-library/react";
import { LoginScreen } from "../../ui/screens";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { BrowserRouter as Router } from "react-router-dom";

it("Renders the Create screen", () => {
  const { getAllByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <LoginScreen />
      </Provider>
    </Router>
  );

  const title = getAllByText("Inloggen");
  expect(title.length).toBe(2);
});
