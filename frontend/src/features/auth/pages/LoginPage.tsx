import { Link } from "react-router-dom";
import LoginForm from "../components/LoginForm";


export default function LoginPage(){

    return (
        <div>
            <h1>Login in</h1>
            <LoginForm/>
            <p>
                No account? <Link to="register">Register</Link>
            </p>
        </div>
    )
}