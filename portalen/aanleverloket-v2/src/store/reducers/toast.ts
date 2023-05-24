import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { ToastType } from '../../ui/interfaces/Toast';

interface ToastMessage {
    // title: string;
    message: string;

}
interface ToastState {
    message?: ToastMessage;
    type?: ToastType;
    autoClose: boolean;
};

const initialState: ToastState = {
    autoClose: true,
    // message: {
    //     title: 'Error 500',
    //     message: 'Oeps, er is iets mis eggaan!'
    // },
    // type: 'success'
};

let timer: any;

export const setToast = createAsyncThunk(
    'toast/setToast',
    async (action: { type: ToastType, message: ToastMessage, autoClose: boolean }, thunkAPI) => {
        clearTimeout(timer);
        
        thunkAPI.dispatch(_setToast(action));

        if (action.autoClose)
            timer = setTimeout(() => {
                thunkAPI.dispatch(clearToast());
            }, 6000);
    }
);

const authState = createSlice({
    name: 'toast',
    initialState,
    reducers: {
        _setToast: (state, action: PayloadAction<ToastState>) => {
            state.message = action.payload.message;
            state.type = action.payload.type;
            state.autoClose = action.payload.autoClose;
        },
        clearToast: (state) => {
            clearTimeout(timer);
            state.message = undefined;
            state.type = undefined;
        }
    },
});

export const { clearToast, _setToast } = authState.actions;
export default authState.reducer;