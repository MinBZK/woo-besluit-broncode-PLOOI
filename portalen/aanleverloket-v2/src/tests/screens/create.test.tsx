import "@testing-library/jest-dom/extend-expect";
import { render } from "@testing-library/react";
import { CreateScreen } from "../../ui/screens";
import { Provider } from "react-redux";
import {setupStore} from "../../store";
import { BrowserRouter as Router } from "react-router-dom";
import { ApiFactory } from "../../api";
import { login, storeToken } from "../../store/reducers/auth";
import { addForm, clear } from "../../store/reducers/metadata";

ApiFactory.isTestSuite = true;
const mockJwt = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

async function init() {
  const store = setupStore();
  await store.dispatch(storeToken(mockJwt));
  await store.dispatch(login());
  // await store.dispatch(clear());
  // await store.dispatch(addForm());

  return store;
}


it("Renders the Create screen", async () => {
  const store = await init();
  const { getByText } = render(
    <Router>
      <Provider store={store}>
        <CreateScreen />
      </Provider>
    </Router>
  );

  const title = getByText("Gerelateerde documenten");
  expect(title.parentElement).toBeInTheDocument();
});
