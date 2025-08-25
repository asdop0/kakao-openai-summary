import { Link } from "react-router-dom";
import api from "../api/axios";

const SummaryCard = ({ summary, setPageRefresh }) => {
  const handleDelete = () => {
    const userConfirmed = window.confirm("이 요약을 삭제하시겠습니까?");
    if (!userConfirmed) {
        alert("취소되었습니다.");
        return;
    }
    if (userConfirmed) {
      api.delete(`/summaries/${summary.id}`)
        .then(() => {
            alert("삭제되었습니다.");
            setPageRefresh(prev => !prev);;
        })
        .catch(err => {
            console.error("삭제 실패", err);
        });      
    }
  };

  return (
    <div>
      {/* 한 줄 요약 */}
      <p>{summary.shortText}</p>
      
      {/* 생성일 */}
      <p>{summary.createDate.split('T')[0]}</p>
      
      {/* 상세보기 버튼 */}
      <Link to={`/summaries/detail/${summary.id}`}>
        <button>상세보기</button>
      </Link>
      
      {/* 삭제 버튼 */}
      <button onClick={handleDelete}>
        삭제
      </button>
    </div>
  );
};

export default SummaryCard;
