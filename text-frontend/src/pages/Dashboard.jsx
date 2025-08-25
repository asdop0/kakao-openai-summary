import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SummaryCard from "../components/SummaryCard";
import api from "../api/axios";

const Dashboard = () => {
  const navigate = useNavigate();
  const [summaries, setSummaries] = useState(null);
  const [pageRefresh, setPageRefresh] = useState(false);

  useEffect(() => {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) {
      alert("로그인이 필요합니다.");
      navigate("/"); // 루트(로그인 페이지)로 이동
    }
    console.log("실행");

    api.get(`/summaries`)
      .then(res => {
        if(res.data.length === 0) {
          setSummaries(null);
        } else {
          setSummaries(res.data);
        }
      })
      .catch(err => {
        console.error(err);
      });
  }, [pageRefresh]);

  return (
    <div style={styles.container}>
      <div>
        <h2>나의 요약 기록</h2>
      </div>

      {/* 기록 리스트 영역 */}
      <div>
        {!summaries && (
            <p>요약 기록이 없습니다.</p>
        )}
      </div>

      <div>
        {summaries && summaries.map((summary) => (
        <div key={summary.id}>
        <SummaryCard key={summary.id} summary={summary} setPageRefresh={setPageRefresh}/>
        </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
  },
};