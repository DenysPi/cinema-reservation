import { Link, useParams } from 'react-router-dom';
import { useMovie } from '../hooks/useMovie';

export default function MovieDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const { movie, error } = useMovie(Number(id));

  if (error) return <p>Error: {error}</p>;
  if (!movie) return <p>Loading...</p>;

  return (
    <div>
      <Link to="/movies">← Back</Link>
      <h1>{movie.title}</h1>
      <p>{movie.duration} minutes</p>
    </div>
  );
}