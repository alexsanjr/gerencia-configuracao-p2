import api from "./api";

const API_URL = "/motoristas";

interface MotoristaPayload {
    cnh: string;
    nome: string;
    cpf: string;
    dataNascimento: string;
}

interface MotoristaLogadoPayload {
    cnh: string;
}

// Cadastrar um novo motorista
export const cadastrarMotorista = async (payload: MotoristaPayload) => {
    const response = await api.post(API_URL, payload);
    return response.data;
};

// Cadastrar motorista para usuário logado
export const cadastrarMotoristaLogado = async (payload: MotoristaLogadoPayload) => {
    const response = await api.post(`${API_URL}/logado`, payload);
    return response.data;
};

// Buscar todos os motoristas
export const getMotoristas = async () => {
    const response = await api.get(API_URL);
    return response.data;
};

// Buscar um motorista específico
export const getMotoristaById = async (id: number) => {
    const response = await api.get(`${API_URL}/${id}`);
    return response.data;
};

// Buscar motorista por CPF
export const getMotoristaByCpf = async (cpf: string) => {
    const response = await api.get(`${API_URL}/cpf/${cpf}`);
    return response.data;
};

// Buscar motorista por CNH
export const getMotoristaByCnh = async (cnh: string) => {
    const response = await api.get(`${API_URL}/cnh/${cnh}`);
    return response.data;
};

// Atualizar um motorista
export const atualizarMotorista = async (id: number, payload: Partial<MotoristaPayload>) => {
    const response = await api.put(`${API_URL}/${id}`, payload);
    return response.data;
};

// Deletar um motorista
export const deletarMotorista = async (id: number) => {
    const response = await api.delete(`${API_URL}/${id}`);
    return response.data;
};
