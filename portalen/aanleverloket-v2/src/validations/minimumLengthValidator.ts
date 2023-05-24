import { IListValidator, ITextValidator } from "../models/validator";

export class MinimumLengthValidator implements ITextValidator
{
    private _minLength = 0;

    constructor(length: number)
    {
        this._minLength = length;
    }

    validate(value?: string): string | undefined {
        if (value!.length > 0 && value!.length < this._minLength)
            return `Een minimum van ${this._minLength} karakters is vereist.`;        
    }
}

export class MinimumSelectedValidator implements IListValidator
{
    private _minLength = 0;

    constructor(length: number)
    {
        this._minLength = length;
    }

    validate(values?: any[]): string | undefined {
        if (!values || !values.length || values.length < this._minLength)
            return `Selecteers minstens ${this._minLength} items(s).`;
    }

}