import { MetadataFactory } from '../../factories/metadata';

test('It creates responsecodes', async () => {
    const f = new MetadataFactory().create({ id: '21' }, [], {id: '1'}, {officieleTitel: 'Test'}, {documentsoorten: [], themas: []}, []);
    expect(f.document.titelcollectie.officieleTitel).toBe('Test');
});