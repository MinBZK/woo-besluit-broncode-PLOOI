import { render } from "@testing-library/react";
import { LoginFormOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import {setupStore} from "../../store";

it("Renders the LoginFormOrganism", () => {
  const { getByRole } = render(
    <Provider store={setupStore()}>
      <LoginFormOrganism />
    </Provider>
  );

  const sideinfo = getByRole('button', { name: /inloggen/i })
  expect(sideinfo.parentElement?.parentElement?.parentElement?.parentElement).toBeInTheDocument();
});
