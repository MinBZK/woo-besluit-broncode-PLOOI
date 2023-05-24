import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ApiFactory } from "../../api";
import { Storage, StorageLocation } from "../../utils/Storage";
import jwt_decode from "jwt-decode";
import {WaardelijstCrawler} from "../../utils/WaardelijstCrawler";

interface AuthState {
  isAuthenticated: boolean;
  fetching: boolean;
  jwt?: string;
  organizations: {id:string, label:string}[];
  roles: string[];
  error?: string;
}

const initialState: AuthState = {
  fetching: false,
  isAuthenticated: false,
  organizations: [],
  roles: []
};

export const redirectLogin = createAsyncThunk(
  "auth/redirectLogin",
  async () => {
    Storage.Clear(StorageLocation.TOKEN);
    ApiFactory.createAuthApi().Login();
  }
);

export const login = createAsyncThunk("auth/login", async () => {
  const token = Storage.Get(StorageLocation.TOKEN);
  if (token) {
    const decoded = jwt_decode(token);
    return decoded;
  }

  return undefined;
});

export const logout = createAsyncThunk(
  "auth/logout",
   async () => {
     Storage.Clear(StorageLocation.TOKEN);
  }
);

export const storeToken = createAsyncThunk(
  "auth/getParameterJWT",
  async (code: string, thunkAPI) => {
    Storage.Set(StorageLocation.TOKEN, code);
    window.location.href = window.location.href.replace(`?access_token=${code}`, "");
    await thunkAPI.dispatch(login());
  }
);

export const getToken = createAsyncThunk(
  "auth/getToken",
  async (code: string, thunkAPI) => {
    const token = await ApiFactory.createAuthApi().GetToken(code);
    Storage.Set(StorageLocation.TOKEN, token);

    window.location.href = window.location.href.replace(`?code=${code}`, "");
    await thunkAPI.dispatch(login());
  }
);

const authState = createSlice({
  name: "auth",
  initialState,
  reducers: {},
  extraReducers: (caseBuilder) => {
    caseBuilder.addCase(login.fulfilled, (state, action:PayloadAction<any>) => {
      state.fetching = false;
      state.isAuthenticated = true;
      state.roles = action.payload["roles"];
      state.organizations = action.payload["org"].map((org: any) => WaardelijstCrawler.searchOrganisationLabel(org));
    });
    caseBuilder.addCase(logout.fulfilled, (state, action) => {
      state.fetching = false;
      state.isAuthenticated = false;
      state.organizations = [];
      state.roles = [];
    });

  },
});

export default authState.reducer;
