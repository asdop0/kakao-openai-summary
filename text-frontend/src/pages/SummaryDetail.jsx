import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/axios";

const SummaryDetail = () => {
  const { id } = useParams();
  const [summary, setSummary] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const res = await api.get(`/summaries/${id}`);
        setSummary(res.data);
      } catch (err) {
        console.error("상세 불러오기 실패", err);
      }
    };
    fetchSummary();
  }, [id]);

  return summary ? (
    <div>
      <h1>요약 상세</h1>
      <button onClick={() => navigate("/dashboard")}>목록으로 돌아가기</button>
      <div>
        <h3>작성 날짜</h3>
        <p>{summary.createDate.split('T')[0]}</p>
      </div>

      <div>
        <h3>본문</h3>
        <p>{summary.originalText}</p>
      </div>
      <hr/>

      <div>
        <h3>자세한 요약</h3>
        <p>{summary.summarizedText}</p>
      </div>
    </div>
  ) : (
    <p>요약 정보를 불러오는 중입니다...</p>
  );
};

export default SummaryDetail;
