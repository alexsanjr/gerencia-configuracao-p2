import React, { useEffect, useState } from "react";
import { getMe } from "../../services/meService";
import api from "../../services/api";
import { locais } from "../../utils/veiculoOptions";
import { Icon } from "@iconify/react";
import "./MinhasReservas.min.css";
import NavBar from "../../components/NavBar/NavBar";
import Footer from "../../components/Footer/Footer";

interface Reserva {
    id: number;
    categoria: string;
    status: string;
    dataReserva: string;
    dataVencimento: string;
    localRetiradaId: number;
    localRetiradaNome?: string;
    usuarioNome?: string;
    veiculoInfo?: string;
    motoristaId?: number;
    motoristaNome?: string;
}

const MinhasReservas: React.FC = () => {
    const [reservas, setReservas] = useState<Reserva[]>([]);
    const [loading, setLoading] = useState(true);
    const [erro, setErro] = useState("");

    useEffect(() => {
        async function fetchReservas() {
            setLoading(true);
            setErro("");
            try {
                const me = await getMe();
                const reservaIds: number[] = me.reservas || [];
                const reservasDetalhes = await Promise.all(
                    reservaIds.map(async (id) => {
                        const res = await api.get(`/reservas/${id}`);
                        const reserva = res.data;
                        // Busca o nome da filial/local de retirada usando veiculoOptions
                        let localRetiradaNome = "";
                        const localOpt = locais.find(l => l.value === String(reserva.localRetiradaId));
                        localRetiradaNome = localOpt ? localOpt.label : `ID ${reserva.localRetiradaId}`;
                        // Busca nome do usuário
                        let usuarioNome = "";
                        if (reserva.usuarioId) {
                            try {
                                const usuarioRes = await api.get(`/usuarios/${reserva.usuarioId}`);
                                usuarioNome = usuarioRes.data.nome || `ID ${reserva.usuarioId}`;
                            } catch {
                                usuarioNome = `${reserva.usuarioId}`;
                            }
                        }
                        // Busca info do veículo
                        let veiculoInfo = "";
                        if (reserva.veiculoId) {
                            try {
                                const veiculoRes = await api.get(`/veiculos/${reserva.veiculoId}`);
                                const v = veiculoRes.data;
                                veiculoInfo = v ? `${v.marca} ${v.modelo} (${v.placa})` : `ID ${reserva.veiculoId}`;
                            } catch {
                                veiculoInfo = `${reserva.veiculoId}`;
                            }
                        }
                        // Busca nome do motorista
                        let motoristaNome = "";
                        if (reserva.motoristaId) {
                            try {
                                const motoristaRes = await api.get(`/usuarios/${reserva.motoristaId}`);
                                motoristaNome = motoristaRes.data.nome || `ID ${reserva.motoristaId}`;
                            } catch {
                                motoristaNome = `${reserva.motoristaId}`;
                            }
                        }
                        return {
                            id,
                            ...reserva,
                            localRetiradaNome,
                            usuarioNome,
                            veiculoInfo,
                            motoristaNome,
                        };
                    }),
                );
                setReservas(reservasDetalhes);
            } catch (err) {
                console.error("Erro detalhado:", err);
                setErro("Erro ao buscar reservas do usuário.");
            } finally {
                setLoading(false);
            }
        }
        fetchReservas();
    }, []);

    return (
        <>
            <NavBar />
            <div className="minhas-reservas-container">
                <h2>Minhas Reservas</h2>

                {loading ? (
                    <div className="minhas-reservas-loading">Carregando reservas...</div>
                ) : erro ? (
                    <div className="minhas-reservas-erro">{erro}</div>
                ) : reservas.length === 0 ? (
                    <div className="minhas-reservas-vazio">Nenhuma reserva encontrada.</div>
                ) : (
                    <div className="minhas-reservas-list">
                        {reservas.map((r) => (
                            <div className="minhas-reserva-card" key={r.id}>
                                <div className="minhas-reserva-imgbox" style={{ minWidth: 100, minHeight: 100, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #f8fafd 0%, #e9ecef 100%)', border: '2px solid #e2e8f0', borderRadius: 16 }}>
                                    <Icon icon="tabler:car" width={56} height={56} color="#9F67DD" />
                                </div>
                                <div className="minhas-reserva-info" style={{ width: '100%' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: 8 }}>
                                        <div className="minhas-reserva-title" style={{ marginBottom: 0 }}>
                                            <Icon icon="tabler:ticket" style={{ marginRight: 6, color: '#9F67DD' }} />
                                            Reserva #{r.id}
                                        </div>
                                        <span style={{
                                            fontSize: '0.95rem',
                                            fontWeight: 700,
                                            borderRadius: '1rem',
                                            padding: '0.4rem 1.1rem',
                                            background: r.status === 'CANCELADO' ? '#ffe7e2' : r.status === 'ENCERRADO' ? '#e8f5e9' : r.status === 'EM_ANDAMENTO' ? '#f2eaff' : r.status === 'AGENDADO' ? '#e3f2fd' : '#fff3e0',
                                            color: r.status === 'CANCELADO' ? '#ff6041' : r.status === 'ENCERRADO' ? '#4caf50' : r.status === 'EM_ANDAMENTO' ? '#6c47ff' : r.status === 'AGENDADO' ? '#1976d2' : '#ff9800',
                                            textTransform: 'uppercase',
                                            letterSpacing: 1,
                                            marginLeft: 8,
                                            marginBottom: 0,
                                            marginTop: 0,
                                            minWidth: 110,
                                            textAlign: 'center',
                                        }}>
                                            {r.status}
                                        </span>
                                    </div>
                                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem 2rem', margin: '1rem 0 0.5rem 0' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                                            <Icon icon="tabler:calendar-event" style={{ color: '#9F67DD' }} />
                                            <span style={{ color: '#475569' }}>Reserva:</span>
                                            <b style={{ color: '#9F67DD' }}>{new Date(r.dataReserva).toLocaleDateString("pt-BR")}</b>
                                        </div>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                                            <Icon icon="tabler:calendar-check" style={{ color: '#9F67DD' }} />
                                            <span style={{ color: '#475569' }}>Vencimento:</span>
                                            <b style={{ color: '#9F67DD' }}>{new Date(r.dataVencimento).toLocaleDateString("pt-BR")}</b>
                                        </div>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                                            <Icon icon="tabler:category" style={{ color: '#9F67DD' }} />
                                            <span style={{ color: '#475569' }}>Categoria:</span>
                                            <b style={{ color: '#1e293b' }}>{r.categoria}</b>
                                        </div>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                                            <Icon icon="tabler:map-pin" style={{ color: '#9F67DD' }} />
                                            <span style={{ color: '#475569' }}>Filial:</span>
                                            <b style={{ color: '#1e293b' }}>{r.localRetiradaNome}</b>
                                        </div>
                                    </div>
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginTop: 8 }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                                            <Icon icon="tabler:car" style={{ color: '#9F67DD' }} />
                                            <b style={{ color: '#475569' }}>Veículo:</b> <span style={{ color: '#1e293b' }}>{r.veiculoInfo || '-'}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            <Footer />
        </>
    );
};

export default MinhasReservas;
