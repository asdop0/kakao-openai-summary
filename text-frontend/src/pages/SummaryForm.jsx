import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

export default function SummaryForm() {
  const [originalText, setOriginalText] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      alert("로그인이 필요합니다.");
      navigate("/"); // 루트(로그인 페이지)로 이동
    }
  });

  // 본문 입력 핸들러
  const handleChange = (e) => {
    if (e.target.value.length <= 2000) { // 본문 길이 제한
      setOriginalText(e.target.value);
    }
  };

  // 확인 버튼: 서버 전송
  const handleSubmit = async () => {
    if (!originalText.trim()) {
      alert("본문을 입력해주세요.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.post("/summaries", { originalText });
      navigate(`/dashboard`);
    } catch (error) {
      console.error(error);
      alert("요약 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 취소 버튼: 대시보드로 이동
  const handleCancel = () => {
    navigate("/dashboard");
  };

  return (
    <div style={{ maxWidth: "600px", margin: "0 auto", padding: "2rem" }}>
      <h2>새 요약 작성</h2>
      <textarea
        value={originalText}
        onChange={handleChange}
        rows={10}
        style={{ width: "100%", padding: "1rem", fontSize: "1rem" }}
        placeholder="본문을 입력하세요... (최대 2000자)"
      />
      <div style={{ marginTop: "1rem", display: "flex", gap: "1rem" }}>
        <button onClick={handleCancel} style={{ padding: "0.5rem 1rem" }}>
          취소
        </button>
        <button
          onClick={handleSubmit}
          disabled={loading}
          style={{ padding: "0.5rem 1rem" }}
        >
          {loading ? "생성 중..." : "확인"}
        </button>
      </div>
      <p style={{ marginTop: "0.5rem", color: "#666" }}>
        {originalText.length}/2000
      </p>
    </div>
  );
}
