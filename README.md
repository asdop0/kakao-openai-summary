# kakao-openai-summary
## 프로젝트 소개 (Introduction)
 사용자가 입력한 텍스트를 OpenAI API로 요약하고, 카카오 로그인을 통해 인증된 사용자만 서비스를 이용할 수 있도록 구현한 웹 애플리케이션입니다.
## 프로젝트 목적 (Project Goal)
- 카카오 로그인(OAuth2) 인증 구현 <br>
- OpenAI API를 이용한 텍스트 요약 기능 구현

## 기술 스택 (Tech Stack)

### **🔹 Frontend**
- JavaScript (ES6+)
- React (UI 개발)
- React Router (페이지 이동 관리)
- Axios (Rest API 통신)

### **🔹 Backend**
- Java 21
- Spring Boot (REST API 개발)
- Spring Data JPA (ORM)

### **🔹 External API**
- Kakao OAuth2.0 (소셜 로그인)
- OpenAI API (텍스트 요약 기능)

### **🔹 API Gateway**
- Spring Cloud Gateway (API 라우팅 및 필터 적용)

### **🔹 Database**
- MySQL (데이터 저장)

### **🔹 Collaboration & Tools**
- Git & GitHub (형상 관리)
- Postman (API 테스트)

## 주요 기능 (Key Features)
- **카카오 로그인**: OAuth2 기반 인증 <br>
 로그인 성공 응답입니다.
```json
{
  "access_token" : "ImA_****",
  "token_type" : "bearer",
  "refresh_token" : "Fkg_****",
  "expires_in" : 21599,
  "refresh_token_expires_in" : 5183999
}
```

- **OpenAI 텍스트 요약**: 입력한 본문을 한 줄 요약과 자세한 요약으로 제공 <br>
  API 응답입니다.
  ```json
  {
  "id": "chatcmpl-****",
  "object": "chat.completion",
  "model": "gpt-4o-mini-2024-07-18",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": {
          "shortText": "커피는 인기 음료로 다양한 효과가 있다.",
          "summarizedText": "커피는 전 세계적으로 사랑받는 음료로, 아침에 마시면 피로를 덜고 집중력을 높여준다. 카페인은 두뇌 활동을 촉진하며, 커피 산업은 농업, 유통, 서비스 등에서 일자리를 창출하는 경제적 가치도 크다. 그러나 과도한 섭취는 불면증과 소화 장애 등 건강 문제를 초래할 수 있어 적절한 섭취가 중요하다. 다양한 종류와 로스팅 방법으로 커피 경험은 풍부하다."
        }
      }
    }
  ]
  }

- **JWT 기반 인증**: 서버 API 호출 시 토큰 사용

## 프로젝트 구조 (Project Structure)
**Spring Boot** - text-backend <br>
**Spring Cloud Gateway** - text-gateway <br>
**React** - text-frontend <br>
