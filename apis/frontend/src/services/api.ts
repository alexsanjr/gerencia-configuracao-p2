import axios from "axios";

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers = config.headers || {};
        config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
});

export default api;

// Interceptor para lidar com 403 e redirecionar para login
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && (error.response.status === 403 || error.response.status === 401)) {
            localStorage.removeItem("token");
            window.location.href = "/login";
        }
        return Promise.reject(error);
    },
);
