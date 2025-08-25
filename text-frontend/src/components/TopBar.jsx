import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import './TopBar.css';

const TopBar = () => {
  const navigate = useNavigate();
  const accessToken = localStorage.getItem("accessToken");

  const REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY;
  const REDIRECT_URI = 'http://localhost:5173/oauth/callback';

  const handleLogin = () => {
    const kakaoAuthURL = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;
    window.location.href = kakaoAuthURL;
  };

  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem("refreshToken");
      await api.post("/kakao/logOut", { refreshToken });

      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      alert("로그아웃 되었습니다.");
      navigate("/");
    } catch (err) {
      console.error("로그아웃 실패", err);
      alert("로그아웃 실패!");
    }
  };

  const handleCreateSummary = () => {
    navigate("/summaries/new");
  };

  return (
    <div className="topbar">

      <div className="topbar-right">
        {accessToken ? (<>
          <button onClick={handleCreateSummary}>새 요약</button> |
          <button onClick={handleLogout}>로그아웃</button>
          </>
        ) : (
          <button onClick={handleLogin}>카카오 로그인</button>
        )}
      </div>
    </div>
  );
};

export default TopBar;
