import { Navigate, Route, Routes } from 'react-router-dom';
import { MovieDetailsPage, MoviesPage } from '../features/movies';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/movies" replace />} />
      <Route path="/movies" element={<MoviesPage />} />
      <Route path="/movies/:id" element={<MovieDetailsPage />} />
    </Routes>
  );
}


