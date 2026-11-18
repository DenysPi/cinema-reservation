import { Navigate, Route, Routes } from 'react-router-dom';
import { MovieDetailsPage, MoviesPage } from '../features/movies';
import LoginPage from '../features/auth/pages/LoginPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/movies" replace />} />
      <Route path="/movies" element={<MoviesPage />} />
      <Route path="/movies/:id" element={<MovieDetailsPage />} />
      <Route path='/login' element={<LoginPage />}/>
    </Routes>
  );
}


