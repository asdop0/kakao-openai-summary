import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
  const navigate = useNavigate();
  
  useEffect(() => {
    if (localStorage.getItem("accessToken")) {
      navigate("/dashboard");
    }

  });

  return (
    <div style={styles.container}>
      <h1>로그인이 필요합니다.</h1>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
  },
};

export default LoginPage;
