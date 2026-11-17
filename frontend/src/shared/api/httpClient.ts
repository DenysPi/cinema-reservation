import { API_URL } from '../config/env';
import { ApiError } from './ApiError';


export async function http<T>(path: string, options: RequestInit = {}): Promise<T> {

    const token = localStorage.getItem('token');

    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...options.headers,
        },
    });

    if (!response.ok){
        const body = await response.json().catch(()=> ({}))
        throw new ApiError(response.status, body.message ?? `Request failed (${response.status})`);
    }

    const text = await response.text();
    return (text ? JSON.parse(text) : undefined) as T;
}