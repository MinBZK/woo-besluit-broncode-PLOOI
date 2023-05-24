import { IFileValidator } from "../models/validator";

export class MaxFileSizeValidator implements IFileValidator
{
    _maxSize: number;
    constructor(maxSize: number)
    {
        this._maxSize = maxSize;
    }

    validate(file?: File | undefined): string | undefined {
        if(file && file.size > this._maxSize)
            return "Bestand is te groot";
    }

}