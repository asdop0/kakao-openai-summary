import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8000",
    timeout: 50000,
});

api.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
            config.headers["X-AUTH-TOKEN"] = accessToken;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        const { status, data } = error.response || {};
        const errorCode = data?.errorCode;

        if(status === 403 && errorCode === "ACCESS_TOKEN_EXPIRED" && !originalRequest._retry) {

            originalRequest._retry = true;
            try {
                const refreshToken = localStorage.getItem("refreshToken");

                const res = await axios.post("http://localhost:8000/kakao/refresh", {refreshToken});

                const newAccessToken = res.data.accessToken;
                localStorage.setItem("accessToken", newAccessToken);

                error.config.headers["X-AUTH-TOKEN"] = newAccessToken;
            } catch (refreshError) {
                console.error("토큰 갱신 실패: ", refreshError);
            }
        } else if(error.response?.status === 401) {
            localStorage.setItem("refreshToken", null);
            localStorage.setItem("accessToken", null);
            alert("로그인을 다시 해주세요.");
            window.location.href = "/";

        }
        return Promise.reject(error);
    }
);

export default api;