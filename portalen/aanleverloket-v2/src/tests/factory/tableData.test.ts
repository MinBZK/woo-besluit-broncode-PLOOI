import { MockSearchApi } from '../../api/search';
import { TableDataFactory } from '../../factories/tableData';

test('It creates tabledata', async () => {
    const f = new TableDataFactory();
    const s = await new MockSearchApi().searchOrgDocuments("");

    f.create(s);
});