import { StringSanitizer } from '../../utils/StringSanitizer';

it('Ensures a correct endpoint', () => {
    const sanitizer = new StringSanitizer();
    const url = sanitizer.sanitizeApiEndpoint('https://api.com/', 'documenten', '10', 'testId=10');

    expect(url).toBe('https://api.com/documenten/10?testId=10');
});

it('Ensures a correct label in waardelijst item', () => {
    const sanitizer = new StringSanitizer();
    const url = sanitizer.sanitizeWaardelijstItem({
        id: 'https://id.com/id/10',
        label: 'Test (remove)'
    });

    expect(url.label).toBe('Test');
});