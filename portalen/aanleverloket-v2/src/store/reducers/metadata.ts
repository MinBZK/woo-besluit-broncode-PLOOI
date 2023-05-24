import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ApiFactory } from "../../api";
import { MetadataFactory } from "../../factories";
import { FileFormat, Identifier, Metadata } from "../../models";
import { formatDate } from "../../utils/DateFormatter";
import { FileReaderUtil } from "../../utils/FileReader";
import { setToast } from "./toast";

const mdFactory = new MetadataFactory();

interface MetadataState {
  data: FormState[];
  formIndex: number;
  fetching: boolean;
  mode: "create" | "update";
}

const generateMetadata = (title: string, organization: Identifier) => {
  return mdFactory.create(
    organization,
    [],
    {
      id: "http://publications.europa.eu/resource/authority/language/NLD",
      label: "Nederlands",
    },
    {
      officieleTitel: title,
    },
    {
      documentsoorten: [],
      themas: [],
    },
    [
      {
        soortHandeling: { id: "" },
        atTime: new Date().toJSON(),
        wasAssociatedWith: organization,
      },
    ]
  );
}

const validateForm = (data:FormState): boolean => {
  const requiredValues = [
    data.meta.document.identifiers[0],
    data.meta.document.language.id,
    data.meta.document.titelcollectie.officieleTitel,
    data.meta.document.classificatiecollectie.documentsoorten,
    data.file?.name
  ];

  const geldigheidBegindatum = data.meta.document.geldigheid?.begindatum;
  const geldigheidEinddatum = data.meta.document.geldigheid?.einddatum;

  if (geldigheidBegindatum || geldigheidEinddatum) {
    requiredValues.push(geldigheidEinddatum ? formatDate(geldigheidEinddatum) : "");
    requiredValues.push(geldigheidBegindatum ? formatDate(geldigheidBegindatum) : "");
  }

  return requiredValues.every(v => v && v.length > 0);
};

export interface FormState {
  file?: File;
  meta: Metadata;
}

const initialState: MetadataState = {
  data: [],
  fetching: false,
  mode: "create",
  formIndex: 0
};

export const uploadForm = createAsyncThunk(
  "metadata/create",
  async (
    _,
    thunkAPI
  ) => {
    const metadata_api = ApiFactory.createMetadataApi();
    const document_api = ApiFactory.createDocumentApi();

    const state: MetadataState = (thunkAPI.getState() as any).meta;
    const hoofd_document: FormState = state.data[0];

    for (let i = 0; i < state.data.length; i++) {
      const element = state.data[i];
      if(!validateForm(element)){
        console.log("req velden niet ingevuld")
        return //error message
      }
    }

    let reader;
    if(hoofd_document.file)
      reader = new FileReaderUtil(hoofd_document.file);
    else 
      return;

    try {
      debugger;
      const createResult = await metadata_api.createMetadataSession(hoofd_document.meta);
      const fileBinary = await reader.ToBinaryString();

      const hoofdFileType = hoofd_document.file?.name && hoofd_document.file?.name.indexOf('pdf') > -1 ? { id: "http://publications.europa.eu/resource/authority/file-type/PDF", label: 'PDF' } : { id: "http://publications.europa.eu/resource/authority/file-type/ZIP", label: 'ZIP' };
      await document_api.uploadDocument(createResult.uploadUrl, hoofdFileType.label as FileFormat, fileBinary);
      
      for (let i = 1; i < state.data.length; i++) {
        let bijlage = state.data[i];
        if (!bijlage.file)
          return;

        // bijlage.meta.documentrelaties = {
        //   relation: createResult.pid,
        //   role: "https://identifier.overheid.nl/tooi/def/thes/kern/c_4d1ea9ba"
        // };

        const _bijlage: FormState = {...bijlage,  meta: {...bijlage.meta, documentrelaties: {
          relation: createResult.pid,
          role: "https://identifier.overheid.nl/tooi/def/thes/kern/c_4d1ea9ba"
        }}};

        const bijlageReader = new FileReaderUtil(bijlage.file);
        const bijlageBinary = await bijlageReader.ToBinaryString();

        const bijlageFileType = bijlage.file?.name && hoofd_document.file?.name.indexOf('pdf') > -1 ? { id: "http://publications.europa.eu/resource/authority/file-type/PDF", label: 'PDF' } : { id: "http://publications.europa.eu/resource/authority/file-type/ZIP", label: 'ZIP' };
        const createBijlageResult = await metadata_api.createMetadataSession(_bijlage.meta);
        await document_api.uploadDocument(createBijlageResult.uploadUrl,bijlageFileType.label as FileFormat, bijlageBinary);
      }

      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "success",
          message: {
            message: "Document is aangeleverd",
          },
        })
      );
    } catch (e: any) {
      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "error",
          message: {
            message: e.message,
          },
        })
      );
      throw e;
    }
  }
);

