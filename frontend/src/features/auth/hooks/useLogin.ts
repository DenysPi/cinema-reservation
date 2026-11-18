import {useState } from "react";
import { useNavigate } from "react-router-dom";

import type { LoginRequest } from "../types";
import { ApiError } from "../../../shared/api/ApiError";
import { useAuth } from "../../../context/AuthContext";

export function useLogin(){

    const {login: authLogin} = useAuth();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    async function login(request: LoginRequest){

        setLoading(true)
        setError(null)

        try{
            await authLogin(request);
           
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