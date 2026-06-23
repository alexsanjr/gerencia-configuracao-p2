import api from "./api";

const API_URL = "/agendar-manutencao";

export interface EstacaoServico {
    id: number;
    nome: string;
}

export interface Manutencao {
    id: number;
    veiculoId: number;
    estacaoDeServicoId: number;
    motivoManutencao: string;
    dataManutencao: string;
    status?: "AGENDADO" | "EM_ANDAMENTO" | "FINALIZADO" | "CANCELADO";
}

export type ManutencaoPayload = Omit<Manutencao, 'id' | 'status'>

export const getManutencoes = async (): Promise<Manutencao[]> => {
    const response = await api.get(API_URL);
    return response.data;
};

export const getManutencaoById = async (id: number): Promise<Manutencao> => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
};

export const agendarManutencao = async (payload: ManutencaoPayload): Promise<Manutencao> => {
    const response = await api.post(`${API_URL}`, payload);
    return response.data;
};

export const finalizarManutencao = async (id: number): Promise<Manutencao> => {
    const response = await api.patch(`${API_URL}/${id}/finalizar`);
    return response.data;
};

export const getEstacaoDeServico = async (): Promise<EstacaoServico[]> => {
    const response = await api.get("/estacao-de-servico");
    return response.data;
};
