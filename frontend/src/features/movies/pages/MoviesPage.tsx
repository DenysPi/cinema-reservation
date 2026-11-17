import { useState } from 'react';
import { useMovies } from '../hooks/useMovies';
import MovieCard from '../components/MovieCard';

export default function MoviesPage() {
  const { movies, loading, error } = useMovies();
  const [onlyLong, setOnlyLong] = useState(false);

  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;

  const visibleMovies = onlyLong ? movies.filter((m) => m.duration > 150) : movies;

  return (
    <div>
      <h1>Movies</h1>
      <button onClick={() => setOnlyLong(!onlyLong)}>
        {onlyLong ? 'Show all' : 'Only long movies'}
      </button>
      <ul>
        {visibleMovies.map((movie) => (
          <MovieCard key={movie.movieId} movie={movie} />
        ))}
      </ul>
    </div>
  );
}