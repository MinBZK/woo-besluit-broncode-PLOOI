import { ResponseCodesFactory } from '../../factories/responseCodes';

test('Document: It creates 400', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 400)).toBe('Het bestand kon niet verwerkt worden');
});

test('Document: It creates 401', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 401)).toBe('Deze handeling is alleen mogelijk voor geautoriseerden');
});
test('Document: It creates 403', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 403)).toBe('U bent niet geautoriseerd voor deze actie');
});

test('Document: It creates 404', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 404)).toBe('De opgevraagde resource bestaat niet');
});

test('Document: It creates 415', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 415)).toBe('Formaat niet ondersteund');
});

test('Document: It creates 500', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('documenten', 500)).toBe('Interne serverfout');
});



test('Meta: It creates 400', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('metadata', 400)).toBe('De aangeleverde metadata voldoen niet aan het schema');
});

test('Meta: It creates 404', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('metadata', 404)).toBe('De opgevraagde resource bestaat niet');
});

test('Meta: It creates 500', async () => {
    const f = new ResponseCodesFactory();
    expect(f.create('metadata', 500)).toBe('Interne serverfout');
});
