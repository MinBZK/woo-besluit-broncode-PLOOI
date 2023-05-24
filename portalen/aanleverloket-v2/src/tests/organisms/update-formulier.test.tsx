import { fireEvent, render, waitFor } from "@testing-library/react";
import { UpdateFormulierOrganism } from "../../ui/organisms";
import "@testing-library/jest-dom/extend-expect";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { BrowserRouter as Router } from "react-router-dom";

// const props = {
//   data: {
//     self: {
//       verantwoordelijke: {
//         id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
//         label:"ministerie van Binnenlandse Zaken en Koninkrijksrelaties"
//       },
//     },
//     document: {
//       openbaarmakingsdatum: "",
//       // identifiers?: string[];
//       // verantwoordelijke?: Identifier;
//       publisher: {
//         id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
//         label:"ministerie van Binnenlandse Zaken en Koninkrijksrelaties"
//       },
//       language: { id: "http://publications.europa.eu/resource/authority/language/NLD", label: "Nederlands" },
//       format: {
//         id: "verantwoordelijke",
//         // label: "verantwoordelijke",
//       },
//       // onderwerpen?: string[];
//       // omschrijvingen?: string[];
//       titelcollectie: {
//         officieleTitel: "title",
//         // verkorteTitels?: string[];
//         // alternatieveTitels?: string[];
//       },
//       classificatiecollectie: {
//         documentsoorten: [],
//         themas: [],
//         // trefwoorden?: string[];
//       },
//       // documenthandelingen?: Documenthandelingen[];
//       // created?: string;
//       // creator?: string;
//       // aggregatiekenmerken?: string[];
//       // extraMetadata?: ExtraMetadata[];
//       // pid?: string;
//     }
//   }
// };

const props = {
  data: [
    {
      meta: {
        document: {
        publisher: {
          id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
          label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
        },
        identifiers: ["test"],
        language: {
          id: "http://publications.europa.eu/resource/authority/language/NLD",
          label: "Nederlands",
        },
        titelcollectie: {
          officieleTitel: "title",
        },
        classificatiecollectie: {
          documentsoorten: [
            {
              id: "Soort1",
              label: "Soort1",
            },
          ],
          themas: [
            {
              id: "Thema1",
              label: "Thema1",
            },
          ],
        },
        documenthandelingen: [
          {
            soortHandeling: {
              id: "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfcee535",
              label: "ontvangst",
            },
            atTime: new Date(2222, 12, 2).toString(),
            wasAssociatedWith: {
              id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
              label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
            },
          },
        ],
      },
      file: new File(["(⌐□_□)"], "chucknorris.png", { type: "image/png" }),
    }
  },
  ],
};
function formatDate(date = new Date()) {
  return [
    date.getFullYear(),
    padTo2Digits(date.getMonth() + 1),
    padTo2Digits(date.getDate()),
  ].join("-");
}

function padTo2Digits(num: number) {
  return num.toString().padStart(2, "0");
}

