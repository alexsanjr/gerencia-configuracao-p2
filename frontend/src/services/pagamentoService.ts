import api from "./api";

const API_URL = "/pagamento";

export interface PagamentoDTO {
    metodo: string; // backend espera String, não um tipo específico
    cardNumber?: string;
    cardExpiry?: string;
    cardCVV?: string;
    cardName?: string;
}

export const criarPagamento = async (payload: PagamentoDTO) => {
    try {
        // Validação dos campos do cartão antes de enviar
        if (payload.metodo === "credit-card" || payload.metodo === "debit-card") {
            if (!payload.cardNumber || payload.cardNumber.length < 12) {
                throw new Error("Número de cartão inválido");
            }
            if (!payload.cardExpiry || !payload.cardCVV || !payload.cardName) {
                throw new Error("Dados do cartão incompletos");
            }
        }

        const response = await api.post(API_URL, payload);
        console.log("Pagamento criado:", response.data);
        return response.data;
    } catch (error) {
        console.error("Erro ao criar pagamento:", error);
        throw error;
    }
};
