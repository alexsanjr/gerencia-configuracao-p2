import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { cadastrarMotorista, cadastrarMotoristaLogado } from "../../services/MotoristaService";
import "./CadastroMotorista.min.css";
import NavBar from "../../components/NavBar/NavBar";
import ImgRep from "../../assets/bkaluguel.jpg";

interface MotoristaForm {
    cnh: string;
    nome?: string;
    cpf?: string;
    dataNascimento?: string;
}

const CadastroMotorista: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const reservaPayload = location.state?.reservaPayload;

    // Estados para o formulário de motorista
    const [tipoMotorista, setTipoMotorista] = useState<"eu" | "outro" | "">("");
    const [motoristaForm, setMotoristaForm] = useState<MotoristaForm>({
        cnh: "",
        nome: "",
        cpf: "",
        dataNascimento: "",
    });
    const [loading, setLoading] = useState(false);
    const [erro, setErro] = useState("");

    const handleInputChange = (field: keyof MotoristaForm, value: string) => {
        let formattedValue = value;

        // Formatação para CPF (apenas números, máximo 11 dígitos)
        if (field === "cpf") {
            formattedValue = value.replace(/\D/g, "").slice(0, 11);
        }

        // Formatação para CNH (apenas números e letras, máximo 11 caracteres)
        if (field === "cnh") {
            formattedValue = value.replace(/[^a-zA-Z0-9]/g, "").slice(0, 11);
        }

        setMotoristaForm((prev) => ({
            ...prev,
            [field]: formattedValue,
        }));
    };

    const handleCadastroMotorista = async () => {
        if (!motoristaForm.cnh.trim()) {
            setErro("CNH é obrigatória");
            return;
        }

        setLoading(true);
        setErro("");

        try {
            let motoristaResponse;

            if (tipoMotorista === "eu") {
                // Cadastro para usuário logado
                motoristaResponse = await cadastrarMotoristaLogado({
                    cnh: motoristaForm.cnh,
                });
            } else if (tipoMotorista === "outro") {
                // Validações para outro motorista
                if (
                    !motoristaForm.nome?.trim() ||
                    !motoristaForm.cpf?.trim() ||
                    !motoristaForm.dataNascimento?.trim()
                ) {
                    setErro("Todos os campos são obrigatórios para cadastrar outro motorista");
                    return;
                }

                // Cadastro para outro motorista
                motoristaResponse = await cadastrarMotorista({
                    cnh: motoristaForm.cnh,
                    nome: motoristaForm.nome!,
                    cpf: motoristaForm.cpf!,
                    dataNascimento: motoristaForm.dataNascimento!,
                });
            }

            // Atualizar o reservaPayload com o ID do motorista
            const updatedReservaPayload = {
                ...reservaPayload,
                motoristaId: motoristaResponse.id,
            };

            // Navegar para o pagamento após sucesso
            navigate("/pagamento", { state: { reservaPayload: updatedReservaPayload } });
        } catch (err: any) {
            console.error("Erro ao cadastrar motorista:", err);
            if (err.response?.data?.message) {
                setErro(err.response.data.message);
            } else {
                setErro("Erro ao cadastrar motorista. Tente novamente.");
            }
        } finally {
            setLoading(false);
        }
    };

    // Se não há reservaPayload, redirecionar
    if (!reservaPayload) {
        return (
            <>
                <NavBar />
                <div className="cadastro-motorista-container">
                    <div className="erro-message">
                        Dados da reserva não encontrados. Volte e faça a reserva novamente.
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            <NavBar />
            <section className="cadastro-motorista-container" style={{ backgroundImage: `url(${ImgRep})` }}>
                <div className="motorista-content">
                    <div className="motorista-header">
                        <h2>Cadastro de Motorista</h2>
                        <p>Quem será o motorista do veículo?</p>
                    </div>

                    <div className="motorista-section">
                        <div className="motorista-tipo-selecao">
                            <label className={tipoMotorista === "eu" ? "selected" : ""}>
                                <input
                                    type="radio"
                                    name="tipoMotorista"
                                    value="eu"
                                    checked={tipoMotorista === "eu"}
                                    onChange={(e) => setTipoMotorista(e.target.value as "eu")}
                                />
                                <span className="radio-custom"></span>
                                <div className="radio-content">
                                    <strong>Eu sou o motorista</strong>
                                    <small>Usar meus dados cadastrados</small>
                                </div>
                            </label>

                            <label className={tipoMotorista === "outro" ? "selected" : ""}>
                                <input
                                    type="radio"
                                    name="tipoMotorista"
                                    value="outro"
                                    checked={tipoMotorista === "outro"}
                                    onChange={(e) => setTipoMotorista(e.target.value as "outro")}
                                />
                                <span className="radio-custom"></span>
                                <div className="radio-content">
                                    <strong>Outra pessoa é o motorista</strong>
                                    <small>Cadastrar dados de terceiro</small>
                                </div>
                            </label>
                        </div>

                        {tipoMotorista && (
                            <div className="motorista-form">
                                <div className="form-group">
                                    <label htmlFor="cnh">CNH *</label>
                                    <input
                                        type="text"
                                        id="cnh"
                                        value={motoristaForm.cnh}
                                        onChange={(e) => handleInputChange("cnh", e.target.value)}
                                        placeholder="Digite o número da CNH (11 caracteres)"
                                        maxLength={11}
                                    />
                                </div>

                                {tipoMotorista === "outro" && (
                                    <>
                                        <div className="form-group">
                                            <label htmlFor="nome">Nome Completo *</label>
                                            <input
                                                type="text"
                                                id="nome"
                                                value={motoristaForm.nome}
                                                onChange={(e) => handleInputChange("nome", e.target.value)}
                                                placeholder="Digite o nome completo do motorista"
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label htmlFor="cpf">CPF *</label>
                                            <input
                                                type="text"
                                                id="cpf"
                                                value={formatCPF(motoristaForm.cpf || "")}
                                                onChange={(e) => handleInputChange("cpf", e.target.value)}
                                                placeholder="Digite o CPF (apenas números)"
                                                maxLength={14} // 11 números + máscara
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label htmlFor="dataNascimento">Data de Nascimento *</label>
                                            <input
                                                type="date"
                                                id="dataNascimento"
                                                value={motoristaForm.dataNascimento}
                                                onChange={(e) => handleInputChange("dataNascimento", e.target.value)}
                                            />
                                        </div>
                                    </>
                                )}

                                <div className="form-actions">
                                    <button
                                        type="button"
                                        className="btn-voltar"
                                        onClick={() => navigate(-1)}
                                        disabled={loading}
                                    >
                                        Voltar
                                    </button>

                                    <button
                                        type="button"
                                        className="btn-continuar"
                                        onClick={handleCadastroMotorista}
                                        disabled={loading}
                                    >
                                        {loading ? "Cadastrando..." : "Continuar para Pagamento"}
                                    </button>
                                </div>

                                {erro && <div className="erro-message">{erro}</div>}
                            </div>
                        )}
                    </div>
                </div>
            </section>
        </>
    );
};

function formatCPF(value: string) {
    const v = value.replace(/\D/g, "").slice(0, 11);
    return v
        .replace(/^(\d{3})(\d)/, "$1.$2")
        .replace(/^(\d{3})\.(\d{3})(\d)/, "$1.$2.$3")
        .replace(/\.(\d{3})(\d)/, ".$1-$2");
}

export default CadastroMotorista;

