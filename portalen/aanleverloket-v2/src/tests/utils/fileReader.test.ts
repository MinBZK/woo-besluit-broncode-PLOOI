import { FileReaderUtil } from '../../utils/FileReader';

// it('Converts file to binary', async () => {
//     const file = new File(["foo"], "foo.txt", {
//         type: "text/plain",
//     });

//     const fr = new FileReaderUtil(file);
//     const result = await fr.ToBinaryString();

//     expect(result).toBe("foo");
// });

it('Does not exceed max file size', async () => {
    const file = new File(["foo"], "foo.txt", {
        type: "text/plain",
    });

    const fr = new FileReaderUtil(file);
    const result = await fr.exceedsMaxFileSize(3);

    expect(result).toBe(false);
});

it('Exceeds max file size', async () => {
    const file = new File(["foo"], "foo.txt", {
        type: "text/plain",
    });

    const fr = new FileReaderUtil(file);
    const result = await fr.exceedsMaxFileSize(1);

    expect(result).toBe(true);
});
