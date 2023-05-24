import { WaardelijstCollection } from '../../factories/documentsorten';

test('It adds item to collection', async () => {
    const s = new WaardelijstCollection();
    s.addItem({ item: {id: '1'} });
    expect(s.getCount()).toBe(1);
});

test('It adds items to collection', async () => {
    const s = new WaardelijstCollection();
    s.addItems([{ item: {id: '1'} }]);
    expect(s.getCount()).toBe(1);
});


test('It iterates collections', async () => {
    const s = new WaardelijstCollection();
    s.addItem({ item: {id: '1'} });
    const iterator = s.getIterator();
    
    do{
        const item = iterator.current();
        expect(item).not.toBeNull();
    }
    while(iterator.next() && iterator.valid())
    
});

test('It rewinds collections', async () => {
    const s = new WaardelijstCollection();
    s.addItem({ item: {id: '1'} });
    const iterator = s.getIterator();
    
    do{
        const item = iterator.current();
        expect(item).not.toBeNull();
    }
    while(iterator.next() && iterator.valid());

    iterator.rewind();
    expect(iterator.current().item.id).toBe('1');
    
});