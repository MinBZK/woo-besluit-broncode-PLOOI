import { setupStore } from "../../store";
import { setToast, clearToast, _setToast } from "../../store/reducers/toast";


test('Sets error toast', async () => {
    const store = setupStore();
    await store.dispatch(setToast({
        type: 'error',
        autoClose: true,
        message: {
            message: ''
        }
    }));

    expect(store.getState().toast.type).toBe('error');
});

test('Sets success toast', async () => {
    const store = setupStore();
    await store.dispatch(setToast({
        type: 'success',
        autoClose: true,
        message: {
            message: ''
        }
    }));

    expect(store.getState().toast.type).toBe('success');
});

test('Sets info toast', async () => {
    const store = setupStore();
    await store.dispatch(setToast({
        type: 'info',
        autoClose: true,
        message: {
            message: ''
        }
    }));

    expect(store.getState().toast.type).toBe('info');
});


test('Sets info toast', async () => {
    const store = setupStore();
    store.dispatch(_setToast({
        type: 'info',
        autoClose: true,
        message: {
            message: ''
        }
    }));

    expect(store.getState().toast.type).toBe('info');
});


test('Clears toast', async () => {
    const store = setupStore();
    store.dispatch(clearToast());
    expect(store.getState().toast.type).toBeUndefined();
});
