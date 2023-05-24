import { RootState } from ".";

export const selectAuth = (state: RootState) => state.auth;
export const selectToast = (state: RootState) => state.toast;
export const selectMetadata = (state: RootState) => state.meta;
export const selectSearch = (state: RootState) => state.search;