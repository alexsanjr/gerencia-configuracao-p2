import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import type { Veiculo } from "../../types/veiculo";
import { getLoggedUserId } from "../../services/authService";
// import { criarReserva } from "../../services/reservaService";
// import "./Reserva.css";
import "./Reserva.min.css";
import NavBar from "../../components/NavBar/NavBar";

import ImgRep from "../../assets/bkaluguel.jpg";
import { InputDate, InputSelect, InputText } from "../../components/inputs";

const categorias = [
    { label: "Imediata", value: "IMEDIATA" },
    { label: "Antecipada", value: "ANTECIPADA" },
];

const Reserva: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const veiculo: Veiculo | undefined = location.state?.veiculo;

    const [categoria, setCategoria] = useState("IMEDIATA");
    const todayStr = new Date().toISOString().slice(0, 10);
    const [dataReserva, setDataReserva] = useState(todayStr);
    const [dataVencimento, setDataVencimento] = useState("");
    const [status] = useState("PENDENTE");
    const [erro, setErro] = useState("");

    // Atualiza dataReserva para hoje se categoria mudar para IMEDIATA
    useEffect(() => {
        if (categoria === "IMEDIATA") {
            setDataReserva(todayStr);
        }
    }, [categoria, todayStr]);

    if (!veiculo) {
        return <div>Veículo não selecionado. Volte e escolha um veículo.</div>;
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setErro("");
        if (!dataReserva || !dataVencimento) {
            setErro("Preencha todas as datas.");
            return;
        }
        if (dataVencimento < todayStr) {
            setErro("A data de vencimento não pode ser anterior a hoje.");
            return;
        }
        if (dataVencimento < dataReserva) {
            setErro("A data de vencimento não pode ser anterior à data da reserva.");
            return;
        }

        const usuarioId = getLoggedUserId();
        if (!usuarioId) {
            setErro("Usuário não está logado. Por favor, faça login novamente.");
            return;
        }

        const reservaPayload = {
            categoria,
            status,
            dataReserva,
            dataVencimento,
            usuarioId,
            localRetiradaId: veiculo.filialId,
            veiculoId: veiculo.id,
            veiculo: {
                ...veiculo,
            },
        };
        navigate("/cadastro-motorista", { state: { reservaPayload } });
    };

    return (
        <>
            <NavBar />
            <section className="reserva-container" style={{ backgroundImage: `url(${ImgRep})` }}>
                <section className="container-agendamento">
                    <div className="veiculo">
                        <div className="titulo">
                            <strong>
                                {veiculo.marca} {veiculo.modelo}
                            </strong>
                        </div>

                        <ul>
                            <li>
                                <span>Ano:</span>
                                <b>{veiculo.ano}</b>
                            </li>
                            <li>
                                <span>Cor:</span>
                                <b>{veiculo.cor}</b>
                            </li>
                            <li>
                                <span>Filial:</span>
                                <b>{veiculo.filialId}</b>
                            </li>
                        </ul>
                    </div>

                    <form className="form-grid" onSubmit={handleSubmit}>
                        <div className="grid-f">
                            <label>
                                <p>Categoria</p>
                                <InputSelect
                                    icon="material-symbols:category-rounded"
                                    options={categorias}
                                    value={categoria}
                                    onChange={(e) => setCategoria(e.target.value)}
                                />
                            </label>

                            <label>
                                Data da Reserva:
                                <InputDate
                                    icon="solar:calendar-bold"
                                    value={dataReserva}
                                    min={todayStr}
                                    onChange={(e) => setDataReserva(e.target.value)}
                                    disabled={categoria === "IMEDIATA"}
                                />
                            </label>

                            <label>
                                Data de Entrega:
                                <InputDate
                                    icon="solar:calendar-bold"
                                    value={dataVencimento}
                                    min={dataReserva || todayStr}
                                    onChange={(e) => setDataVencimento(e.target.value)}
                                />
                            </label>

                            <label>
                                Local de Retirada (ID):
                                <InputText icon="dashicons:car" value={veiculo.estoqueId} disabled />
                            </label>
                        </div>

                        {erro && <div className="erro">{erro}</div>}

                        <button>Confirmar Reserva</button>
                    </form>
                </section>
            </section>
        </>
    );
};

export default Reserva;
