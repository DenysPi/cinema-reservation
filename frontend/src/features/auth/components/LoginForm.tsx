import { useState, type SubmitEvent } from 'react';
import { TextField } from "../../../shared/ui"
import { useLogin } from '../hooks/useLogin';

export default function LoginForm(){

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const {login, loading, error} = useLogin();

    function handleSubmit(event: SubmitEvent<HTMLFormElement>){
        event.preventDefault();
        login({email, password});
    }
    return (
        <form onSubmit={handleSubmit}>
            <TextField
                label="Email"
                type="email"
                value={email}
                onChange = {setEmail}
                required
            />

            <TextField 
                label='password'
                type='password'
                value={password}
                onChange={setPassword}
                required
            />

        </form>

    )
}