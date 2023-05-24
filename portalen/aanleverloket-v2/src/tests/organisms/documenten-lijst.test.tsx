import { fireEvent, render, waitFor } from "@testing-library/react";
import { DocumentenLijstOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { HashRouter } from "react-router-dom";
import { searchDocuments } from "../../store/reducers/search";
import { ApiFactory } from "../../api";

ApiFactory.isTestSuite = true;

it("Renders the DocumentenLijstOrganism", () => {
  const { getByRole } = render(
    <Provider store={setupStore()}>
      <HashRouter>
        <DocumentenLijstOrganism />
      </HashRouter>
    </Provider>
  );

  const title = getByRole("heading", { name: /documentenlijst/i });
  expect(title.parentElement).toBeInTheDocument();
});

it("Renders the DocumentenLijstOrganism with items", async () => {
  const store = setupStore();
  await store.dispatch(searchDocuments(1));

  const { getByRole } = render(
    <Provider store={store}>
      <HashRouter>
        <DocumentenLijstOrganism />
      </HashRouter>
    </Provider>
  );

  const title = getByRole("heading", { name: /documentenlijst/i });
  expect(title.parentElement).toBeInTheDocument();
  expect(store.getState().search.data?.aantalResultaten).toBeGreaterThan(0);
});

// it("Press depubliceren", async () => {
//   const store = setupStore();
//   await store.dispatch(searchDocuments(1));

//   const { getAllByRole, getByRole } = render(
//     <Provider store={store}>
//       <HashRouter>
//         <DocumentenLijstOrganism />
//       </HashRouter>
//     </Provider>
//   );
//   expect(store.getState().search.data?._embedded.resultaten.length).toBeGreaterThan(0);

//   const button = getAllByRole('button', { name: /zichtbaar/i });
//   await waitFor(() => fireEvent.click(button[0]));

//   const title = getByRole('heading', {
//     name: /Depubliceren/i
//   })
//   expect(title.parentElement?.parentElement?.parentElement).toBeInTheDocument();

// });
