import { http } from '../../../shared/api/httpClient';
import type {LoginRequest,TokenResponse, RegisterRequest} from '../types';

export const authApi = {
    login: (request: LoginRequest) =>
        http<TokenResponse>('/auth/login', {
            method: 'POST',
            body: JSON.stringify(request),
        }),
    
    register: (request: RegisterRequest) =>
        http<void>('auth/register', {
            method: 'POST',
            body: JSON.stringify(request),
        }),
    
}