import { type MovieCardProps } from "../types";
import { Link } from "react-router-dom";


export default function MovieCard({movie}: MovieCardProps){
    return (
        <li>
            <Link to={`/movies/${movie.movieId}`}>
                <strong>{movie.title}</strong>
            </Link>{' '}
            ({movie.duration} min) 
        </li>
    );
}