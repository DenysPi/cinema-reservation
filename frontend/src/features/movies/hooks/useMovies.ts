import { useEffect, useState } from 'react';
import { moviesApi } from '../api/moviesApi';
import type { Movie } from '../types';

export function useMovies() {
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    moviesApi.getAll()
      .then(setMovies)
      .catch((e: Error) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return { movies, loading, error };
}