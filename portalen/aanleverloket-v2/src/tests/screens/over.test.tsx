import "@testing-library/jest-dom/extend-expect";
import { render } from "@testing-library/react";
import { OverScreen } from "../../ui/screens";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { HashRouter } from "react-router-dom";

it("Renders the Create screen", () => {
  const { getByText } = render(
    <Provider store={setupStore()}>
      <HashRouter>
        <OverScreen />
      </HashRouter>
    </Provider>
  );

  const title = getByText("De Wet open overheid");
  expect(title.parentElement).toBeInTheDocument();
});
