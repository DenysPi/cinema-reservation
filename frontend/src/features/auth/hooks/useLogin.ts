import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../api/authApi";
import type { LoginRequest } from "../types";
import { ApiError } from "../../../shared/api/ApiError";

export function useLogin(){

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    async function login(request: LoginRequest){

        setLoading(true)
        setError(null)

        try{
            const {accessToken} = await authApi.login(request);
            localStorage.setItem('token', accessToken);
            navigate('/movies');
        }catch(e){
            setError(e instanceof ApiError && e.status === 401
                ? 'invalid email or password'
                : "Something went wrong, please try again"
            )
        }finally{
            setLoading(false);
        }
    
    }
    return {login, loading, error};
}