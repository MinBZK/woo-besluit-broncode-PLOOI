import { ApiFactory } from "../../api";
import { mockDocument } from "../../api/metadata";
import { Metadata } from "../../models";
import { setupStore } from "../../store";
import {
  unpublish,
  fetchForm,
  updateMetadata,
  uploadForm,
} from "../../store/reducers/metadata";

ApiFactory.isTestSuite = true;
const file = new File(["foo"], "foo.txt", {
  type: "text/plain",
});

const meta: Metadata = {
  document: {
    publisher: {
      id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
      label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
    },
    identifiers: ["identifiers"],
    language: {
      id: "http://publications.europa.eu/resource/authority/language/NLD",
      label: "Nederlands",
    },
    titelcollectie: {
      officieleTitel: "titel",
    },
    classificatiecollectie: { documentsoorten: [], themas: [] },
    documenthandelingen: [
      {
        soortHandeling: { id: "" },
        atTime: new Date().toJSON(),
        wasAssociatedWith: {
          id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
          label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
        },
      },
    ],
  },
};

test("Uploads a document", async () => {
  const store = setupStore();

  await store.dispatch(uploadForm());

  expect(store.getState().toast.type).toBe("success");
});

test("Fetches metadata", async () => {
  const store = setupStore();

  await store.dispatch(fetchForm("123"));
  expect(store.getState().meta.data).not.toBeNull();
});

test("Depubliceert metadata", async () => {
  const store = setupStore();

  await store.dispatch(unpublish("123"));
  expect(store.getState().meta.data).toBeUndefined();
});

test("Updates metadata", async () => {
  const store = setupStore();

  await store.dispatch(
    updateMetadata({
      ...mockDocument,
      document: { ...mockDocument.document, label: "SUCCESS" },
    })
  );

  expect(store.getState().toast.message?.message).toBe("Metadata is geüpdatet");
});
