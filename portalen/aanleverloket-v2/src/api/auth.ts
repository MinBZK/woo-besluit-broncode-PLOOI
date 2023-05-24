const mockJwt = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

export interface IAuthApi
{
    Login(): void;
    GetToken(code: string): Promise<string>;
};

export class MockAuthApi implements IAuthApi
{
    public Login() { }    
    public GetToken(code: string): Promise<string> {
        window.location.search = "code=1234-1234"
        return Promise.resolve(mockJwt);
    }
    
}

export class AuthApi implements IAuthApi {
    private readonly api: string;
    constructor() { this.api = process.env.REACT_APP_AUTH_API as string; }

    public Login() {
        const loginUrl = new URL(this.api);
        // loginUrl.pathname = "auth/login";
        loginUrl.searchParams.append("redirect-uri", window.location.href);

        window.location.href = loginUrl.toString();
    }

    public async GetToken(code: string): Promise<string> {
        const loginUrl = new URL(this.api);

        loginUrl.pathname = "auth/token";

        loginUrl.searchParams.append("code", code);

        const response = await fetch(loginUrl.toString(), { method: 'POST' });
        if (!response.ok)
            throw new Error("Code expired");

        return response.text();
    }

    // public ValidateToken(token:string)
    // {

    // }
}