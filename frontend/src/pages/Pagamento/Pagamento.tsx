import { useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { criarPagamento, type PagamentoDTO } from "../../services/pagamentoService";
import { criarReserva } from "../../services/reservaService";
// import "./Pagamento.css";
import NavBar from "../../components/NavBar/NavBar";
import ImgRep from "../../assets/bkaluguel.jpg";

import "./Pagamento.min.css";

export default function Pagamento() {
    const location = useLocation();
    const navigate = useNavigate();
    const reservaPayload = location.state?.reservaPayload;
    const [metodo, setMetodo] = useState("");
    const [loading, setLoading] = useState(false);
    const [form, setForm] = useState({
        cardNumber: "",
        cardExpiry: "",
        cardCVV: "",
        cardName: "",
    });
    const [success, setSuccess] = useState("");
    const [error, setError] = useState("");

    function handleInput(e: ChangeEvent<HTMLInputElement>) {
        setForm({ ...form, [e.target.id]: e.target.value });
    }

    async function handleSubmit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setLoading(true);
        setError("");

        if (!metodo) {
            setError("Por favor, selecione um método de pagamento");
            setLoading(false);
            return;
        }

        try {
            // Primeiro cria a reserva
            let reservaResponse;
            if (reservaPayload) {
                reservaResponse = await criarReserva(reservaPayload);
            }

            if (!reservaResponse?.id) {
                throw new Error("Erro ao criar reserva");
            }

            // Prepara o payload do pagamento
            const pagamentoPayload: PagamentoDTO = {
                metodo,
                ...(metodo === "credit-card" || metodo === "debit-card"
                    ? {
                          cardNumber: form.cardNumber.replace(/\s/g, ""), // remove espaços do número do cartão
                          cardExpiry: form.cardExpiry,
                          cardCVV: form.cardCVV,
                          cardName: form.cardName,
                      }
                    : {}),
            };

            // Cria o pagamento
            await criarPagamento(pagamentoPayload);

            setSuccess("Pagamento realizado com sucesso!");
            setTimeout(() => {
                navigate("/minhas-reservas");
            }, 2000);
        } catch (err: any) {
            setError(err.message || "Erro ao processar pagamento. Tente novamente.");
            console.error("Erro no pagamento:", err);
        } finally {
            setLoading(false);
        }
    }

    return (
        <>
            <NavBar />

            <section className="container-pagamento" style={{ backgroundImage: `url(${ImgRep})` }}>
                <div className="container-tamanho">
                    <div className="pagamento-box-aluguel">
                        <h4>Quase lá!</h4>
                        <p>
                            Seu <span>veículo</span> está quase garantido.
                        </p>

                        <form onSubmit={handleSubmit}>
                            <div className="payment-options">
                                <label className="payment-option">
                                    <input
                                        type="radio"
                                        name="payment-method"
                                        value="credit-card"
                                        checked={metodo === "credit-card"}
                                        onChange={() => setMetodo("credit-card")}
                                        disabled={loading}
                                    />
                                    <span>Cartão de Crédito</span>
                                </label>
                                <label className="payment-option">
                                    <input
                                        type="radio"
                                        name="payment-method"
                                        value="debit-card"
                                        checked={metodo === "debit-card"}
                                        onChange={() => setMetodo("debit-card")}
                                        disabled={loading}
                                    />
                                    <span>Cartão de Débito</span>
                                </label>
                                <label className="payment-option">
                                    <input
                                        type="radio"
                                        name="payment-method"
                                        value="pix"
                                        checked={metodo === "pix"}
                                        onChange={() => setMetodo("pix")}
                                        disabled={loading}
                                    />
                                    <span>PIX</span>
                                </label>
                            </div>

                            {(metodo === "credit-card" || metodo === "debit-card") && (
                                <div className="payment-fields">
                                    <div className="form-group">
                                        <label htmlFor="cardNumber">Número do cartão</label>
                                        <input
                                            type="text"
                                            id="cardNumber"
                                            placeholder="0000 0000 0000 0000"
                                            maxLength={19}
                                            required
                                            value={form.cardNumber}
                                            onChange={handleInput}
                                            disabled={loading}
                                        />
                                    </div>
                                    <div className="form-row">
                                        <div className="form-group">
                                            <label htmlFor="cardExpiry">Validade</label>
                                            <input
                                                type="text"
                                                id="cardExpiry"
                                                placeholder="MM/AA"
                                                maxLength={5}
                                                required
                                                value={form.cardExpiry}
                                                onChange={handleInput}
                                                disabled={loading}
                                            />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="cardCVV">CVV</label>
                                            <input
                                                type="password"
                                                id="cardCVV"
                                                placeholder="CVV"
                                                maxLength={4}
                                                required
                                                value={form.cardCVV}
                                                onChange={handleInput}
                                                disabled={loading}
                                            />
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="cardName">Nome impresso no cartão</label>
                                        <input
                                            type="text"
                                            id="cardName"
                                            placeholder="Nome completo"
                                            required
                                            value={form.cardName}
                                            onChange={handleInput}
                                            disabled={loading}
                                        />
                                    </div>
                                </div>
                            )}

                            {metodo === "pix" && (
                                <div className="payment-fields">
                                    <div className="form-group">
                                        <label>Chave PIX</label>
                                        <input type="text" value="empresa@exemplo.com" disabled />
                                    </div>
                                    <div className="pix-info">Abra seu app bancário e pague usando a chave acima.</div>
                                </div>
                            )}

                            <button type="submit" className="pagamento-button" disabled={loading}>
                                {loading ? <span className="loader"></span> : "Confirmar Pagamento"}
                            </button>
                        </form>

                        {success && (
                            <div style={{ color: "#080", textAlign: "center", marginBottom: 16, fontWeight: 600 }}>
                                {success}
                            </div>
                        )}
                        {error && (
                            <div style={{ color: "#c00", textAlign: "center", marginBottom: 16, fontWeight: 600 }}>
                                {error}
                            </div>
                        )}
                    </div>
                </div>
            </section>
        </>
    );
}
