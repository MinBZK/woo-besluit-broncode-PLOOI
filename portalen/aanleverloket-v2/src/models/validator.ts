export interface ITextValidator
{
    validate(value?:string): string | undefined;
}

export interface IListValidator
{
    validate(values?:any[]): string | undefined;
}

export interface IFileValidator
{
    validate(file?:File): string | undefined;
}