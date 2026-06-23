import { Icon } from "@iconify/react/dist/iconify.js";
import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import * as manutencaoService from "../../../services/manutencaoService";
import type { Manutencao, EstacaoServico } from "../../../services/manutencaoService";
import useHookAdmList from "./hook/useHookAdmList";
import type { ApiError } from "../../../types/api-error";

import "./lista_manutencao.min.css";

const ListaManutencao: React.FC = () => {
    const hookResult = useHookAdmList();
    const navigate = useNavigate();
    console.log('Hook result:', hookResult);

    // Garante que Lista_veiculos é sempre um array
    const Lista_veiculos = (() => {
        if (!hookResult?.Lista_veiculos) return [];
        const data = hookResult.Lista_veiculos;
        if (typeof data === 'object' && 'content' in data && Array.isArray(data.content)) {
            return data.content;
        }
        return Array.isArray(data) ? data : [];
    })();

    console.log('Lista_veiculos processada:', Lista_veiculos);

    const [manutencoes, setManutencoes] = useState<Manutencao[]>([]);
    const [estacoesServico, setEstacoesServico] = useState<EstacaoServico[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const loadData = async () => {
            await carregarEstacoes();
            await carregarManutencoes();
        };
        loadData();
    }, []);

    const carregarManutencoes = async () => {
        setLoading(true);
        try {
            const response = await manutencaoService.getManutencoes();
            let lista: Manutencao[] = [];

            if (response && typeof response === 'object') {
                const data = response as any;
                if (Array.isArray(data)) {
                    lista = data;
                } else if (Array.isArray(data.content)) {
                    lista = data.content;
                } else if (Array.isArray(data.results)) {
                    lista = data.results;
                } else if (Array.isArray(data.data)) {
                    lista = data.data;
                }
            }

            setManutencoes(lista);
            console.log('Manutenções carregadas:', lista);
        } catch (error) {
            console.error('Erro ao carregar manutenções:', error);
            handleError(error);
            setManutencoes([]);
        } finally {
            setLoading(false);
        }
    };

    const carregarEstacoes = async () => {
        try {
            const response = await manutencaoService.getEstacaoDeServico();
            let lista: EstacaoServico[] = [];

            if (response && typeof response === 'object') {
                const data = response as any;
                if (Array.isArray(data)) {
                    lista = data;
                } else if (Array.isArray(data.content)) {
                    lista = data.content;
                } else if (Array.isArray(data.results)) {
                    lista = data.results;
                } else if (Array.isArray(data.data)) {
                    lista = data.data;
                }
            }

            setEstacoesServico(lista);
            console.log('Estações carregadas:', lista);
        } catch (error) {
            console.error('Erro ao carregar estações:', error);
            handleError(error);
            setEstacoesServico([]);
        }
    };

    const handleError = (error: unknown) => {
        console.error("Erro:", error);
        if (error && typeof error === 'object' && 'response' in error) {
            const apiError = error as unknown as ApiError;
            alert(apiError.message || "Erro ao processar a requisição");
        } else {
            alert("Erro ao processar a requisição");
        }
    };

    const handleNovaManutencao = () => {
        navigate('/nova_manutencao');
    };

    const handleFinalizarManutencao = async (id: number) => {
        if (window.confirm("Deseja finalizar esta manutenção?")) {
            try {
                await manutencaoService.finalizarManutencao(id);
                await carregarManutencoes();
            } catch (error) {
                handleError(error);
            }
        }
    };

    return (
        <DashboardDefaults title="Gerenciamento de manutenção">
            <section className="container-table-frota">
                <div className="title">
                    <button onClick={handleNovaManutencao}>
                        <i>
                            <Icon icon="fluent-emoji-high-contrast:plus" />
                        </i>
                        <p>Nova Manutenção</p>
                    </button>
                </div>

                <div className="table">
                    <div className="arredondar">
                        {loading ? (
                            <div>Carregando...</div>
                        ) : (
                            <table>
                                <thead>
                                    <tr>
                                        <th>Veículo</th>
                                        <th>Placa</th>
                                        <th>Estação de Serviço</th>
                                        <th>Motivo</th>
                                        <th>Data</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {manutencoes.map((manutencao) => {
                                        console.log('Manutenção atual:', manutencao);
                                        const veiculo = Lista_veiculos.find(v => v.id === manutencao.veiculoId);
                                        console.log('Veículo encontrado:', veiculo);
                                        const estacao = estacoesServico.find(e => Number(e.id) === Number(manutencao.estacaoDeServicoId));
                                        console.log('Estação encontrada:', estacao);

                                        return (
                                            <tr key={manutencao.id}>
                                                <td>{veiculo ? `${veiculo.marca} ${veiculo.modelo}` : 'N/A'}</td>
                                                <td>{veiculo?.placa || 'N/A'}</td>
                                                <td>{estacao?.nome || 'N/A'}</td>
                                                <td>{manutencao.motivoManutencao}</td>
                                                <td>{new Date(manutencao.dataManutencao).toLocaleDateString('pt-BR')}</td>
                                                <td>
                                                    {manutencao.status === 'AGENDADO' && (
                                                        <button
                                                            onClick={() => handleFinalizarManutencao(manutencao.id)}
                                                            title="Finalizar manutenção"
                                                        >
                                                            <Icon icon="tabler:check" />
                                                        </button>
                                                    )}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            </section>
        </DashboardDefaults>
    );
};

export default ListaManutencao;
