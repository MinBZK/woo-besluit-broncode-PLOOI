import { IFileValidator, IListValidator, ITextValidator } from "../models/validator";

export class RequiredValidator implements ITextValidator
{
    validate(value?: string): string | undefined {
        if (!value || !value.length)
            return "Dit is een verplicht veld";
    }
}

export class RequiredListValidator implements IListValidator
{
    validate(value?: any[]): string | undefined {
        if (!value || !value.length)
            return "Dit is een verplicht veld";
    }
}

export class RequiredFileValidator implements IFileValidator
{
    validate(file?: File): string | undefined {
        if (!file || !file.name)
            return "Selecteer een bestand.";
    }
}