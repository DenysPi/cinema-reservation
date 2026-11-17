export type Role = 'USER' | 'ADMIN'

export type User = {
    id: number;
    firstName: string;
    email: string;
    role: Role
}