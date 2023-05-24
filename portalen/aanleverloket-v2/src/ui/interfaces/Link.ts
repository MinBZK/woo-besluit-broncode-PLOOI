export interface ILink {
    href: string;
    text: string;
    lang?: string;
    newWindow?:boolean;
    selected?: boolean;
}

export interface IExternalLink extends ILink{
    external?: boolean;
}

