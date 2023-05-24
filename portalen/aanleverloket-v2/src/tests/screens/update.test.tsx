import "@testing-library/jest-dom/extend-expect";
import { render } from "@testing-library/react";
import { UpdateScreen } from "../../ui/screens";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { HashRouter } from "react-router-dom";

it("Renders the Create screen met Error fetching data.", () => {
  const { getByText } = render(
    <Provider store={setupStore()}>
      <HashRouter>
        <UpdateScreen />
      </HashRouter>
    </Provider>
  );

  const title = getByText("Error fetching data.");
  expect(title.parentElement).toBeInTheDocument();
});
