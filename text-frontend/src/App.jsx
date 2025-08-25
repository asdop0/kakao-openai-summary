import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import KakaoCallback from './pages/KakaoCallback';
import Dashboard from './pages/Dashboard';
import TopBar from './components/TopBar';
import SummaryForm from './pages/SummaryForm';
import SummaryDetail from './pages/SummaryDetail';
import './App.css'

const AppRouter = () => {
  const location = useLocation();
  const hideTopBarPaths = ["/oauth/callback"];

  return (
    <>
      {!hideTopBarPaths.includes(location.pathname) && <TopBar />}
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/oauth/callback" element={<KakaoCallback />} />
        <Route path="/summaries/new" element={<SummaryForm />} />
        <Route path="/summaries/detail/:id" element={<SummaryDetail />} />
      </Routes>
    </>
  );
};

export default function App() {
  return (
    <Router>
      <AppRouter />
    </Router>
  );
}
