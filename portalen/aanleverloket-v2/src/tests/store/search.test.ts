import { ApiFactory } from "../../api"
import { setupStore } from "../../store";
import { searchDocuments } from "../../store/reducers/search";


ApiFactory.isTestSuite = true;

test('Returns search results', async () => {
    const store = setupStore();
    await store.dispatch(searchDocuments(10));

    expect(store.getState().search.data?.aantalResultaten).toBeGreaterThan(0);
});

test('Returns error on negative page', async () => {
    const store = setupStore();
    await store.dispatch(searchDocuments(-1));
    expect(store.getState().toast.type).toBe('error');
});

