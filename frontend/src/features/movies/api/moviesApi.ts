import { http } from '../../../shared/api/httpClient';
import type { Movie } from '../types';

export const moviesApi = {
  getAll: () => http<Movie[]>('/movies'),
  getById: (id: number) => http<Movie>(`/movies/${id}`),
};