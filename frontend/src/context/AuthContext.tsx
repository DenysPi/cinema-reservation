import { createContext, use, useEffect, useState, type ReactNode } from "react";
import  {usersApi, type User } from "../features/users";
import { authApi } from "../features/auth/api/authApi";
import type { LoginRequest } from "../features/auth/types";


type AuthContextValue = {
    token: string | null;
    user: User | null;
    login: (request: LoginRequest) => Promise<void>;
    logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider(props: {children: ReactNode}){
    const [token, setTokenState] = useState<string | null>(() => localStorage.getItem("token"));
    const [user, setUser] = useState<User | null>(null);


    async function login(request: LoginRequest) {
        const {accessToken} = await authApi.login(request);
        localStorage.setItem("token", accessToken);
        setTokenState(accessToken);     
    }

    function logout(){
        localStorage.removeItem('token')
        setTokenState(null);
        setUser(null);
    }

    useEffect(() =>{
        if(!token) return;
        usersApi.me().then(setUser).catch(logout);

    }, [token])

    useEffect(() =>{
        window.addEventListener("auth:logout", logout);
        return (() => window.removeEventListener("auth:logout", logout));
    }, [])

    return (
        <AuthContext.Provider value={{token, user, login, logout}}>
            {props.children}
        </AuthContext.Provider>
    )
}


export function useAuth(){
    const context = use(AuthContext);
    if(!context){
        throw new Error("useAuth must be used inside <AuthProvider>")
    }
    return context;
}