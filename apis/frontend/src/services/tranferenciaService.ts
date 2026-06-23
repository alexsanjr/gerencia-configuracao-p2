import api from "./api";

const API_URL = "/transferencia-veiculos";

// Busca todas as transferências de veículos
export const getTransferencias = async () => {
    const response = await api.get(API_URL);
    return response.data;
};

// Busca uma transferência pelo ID
export const getTransferenciaById = async (id: string | number) => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
};

// Cria uma nova transferência
export const createTransferencia = async (transferencia: any) => {
    const response = await api.post(API_URL, transferencia);
    return response.data;
};

// Atualiza uma transferência existente
export const updateTransferencia = async (id: string | number, transferencia: any) => {
    const response = await api.put(`${API_URL}/${id}`, transferencia);
    return response.data;
};
