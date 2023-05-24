import {
  fireEvent,
  render,
  waitFor,
} from "@testing-library/react";
import { CreateFormulierOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { BrowserRouter as Router } from "react-router-dom";
import { addForm, clear } from "../../store/reducers/metadata";
import { ApiFactory } from "../../api";
import { login, storeToken } from "../../store/reducers/auth";

function formatDate(date = new Date()) {
  return [
    date.getFullYear(),
    padTo2Digits(date.getMonth() + 1),
    padTo2Digits(date.getDate()),
  ].join('-');
}

function padTo2Digits(num: number) {
  return num.toString().padStart(2, '0');
}

ApiFactory.isTestSuite = true;
const mockJwt = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

async function init() {
  const store = setupStore();
  await store.dispatch(storeToken(mockJwt));
  await store.dispatch(login());
  await store.dispatch(clear());
  await store.dispatch(addForm());

  return store;
}

it("Renders the CreateFormulierOrganism", async () => {
  const store = await init();
  const { getByText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );

  const title = getByText("Document uploaden");
  expect(title.parentElement).toBeInTheDocument();
});

// it("Submit the form unsuccesfull", () => {
//   const { getByText } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <CreateFormulierOrganism />
//       </Provider>
//     </Router>
//   );

//   const button = getByText("Publiceren");
//   button.click();
// });


// it("Submits the form succesfull", async () => {
//   const { getByText, getByRole, getByPlaceholderText, getByDisplayValue } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <CreateFormulierOrganism />
//       </Provider>
//     </Router>
//   );

//   const title = getByRole('textbox', { name: /officiële titel/i })
//   await waitFor(() => fireEvent.click(title));
//   await waitFor(() =>
//     fireEvent.change(title, {
//       target: { value: "title" },
//     })
//   );
//   expect(title).toHaveValue("title");

//   const identifier = getByRole('textbox', { name: /identifier/i })
//   await waitFor(() => fireEvent.click(identifier));
//   await waitFor(() =>
//     fireEvent.change(identifier, {
//       target: { value: "identifier" },
//     })
//   );
//   expect(identifier).toHaveValue("identifier");

//   const Organisatie = getByPlaceholderText("Organisatie");
//   expect(Organisatie).toBeInTheDocument();
//   await waitFor(() => fireEvent.click(Organisatie));
//   await waitFor(() =>
//     fireEvent.change(Organisatie, {
//       target: { value: "newValue" },
//     })
//   );
//   expect(Organisatie).toHaveValue("newValue");

//   const handeling = getByText(/^Handeling$/i);
//   await waitFor(() => fireEvent.click(handeling));
//   const dropdownOption = getByText(/^vaststelling$/i);
//   await waitFor(() => fireEvent.click(dropdownOption));

//   const date = formatDate(new Date());
//   const dateInput = getByDisplayValue(date)
//   await waitFor(() => fireEvent.focus(dateInput));
//   await waitFor(() =>
//     fireEvent.change(dateInput, { target: { value: "2020-05-12" } })
//   );
//   expect(dateInput).toHaveValue("2020-05-12");

//   const thema = getByRole('textbox', { name: /thema's/i })
//   await waitFor(() => fireEvent.click(thema));
//   const themaOptions = getByText(/^media$/i);
//   await waitFor(() => fireEvent.click(themaOptions));

//   const docsoort = getByRole('textbox', { name: /documentsoort/i })
//   await waitFor(() => fireEvent.click(docsoort));
//   const docsoortOptions = getByText(/^brief$/i);
//   await waitFor(() => fireEvent.click(docsoortOptions));

//   const input = getByRole("input") as HTMLInputElement;
//   let file = new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" });

//   await waitFor(() =>
//     fireEvent.change(input, {
//       target: { files: [file] },
//     })
//   );

//   expect(input?.files?.length).toBe(1);

//   await waitFor(() => fireEvent.blur(input));
  
//   const button = getByText("Publiceren");
//   button.click();
//   // const text = getByText(/Niet alle benodigde velden zijn ingevuld/i);
//   // expect(text.parentElement).toBeInTheDocument();
// });

it("Set Identifier", async () => {
  const store = await init();

  const { getByRole } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );

  const textbox = getByRole('textbox', { name: /identifier/i })
  expect(textbox).toBeInTheDocument();
  await waitFor(() => fireEvent.click(textbox));
  await waitFor(() =>
    fireEvent.change(textbox, {
      target: { value: "newValue" },
    })
  );
  expect(textbox).toHaveValue("newValue");
});

