import { render } from "@testing-library/react";
import { OverheidFooterOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import {setupStore} from "../../store";

it("Renders the OverheidFooterOrganism", () => {
  const { getByRole } = render(
    // <Provider store={store}>
      <OverheidFooterOrganism />
    // </Provider>
  );

  const listitem = getByRole('link', { name: /over deze website/i });
  expect(listitem.parentElement?.parentElement?.parentElement?.parentElement).toBeInTheDocument();
});
