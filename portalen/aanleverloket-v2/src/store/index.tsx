import { combineReducers, configureStore, PreloadedState } from '@reduxjs/toolkit';
import { AuthReducer, ToastReducer, MetadataReducer, SearchReducer } from './reducers';

const rootReducer = combineReducers({
  auth: AuthReducer,
  toast: ToastReducer,
  meta: MetadataReducer,
  search: SearchReducer
});

export function setupStore(preloadedState?: PreloadedState<RootState>) {
  return configureStore({
    reducer: rootReducer,
    preloadedState
  })
}

export type RootState = ReturnType<typeof rootReducer>
export type AppStore = ReturnType<typeof setupStore>
export type AppDispatch = AppStore['dispatch'];