import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllVeiculos } from "../../../services/veiculosService";
import * as manutencaoService from "../../../services/manutencaoService";
import type { EstacaoServico } from "../../../services/manutencaoService";
import type { Veiculo } from "../../../types/veiculo";

import "./nova_manutencao.min.css";

const NovaManutencao: React.FC = () => {
    const navigate = useNavigate();
    const [veiculos, setVeiculos] = useState<Veiculo[]>([]);
    const [veiculoSelecionado, setVeiculoSelecionado] = useState<string>("");
    const [estacaoSelecionada, setEstacaoSelecionada] = useState<string>("");
    const [motivoManutencao, setMotivoManutencao] = useState<string>("");
    const [estacoesServico, setEstacoesServico] = useState<EstacaoServico[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        async function fetchData() {
            setLoading(true);
            try {
                // Carregar veículos
                const veiculosData = await getAllVeiculos();
                let lista: Veiculo[] = [];
                if (Array.isArray(veiculosData)) {
                    lista = veiculosData;
                } else if (Array.isArray(veiculosData.content)) {
                    lista = veiculosData.content;
                } else if (Array.isArray(veiculosData.results)) {
                    lista = veiculosData.results;
                } else if (Array.isArray(veiculosData.data)) {
                    lista = veiculosData.data;
                }
                // Filtrar apenas veículos disponíveis
                const veiculosDisponiveis = lista.filter(veiculo => veiculo.statusVeiculo === "DISPONIVEL");
                setVeiculos(veiculosDisponiveis);

                // Carregar estações de serviço
                const estacoesData = await manutencaoService.getEstacaoDeServico();
                let listaEstacoes: EstacaoServico[] = [];
                const dataAny = estacoesData as any;
                if (Array.isArray(estacoesData)) {
                    listaEstacoes = estacoesData;
                } else if (dataAny && Array.isArray(dataAny.content)) {
                    listaEstacoes = dataAny.content;
                } else if (dataAny && Array.isArray(dataAny.results)) {
                    listaEstacoes = dataAny.results;
                } else if (dataAny && Array.isArray(dataAny.data)) {
                    listaEstacoes = dataAny.data;
                }
                setEstacoesServico(listaEstacoes);
            } catch (error) {
                console.error("Erro ao carregar dados:", error);
                alert("Erro ao carregar dados. Por favor, tente novamente.");
            } finally {
                setLoading(false);
            }
        }
        fetchData();
    }, []);

    const handleSelectVeiculo = (id: string) => {
        setVeiculoSelecionado((prev) => (prev === id ? "" : id));
    };

    const enviar_form = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!veiculoSelecionado || !estacaoSelecionada || !motivoManutencao) {
            alert("Por favor, preencha todos os campos.");
            return;
        }

        try {
            await manutencaoService.agendarManutencao({
                veiculoId: Number(veiculoSelecionado),
                estacaoDeServicoId: Number(estacaoSelecionada),
                motivoManutencao,
                dataManutencao: new Date().toISOString(),

            });
            navigate("/lista_manutencao");
        } catch (error) {
            console.error("Erro ao criar manutenção:", error);
            alert("Erro ao agendar manutenção. Por favor, tente novamente.");
        }
    };

    return (
        <DashboardDefaults title="Nova manutenção de veículo">
            <section className="criar-veiculo">
                <section className="form-criar-veiculo">
                    <form onSubmit={enviar_form}>
                        <div className="veiculos-lista-manutencao">
                            <label>Selecione o veículo</label>
                            {loading ? (
                                <div>Carregando veículos...</div>
                            ) : veiculos.length === 0 ? (
                                <div>Nenhum veículo disponível para manutenção.</div>
                            ) : (
                                <ul>
                            {veiculos
                                .filter(v => v.statusVeiculo === "DISPONIVEL")
                                .map((v) => (
                                    <li key={v.id}>
                                        <label>
                                            <input
                                                type="radio"
                                                name="veiculo"
                                                checked={veiculoSelecionado === String(v.id)}
                                                onChange={() => handleSelectVeiculo(String(v.id))}
                                            />
                                            {v.marca} {v.modelo} ({v.placa}) - {v.grupo}
                                        </label>
                                    </li>
                                ))}
                                </ul>
                            )}
                        </div>
                        <div className="grid">
                            <div className="column">
                                <div className="form-group">
                                    <label htmlFor="estacaoServico">Estação de Serviço</label>
                                    <select
                                        id="estacaoServico"
                                        value={estacaoSelecionada}
                                        onChange={(e) => setEstacaoSelecionada(e.target.value)}
                                        required
                                    >
                                        <option value="">Selecione uma estação</option>
                                        {estacoesServico.map(estacao => (
                                            <option key={estacao.id} value={estacao.id}>
                                                {estacao.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="motivoManutencao">Motivo da Manutenção</label>
                                    <textarea
                                        id="motivoManutencao"
                                        value={motivoManutencao}
                                        onChange={(e) => setMotivoManutencao(e.target.value)}
                                        required
                                        placeholder="Descreva o motivo da manutenção"
                                        rows={4}
                                    />
                                </div>
                            </div>
                        </div>
                        <button type="submit" disabled={loading}>
                            {loading ? "Processando..." : "Agendar Manutenção"}
                        </button>
                    </form>
                </section>
            </section>
        </DashboardDefaults>
    );
};

export default NovaManutencao;
