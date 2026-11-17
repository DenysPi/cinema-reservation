import { useEffect, useState } from 'react';
import { moviesApi } from '../api/moviesApi';
import type { Movie } from '../types';

export function useMovie(id: number) {
  const [movie, setMovie] = useState<Movie | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setMovie(null);
    setError(null);
    moviesApi.getById(id)
      .then(setMovie)
      .catch((e: Error) => setError(e.message));
  }, [id]);

  return { movie, error };
}