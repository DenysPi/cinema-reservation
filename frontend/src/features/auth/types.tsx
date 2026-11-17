
export type LoginRequest = {
    email: string;
    password: string
}


export type RegisterRequest = {
    email: string;
    password: string;
    firstName: string
}

export type TokenResponse = {
    accessToken: string
}