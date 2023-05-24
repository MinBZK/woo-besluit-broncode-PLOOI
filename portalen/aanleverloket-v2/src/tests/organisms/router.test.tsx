import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import Router from '../../routes';

it("Renders the Root Router", () => {
  const { asFragment } = render(
    <Provider store={setupStore()}>
      <Router />
    </Provider>
  );

  expect(asFragment).not.toBeNull();
});
