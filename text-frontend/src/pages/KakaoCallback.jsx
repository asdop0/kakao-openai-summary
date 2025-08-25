import { useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import api from "../api/axios";

const KakaoCallback = () => {
  const [searchParams] = useSearchParams();
  const code = searchParams.get("code");
  const navigate = useNavigate();

  useEffect(() => {
    if (code) {
      api.post("/kakao/logIn", { code })
        .then(res => {
          localStorage.setItem("accessToken", res.data.accessToken);
          localStorage.setItem("refreshToken", res.data.refreshToken);
          navigate("/dashboard");
        })
        .catch(err => {
          if (err.response && err.response.status === 409) {
            console.log("중복 코드 요청 무시");
          } else {
            console.error(err);
          };
        });
    }

  }, []);

  return <div>로그인 중...</div>;
};

export default KakaoCallback;