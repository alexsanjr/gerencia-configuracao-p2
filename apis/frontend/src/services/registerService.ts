import api from "./api";

export const register = async (usuario: any) => {
    const response = await api.post("/register", usuario, { headers: { Authorization: "" } });
    return response.data;
};