it("Renders the CreateFormulierOrganism", () => {
  const { getByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const title = getByText("Gegevens bewerken");
  expect(title.parentElement).toBeInTheDocument();
});

// it("Submits the form", () => {
//   const { getByText } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <UpdateFormulierOrganism {...props}  />
//       </Provider>
//     </Router>
//   );

//   const button = getByText("Opslaan");
//   button.click();
//   // const text = getByText(/Document is geüpdatet/i);
//   // expect(text.parentElement).toBeInTheDocument();
// });

// it("Submits the form succesfull", async () => {
//   const {
//     getByText,
//     getByRole,
//     getByPlaceholderText,
//     getByDisplayValue,
//     getAllByText,
//   } = render(
//     <Router>
//       <Provider store={setupStore()}>
//         <UpdateFormulierOrganism {...props} />
//       </Provider>
//     </Router>
//   );

//   const title = getByRole("textbox", { name: /officiële titel/i });
//   await waitFor(() => fireEvent.click(title));
//   await waitFor(() =>
//     fireEvent.change(title, {
//       target: { value: "title" },
//     })
//   );

//   const identifier = getByRole("textbox", { name: /identifier/i });
//   await waitFor(() => fireEvent.click(identifier));
//   await waitFor(() =>
//     fireEvent.change(identifier, {
//       target: { value: "identifier" },
//     })
//   );

//   const Organisatie = getByPlaceholderText("Organisatie");
//   expect(Organisatie).toBeInTheDocument();
//   await waitFor(() => fireEvent.click(Organisatie));
//   await waitFor(() =>
//     fireEvent.change(Organisatie, {
//       target: { value: "newValue" },
//     })
//   );

//   const input = getAllByText(/^ontvangst$/i);
//   await waitFor(() => fireEvent.click(input[0]));
//   const dropdownOption = getByText(/^vaststelling$/i);
//   await waitFor(() => fireEvent.click(dropdownOption));

//   const date = formatDate(new Date(2222, 12, 2));
//   const dateInput = getByDisplayValue(date);
//   expect(dateInput).toBeInTheDocument();
//   await waitFor(() => fireEvent.focus(dateInput));
//   await waitFor(() =>
//     fireEvent.change(dateInput, { target: { value: "2020-05-12" } })
//   );

//   const thema = getByRole("textbox", { name: /thema's/i });
//   await waitFor(() => fireEvent.click(thema));
//   const themaOptions = getByText(/^media$/i);
//   await waitFor(() => fireEvent.click(themaOptions));

//   const docsoort = getByRole("textbox", { name: /documentsoort/i });
//   await waitFor(() => fireEvent.click(docsoort));
//   const docsoortOptions = getByText(/^brief$/i);
//   await waitFor(() => fireEvent.click(docsoortOptions));

//   await waitFor(() => fireEvent.blur(docsoortOptions));

//   const button = getByText("Opslaan");
//   button.click();
//   // const text = getByText(/Niet alle benodigde velden zijn ingevuld/i);
//   // expect(text.parentElement).toBeInTheDocument();
// });

it("Submits the form unsuccesfull", async () => {
  const { getByText, getByRole } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const textbox = getByRole("textbox", { name: /officiële titel/i });
  expect(textbox).toBeInTheDocument();
  await waitFor(() => fireEvent.click(textbox));
  await waitFor(() =>
    fireEvent.change(textbox, {
      target: { value: "" },
    })
  );
  await waitFor(() => fireEvent.blur(textbox));

  const button = getByText("Opslaan");
  button.click();
  // const text = getByText(/Niet alle benodigde velden zijn ingevuld/i);
  // expect(text.parentElement).toBeInTheDocument();
});

it("Set Identifier", async () => {
  const { getByRole } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const textbox = getByRole("textbox", { name: /identifier/i });
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
  const { getByPlaceholderText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const textbox = getByPlaceholderText("Organisatie");
  expect(textbox).toBeInTheDocument();
  await waitFor(() => fireEvent.click(textbox));
  await waitFor(() =>
    fireEvent.change(textbox, {
      target: { value: "newValue" },
    })
  );
  expect(textbox).toHaveValue("newValue");
});

it("Documenthandeling change handeling", async () => {
  const { getAllByText, getByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const input = getAllByText(/^ontvangst$/i);
  await waitFor(() => fireEvent.click(input[0]));

  let ondertekening = getAllByText(/^ondertekening$/i);
  expect(ondertekening.length).toBe(1);

  const dropdownOption = getByText(/^ondertekening$/i);
  await waitFor(() => fireEvent.click(dropdownOption));

  ondertekening = getAllByText(/^ondertekening$/i);
  expect(ondertekening.length).toBe(2);
});

it("Documenthandeling change atTime", async () => {
  const { getByDisplayValue } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );

  const date = formatDate(new Date(2222, 12, 2));
  const input = getByDisplayValue(date);
  expect(input).toBeInTheDocument();
  await waitFor(() => fireEvent.focus(input));
  await waitFor(() =>
    fireEvent.change(input, { target: { value: "2020-05-12" } })
  );
  expect(input).toHaveValue("2020-05-12");
});

it("add documenthandeling", async () => {
  const { getByText, getAllByText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );
  let handeling = getAllByText(/^ontvangst$/i);
  expect(handeling.length).toBe(2); //1 selected and 1 in dropdown

  const button = getByText(/Voeg handeling toe/i);
  expect(button).toBeInTheDocument();
  await waitFor(() => fireEvent.click(button));

  handeling = getAllByText(/^ontvangst$/i);
  expect(handeling.length).toBe(3);
});

it("remove documenthandeling", async () => {
  const { getByText, getAllByText, getAllByRole } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
      </Provider>
    </Router>
  );
  let handeling = getAllByText(/^ontvangst$/i);
  expect(handeling.length).toBe(2);

  const button = getByText(/Voeg handeling toe/i);
  expect(button).toBeInTheDocument();
  await waitFor(() => fireEvent.click(button));

  handeling = getAllByText(/^ontvangst$/i);
  expect(handeling.length).toBe(3); // 1 selected 2 dropdown

  const removeButton = getAllByRole("button", { name: /icon\-remove\-blue/i });
  await waitFor(() => fireEvent.click(removeButton[1]));

  handeling = getAllByText(/^ontvangst$/i);
  expect(handeling.length).toBe(2);
});

it("change begindatum", async () => {
  const { getByText, getByPlaceholderText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
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
  const { getByText, getByPlaceholderText } = render(
    <Router>
      <Provider store={setupStore()}>
        <UpdateFormulierOrganism {...props} />
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