it("Documenthandeling change associated with", async () => {
  const store = await init();

  const { getAllByText, getByText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );

  const textbox = getAllByText(/^ministerie van Binnenlandse Zaken en Koninkrijksrelaties$/i);
  expect(textbox.length).toBeGreaterThan(0);
  await waitFor(() => fireEvent.click(textbox[1]));

  let dropdownOption = getByText(/^ministerie van Justitie$/i);
  expect(dropdownOption).toBeInTheDocument;
  await waitFor(() => fireEvent.click(dropdownOption));

  const mvj = getAllByText(/^ministerie van Justitie$/i);
  expect(mvj.length).toBe(2); 


  // const textbox = getByPlaceholderText("Organisatie");
  // expect(textbox).toBeInTheDocument();
  // await waitFor(() => fireEvent.click(textbox));
  // await waitFor(() =>
  //   fireEvent.change(textbox, {
  //     target: { value: "newValue" },
  //   })
  // );
  // expect(textbox).toHaveValue("newValue");
});

it("Documenthandeling change handeling", async () => {
  const store = await init();

  const { getAllByText, getByText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );

  const input = getByText(/^Handeling$/i);
  await waitFor(() => fireEvent.click(input));

  let vaststelling = getAllByText(/^vaststelling$/i);
  expect(vaststelling.length).toBe(1); 

  const dropdownOption = getByText(/^vaststelling$/i);
  await waitFor(() => fireEvent.click(dropdownOption));

  vaststelling = getAllByText(/^vaststelling$/i);
  expect(vaststelling.length).toBe(2); 

});

it("Documenthandeling change atTime", async () => {
  const store = await init();

  const { getByDisplayValue } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );

  const date = formatDate(new Date());
  const input = getByDisplayValue(date)
  expect(input).toBeInTheDocument();
  await waitFor(() => fireEvent.focus(input));
  await waitFor(() =>
    fireEvent.change(input, { target: { value: "2020-05-12" } })
  );
  expect(input).toHaveValue("2020-05-12");
});

it("add documenthandeling", async () => {
  const store = await init();

  const { getByText, getAllByText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );
  let handeling = getAllByText(/^handeling$/i);
  expect(handeling.length).toBe(1);

  const button = getByText(/Voeg handeling toe/i);
  expect(button).toBeInTheDocument();
  await waitFor(() => fireEvent.click(button));

  handeling = getAllByText(/^handeling$/i);
  expect(handeling.length).toBe(2); 
});

it("remove documenthandeling", async () => {
  const store = await init();

  const { getByText, getAllByText, getAllByRole } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );
  let handeling = getAllByText(/^handeling$/i);
  expect(handeling.length).toBe(1);

  const button = getByText(/Voeg handeling toe/i);
  expect(button).toBeInTheDocument();
  await waitFor(() => fireEvent.click(button));

  handeling = getAllByText(/^handeling$/i);
  expect(handeling.length).toBe(2);

  const removeButton = getAllByRole("button", { name: /icon\-remove\-blue/i });
  await waitFor(() => fireEvent.click(removeButton[0]));

  handeling = getAllByText(/^handeling$/i);
  expect(handeling.length).toBe(1);
});

it("change begindatum", async () => {
  const store = await init();

  const { getByText, getByPlaceholderText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );
  const foldable = getByText(/optionele velden/i);
  await waitFor(() => fireEvent.click(foldable));

  const input = getByPlaceholderText(/kies begindatum/i);
  expect(input).toBeInTheDocument();

  await waitFor(() => fireEvent.focus(input));
  await waitFor(() =>
    fireEvent.change(input, { target: { value: "2020-05-12" } })
  );
  expect(input).toHaveValue("2020-05-12");
});

it("change einddatum without begindatum", async () => {
  const store = await init();

  const { getByText, getByPlaceholderText } = render(
    <Router>
      <Provider store={store}>
        <CreateFormulierOrganism />
      </Provider>
    </Router>
  );
  const foldable = getByText(/optionele velden/i);
  await waitFor(() => fireEvent.click(foldable));

  const input = getByPlaceholderText(/kies einddatum/i);
  expect(input).toBeInTheDocument();

  await waitFor(() => fireEvent.focus(input));
  await waitFor(() =>
    fireEvent.change(input, { target: { value: "2020-05-12" } })
  );
  expect(input).toHaveValue("2020-05-12");
});

// function dispatch(arg0: any) {
//   throw new Error("Function not implemented.");
// }

