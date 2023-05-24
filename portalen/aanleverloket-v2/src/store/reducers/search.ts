import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { ApiFactory } from "../../api";
import { Result, SearchResults } from "../../models/search-result";
import { setToast } from "./toast";

interface SearchState {
  data?: SearchResults;
  fetching: boolean;
}

const initialState: SearchState = {
  fetching: false,
};

export const searchDocuments = createAsyncThunk(
  "search/get",
  async (page: number, thunkAPI) => {
    const api = ApiFactory.createSearchApi();

    try {      
      const results =  await api.searchOrgDocuments(
        "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
        page
      );
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

const searchState = createSlice({
  name: "search",
  initialState,
  reducers: {},
  extraReducers: (caseBuilder) => {
    caseBuilder.addCase(searchDocuments.pending, (state, action) => {
      state.fetching = true;
    });
    caseBuilder.addCase(searchDocuments.rejected, (state, action) => {
      state.fetching = false;
    });
    caseBuilder.addCase(searchDocuments.fulfilled, (state, action) => {
      state.fetching = false;

      // Bas Check
      if(state.data !== undefined && state.data!._embedded.resultaten.length >= 0){
        const em = state.data._embedded.resultaten;
        const results:Result[] = [...em, ...action.payload._embedded.resultaten] as Result[];
        action.payload._embedded.resultaten = results;
      }

      state.data = action.payload as any;
    });
  },
});

export default searchState.reducer;
