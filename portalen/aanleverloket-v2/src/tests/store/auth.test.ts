import { ApiFactory } from "../../api"
import { setupStore } from "../../store";
import { useAppSelector } from "../../store/hooks";
import { storeToken, getToken, login, logout, redirectLogin } from "../../store/reducers/auth";
import { selectAuth } from "../../store/selectors";
import { StorageLocation, Storage } from "../../utils/Storage";

ApiFactory.isTestSuite = true;
const mockJwt = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

test('Logs in user', async () => {
    const store = setupStore();
    await store.dispatch(storeToken(mockJwt));
    await store.dispatch(login());

    expect(store.getState().auth.isAuthenticated).toBeTruthy();
});

test('Logs out user', async () => {
    const store = setupStore();
    await store.dispatch(logout());

    expect(store.getState().auth.isAuthenticated).toBeFalsy();
});

test('Get parameter JWT', async () => {
    const store = setupStore();
    await store.dispatch(storeToken("ABC"));

    const token = Storage.Get(StorageLocation.TOKEN);
    expect(token).toBe("ABC");
});

test('Get Token', async () => {
    const store = setupStore();
    await store.dispatch(getToken("ABC"));

    const token = Storage.Get(StorageLocation.TOKEN);
    expect(token?.substring(0, 2)).toBe("ey");
});

test('Redirects Login', async () => {
    const store = setupStore();
    await store.dispatch(redirectLogin());

    const token = Storage.Get(StorageLocation.TOKEN);
    expect(token).toBeNull();
});

