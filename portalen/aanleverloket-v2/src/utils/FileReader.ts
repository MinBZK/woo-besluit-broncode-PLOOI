// const MB = 1048576;
//const MB = 1000000;

export class FileReaderUtil {
    private readonly file: File;

    constructor(f: File) {
        this.file = f;
    }

    public ToBinaryString = (): Promise<ArrayBuffer> => new Promise((resolve, reject) => {
        const fileReader = new FileReader();

        fileReader.onerror = (err) => reject(err);        
        fileReader.readAsArrayBuffer(this.file);
        fileReader.onload = (event) => {
            if (event.target?.result)
            {
                resolve(event.target.result as ArrayBuffer);
            }
            else
            {
                reject('[FileConverter] File conversion error');
            }
        };
    });

    public exceedsMaxFileSize = (maxFileSize:number) => this.file.size > (maxFileSize);
}
