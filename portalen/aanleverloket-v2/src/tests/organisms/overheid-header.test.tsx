import { render } from "@testing-library/react";
import { OverheidHeaderOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { BrowserRouter as Router } from "react-router-dom";

it("Renders the OverheidHeaderOrganism", () => {
  const { getByRole } = render(
    <Router>
      <Provider store={setupStore()}>
        <OverheidHeaderOrganism />
      </Provider>
    </Router>
  );

  const listitem = getByRole("img", { name: /overheid logo/i });
  expect(
    listitem.parentElement?.parentElement?.parentElement
  ).toBeInTheDocument();
});
