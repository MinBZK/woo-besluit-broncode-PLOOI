export enum StorageLocation
{
    TOKEN = "token"
}

export class Storage
{
    public static Set = (location: StorageLocation, value: string) => localStorage.setItem(location, value);
    public static Clear = (location: StorageLocation) => localStorage.removeItem(location);
    public static Get = (location: StorageLocation) =>  localStorage.getItem(location);
}