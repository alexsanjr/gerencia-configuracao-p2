import { useEffect, useState } from "react";
import { Icon } from "@iconify/react";
import {
    getReservas,
    aprovarReserva,
    cancelarReserva,
    encerrarReserva,
    iniciarReserva,
} from "../../../services/reservaService";
import type { ApiError } from "../../../types/api-error";
import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import "./lista_reservas.min.css";

interface Reserva {
    id: number;
    categoria: string;
    dataReserva: string;
    dataVencimento: string;
    localRetiradaId: number;
    status: string;
    usuarioId: number;
    motoristaId: number;
    veiculoId: number;
}

const statusLabels: Record<string, string> = {
    PENDENTE: "Pendente",
    AGENDADO: "Agendado",
    EM_ANDAMENTO: "Em andamento",
    ENCERRADO: "Encerrado",
    CANCELADO: "Cancelado",
};

const ListaReservas: React.FC = () => {
    const [reservas, setReservas] = useState<Reserva[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        async function fetchReservas() {
            setLoading(true);
            try {
                const data = await getReservas();

                console.log("Dados recebidos:", data);

                setReservas(Array.isArray(data) ? data : data.content || []);
            } finally {
                setLoading(false);
            }
        }
        fetchReservas();
    }, []);

    const getReservaUpdateData = (reserva: Reserva) => {
        return {
            usuarioId: reserva.usuarioId,
            categoria: reserva.categoria,
            status: reserva.status,
            dataReserva: reserva.dataReserva,
            dataVencimento: reserva.dataVencimento,
            localRetiradaId: reserva.localRetiradaId,
            motoristaId: reserva.motoristaId,
            veiculoId: reserva.veiculoId
        };
    };

    const handleError = (error: any) => {
        console.error("Erro:", error);
        if (error.response?.data) {
            const apiError = error.response.data as ApiError;
            alert(apiError.message || "Ocorreu um erro. Tente novamente.");
        } else {
            alert("Ocorreu um erro inesperado. Tente novamente.");
        }
    };

    const handleAprovar = async (reserva: Reserva) => {
        try {
            await aprovarReserva(reserva.id, getReservaUpdateData(reserva));
            setReservas(reservas.map((r) => (r.id === reserva.id ? { ...r, status: "AGENDADO" } : r)));
        } catch (error) {
            handleError(error);
        }
    };

    const handleCancelar = async (reserva: Reserva) => {
        try {
            await cancelarReserva(reserva.id, getReservaUpdateData(reserva));
            setReservas(reservas.map((r) => (r.id === reserva.id ? { ...r, status: "CANCELADO" } : r)));
        } catch (error) {
            handleError(error);
        }
    };

    const handleIniciar = async (reserva: Reserva) => {
        try {
            await iniciarReserva(reserva.id, getReservaUpdateData(reserva));
            setReservas(reservas.map((r) => (r.id === reserva.id ? { ...r, status: "EM_ANDAMENTO" } : r)));
        } catch (error) {
            handleError(error);
        }
    };

    const handleEncerrar = async (reserva: Reserva) => {
        try {
            await encerrarReserva(reserva.id, getReservaUpdateData(reserva));
            setReservas(reservas.map((r) => (r.id === reserva.id ? { ...r, status: "ENCERRADO" } : r)));
        } catch (error) {
            handleError(error);
        }
    };

    // Define quais ações estão disponíveis para cada status
    const getAcoes = (reserva: Reserva) => {
        const acoes = [];
        if (reserva.status === "PENDENTE") {
            acoes.push(
                <button key="aprovar" title="Aprovar reserva" onClick={() => handleAprovar(reserva)}>
                    <i>
                        <Icon icon="tabler:check" style={{ color: "black" }} />
                    </i>
                </button>,
            );
            acoes.push(
                <button
                    key="cancelar"
                    className="trash"
                    title="Cancelar reserva"
                    onClick={() => handleCancelar(reserva)}
                >
                    <i>
                        <Icon icon="tabler:x" />
                    </i>
                </button>,
            );
        }
        if (reserva.status === "AGENDADO") {
            acoes.push(
                <button key="iniciar" title="Iniciar reserva (Check-in)" onClick={() => handleIniciar(reserva)}>
                    <i>
                        <Icon icon="tabler:key" style={{ color: "black" }} />
                    </i>
                </button>,
            );
            acoes.push(
                <button
                    key="cancelar"
                    className="trash"
                    title="Cancelar reserva"
                    onClick={() => handleCancelar(reserva)}
                >
                    <i>
                        <Icon icon="tabler:x" />
                    </i>
                </button>,
            );
        }
        if (reserva.status === "EM_ANDAMENTO") {
            acoes.push(
                <button key="encerrar" title="Encerrar reserva (Check-out)" onClick={() => handleEncerrar(reserva)}>
                    <i>
                        <Icon icon="tabler:flag" style={{ color: "black" }} />
                    </i>
                </button>,
            );
        }
        // ENCERRADO e CANCELADO não têm ações
        return acoes;
    };

    return (
        <DashboardDefaults title="Gerenciamento de reservas">
            <section className="container-table-frota">
                <div className="table">
                    <div className="arredondar">
                        {loading ? (
                            <div>Carregando...</div>
                        ) : (
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Usuário ID</th>
                                        <th>Veículo ID</th>
                                        <th>Categoria</th>
                                        <th>Local Retirada</th>
                                        <th>Data Reserva</th>
                                        <th>Data Vencimento</th>
                                        <th>Status</th>
                                        <th>Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {reservas.map((reserva: any) => (
                                        <tr key={reserva.id}>
                                            <td>{reserva.id}</td>
                                            <td>{reserva.usuarioId}</td>
                                            <td>{reserva.veiculoId}</td>
                                            <td>{reserva.categoria}</td>
                                            <td>{reserva.localRetiradaId}</td>
                                            <td>{new Date(reserva.dataReserva).toLocaleDateString("pt-BR")}</td>
                                            <td>{new Date(reserva.dataVencimento).toLocaleDateString("pt-BR")}</td>
                                            <td className="status">
                                                <span className={`status-${reserva.status}`} style={{ textTransform: 'uppercase' }}>
                                                    {(statusLabels[reserva.status] || reserva.status).toUpperCase()}
                                                </span>
                                            </td>
                                            <td className="edit">
                                                <div>{getAcoes(reserva)}</div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            </section>
        </DashboardDefaults>
    );
};

export default ListaReservas;