export const replaceDocument = createAsyncThunk(
  "document/update",
  async (
    args: {
      pid: string;
      file: File;
    },
    thunkAPI
  ) => {
    const document_api = ApiFactory.createDocumentApi();
    const reader = new FileReaderUtil(args.file);

    try {
      const fileBinary = await reader.ToBinaryString();

      const fileType = args.file?.name && args.file?.name.indexOf('pdf') > -1 ? { id: "http://publications.europa.eu/resource/authority/file-type/PDF", label: 'PDF' } : { id: "http://publications.europa.eu/resource/authority/file-type/ZIP", label: 'ZIP' };
      await document_api.updateDocument(`${process.env.REACT_APP_DOCUMENT_API_ENDPOINT}/documenten/${args.pid.split("/").pop()}`, fileType.label as FileFormat, fileBinary);

      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "success",
          message: {
            message: "Document is aangeleverd",
          },
        })
      );
    } catch (e: any) {
      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "error",
          message: {
            message: e.message,
          },
        })
      );
      throw e;
    }
  }
);

export const fetchForm = createAsyncThunk(
  "metadata/get",
  async (id: string, thunkAPI) => {
    const api = ApiFactory.createMetadataApi();

    try {
      const results = await api.getMetadataSession(id);
      return results;
    } catch (e: any) {
      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "error",
          message: {
            message: e.message,
          },
        })
      );
      throw e;
    }
  }
);

export const unpublish = createAsyncThunk(
  "metadata/delete",
  async (id: string, thunkAPI) => {
    const api = ApiFactory.createDocumentApi();
    try {
      await api.depubliceerSession(id);

      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "success",
          message: {
            message: "Document is gedepubliceerd",
          },
        })
      );
    } catch (e: any) {
      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "error",
          message: {
            message: e.message,
          },
        })
      );
      throw e;
    }
  }
);

export const updateMetadata = createAsyncThunk(
  "metadata/update",
  async (data: Metadata, thunkAPI) => {
    const api = ApiFactory.createMetadataApi();

    try {
      const result = await api.updateMetadataSession(data, data.document.pid?.split("/").pop() ?? "");

      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "success",
          message: {
            message: "Metadata is geüpdatet",
          },
        })
      );

      return result;
    } catch (e: any) {
      thunkAPI.dispatch(
        setToast({
          autoClose: true,
          type: "error",
          message: {
            message: e.message,
          },
        })
      );
      throw e;
    }
  }
);

export const addForm = createAsyncThunk(
  "metadata/addForm",
  (_, thunkAPI) => {
    const state: any = thunkAPI.getState();
    const org = state.auth.organizations[0];

    const title = state.meta.data.length === 0
      ? "HoofdDocument"
      : `Bijlage ${state.meta.data.length}`;

    const form = generateMetadata(title, org);

    thunkAPI.dispatch(_addForm({
      meta: form
    }));
  }
);

export const removeForm = createAsyncThunk(
  "metadata/removeForm",
  (index: number, thunkAPI) => {
    thunkAPI.dispatch(_removeForm(index));
  }
);

const metadataState = createSlice({
  name: "metadata",
  initialState,
  reducers: {
    setMode: (state, action: PayloadAction<'update' | 'create'>) => {
      state.mode = action.payload;
    },
    updateForm: (state, action: PayloadAction<FormState>) => {
      state.data[state.formIndex] = action.payload;
    },
    _addForm: (state, action: PayloadAction<FormState>) => {
      state.data.push(action.payload);
      state.formIndex = state.data.length - 1;
    },
    _removeForm: (state, action: PayloadAction<number>) => {
      state.data.splice(action.payload, 1);
      if (state.data.length <= state.formIndex)
        state.formIndex = state.data.length - 1;
    },
    clear: (state) => {
      state.data = [];
      state.formIndex = 0;
    },
    changeFormIndex: (state, action: PayloadAction<number>) => {
      state.formIndex = action.payload;
    }
  },
  extraReducers: (caseBuilder) => {
    caseBuilder.addCase(uploadForm.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(uploadForm.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(uploadForm.fulfilled, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(replaceDocument.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(replaceDocument.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(replaceDocument.fulfilled, (state, action) => {
      state.fetching = false;
    });

    caseBuilder.addCase(fetchForm.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(fetchForm.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(fetchForm.fulfilled, (state, action) => {
      state.fetching = false;
      state.data = action.payload as any;
    });
    caseBuilder.addCase(updateMetadata.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(updateMetadata.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(updateMetadata.fulfilled, (state, action) => {
      state.fetching = false;
      state.data = action.payload as any;
    });
    caseBuilder.addCase(unpublish.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(unpublish.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(unpublish.fulfilled, (state, action) => {
      state.fetching = false;
      state.data = [];
    });
  },
});

const { _addForm, _removeForm } = metadataState.actions;
export const { setMode, updateForm, clear, changeFormIndex } = metadataState.actions;

export default metadataState.reducer;
