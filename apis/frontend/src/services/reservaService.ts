import api from "./api";

const API_URL = "/reservas";

interface ReservaPayload {
    usuarioId: number;
    categoria: string;
    status: string;
    dataReserva: string;
    dataVencimento: string;
    localRetiradaId: number;
    motoristaId: number;
    veiculoId: number;
}

// Criar uma nova reserva
export const criarReserva = async (payload: ReservaPayload) => {
    const response = await api.post(API_URL, payload);
    return response.data;
};

// Buscar todas as reservas
export const getReservas = async () => {
    const response = await api.get(API_URL);
    return response.data;
};

// Buscar uma reserva específica
export const getReservaById = async (id: number) => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
};

// Atualizar o status da reserva para AGENDADO
export const aprovarReserva = async (id: number, reserva: ReservaPayload) => {
    const response = await api.put(`${API_URL}/${id}`, {
        ...reserva,
        status: "AGENDADO",
    });
    return response.data;
};

// Atualizar o status da reserva para CANCELADO
export const cancelarReserva = async (id: number, reserva: ReservaPayload) => {
    const response = await api.put(`${API_URL}/${id}`, {
        ...reserva,
        status: "CANCELADO",
    });
    return response.data;
};

// Atualizar o status da reserva para EM_ANDAMENTO (Check-in)
export const iniciarReserva = async (id: number, reserva: ReservaPayload) => {
    const response = await api.put(`${API_URL}/${id}`, {
        ...reserva,
        status: "EM_ANDAMENTO",
    });
    return response.data;
};

// Atualizar o status da reserva para ENCERRADO (Check-out)
export const encerrarReserva = async (id: number, reserva: ReservaPayload) => {
    const response = await api.put(`${API_URL}/${id}`, {
        ...reserva,
        status: "ENCERRADO",
    });
    return response.data;
};

// Buscar reservas por status
export const getReservasPorStatus = async (status: string) => {
    const response = await api.get(`${API_URL}?status=${status}`);
    return response.data;
};

// Buscar reservas por usuário
export const getReservasPorUsuario = async (usuarioId: number) => {
    const response = await api.get(`${API_URL}?usuarioId=${usuarioId}`);
    return response.data;
};

// Buscar reservas por local de retirada
export const getReservasPorLocal = async (localRetiradaId: number) => {
    const response = await api.get(`${API_URL}?localRetiradaId=${localRetiradaId}`);
    return response.data;
};
