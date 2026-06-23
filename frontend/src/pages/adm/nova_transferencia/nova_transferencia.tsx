// nova_transferencia.tsx
import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import { InputSelect } from "../../../components/inputs";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { estoques } from "../../../utils/veiculoOptions";
import { getAllVeiculos } from "../../../services/veiculosService";
import { createTransferencia } from "../../../services/tranferenciaService";

import "./nova_transferencia.min.css";

const NovaTransferencia: React.FC = () => {
    const navigate = useNavigate();
    const [veiculos, setVeiculos] = useState<any[]>([]);
    const [veiculoSelecionado, setVeiculoSelecionado] = useState<string>("");
    const [estoqueDestino, setEstoqueDestino] = useState("");
    const [loading, setLoading] = useState(false);

    // Filial de origem do veículo selecionado
    const filialOrigem = veiculoSelecionado
        ? (() => {
              const v = veiculos.find((v) => String(v.id) === veiculoSelecionado);
              return v ? String(v.estoqueId) : "";
          })()
        : "";

    const origemLabel = filialOrigem
        ? estoques.find((e) => e.value === filialOrigem)?.label || filialOrigem
        : "Selecione um veículo";

    useEffect(() => {
        async function fetchVeiculos() {
            setLoading(true);
            try {
                const data = await getAllVeiculos();
                let lista = [];
                if (Array.isArray(data)) {
                    lista = data;
                } else if (Array.isArray(data.content)) {
                    lista = data.content;
                } else if (Array.isArray(data.results)) {
                    lista = data.results;
                } else if (Array.isArray(data.data)) {
                    lista = data.data;
                }
                setVeiculos(lista);
            } catch {
                setVeiculos([]);
            } finally {
                setLoading(false);
            }
        }
        fetchVeiculos();
    }, []);

    const handleSelectVeiculo = (id: string) => {
        setVeiculoSelecionado((prev) => (prev === id ? "" : id));
    };

    const enviar_form = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!filialOrigem || !estoqueDestino || !veiculoSelecionado) {
            alert("Selecione um veículo e a filial de destino.");
            return;
        }
        if (filialOrigem === estoqueDestino) {
            alert("Origem e destino devem ser diferentes.");
            return;
        }
        try {
            await createTransferencia({
                estoqueOrigem: Number(filialOrigem),
                estoqueDestino: Number(estoqueDestino),
                idVeiculos: [Number(veiculoSelecionado)],
                status: 0, // conforme especificado, status deve ser número 2
            });
            navigate("/lista_transferencias");
        } catch {
            alert("Erro ao registrar transferência. Tente novamente.");
        }
    };

    return (
        <DashboardDefaults title="Nova transferência de veículos">
            <section className="criar-veiculo">
                <section className="form-criar-veiculo">
                    <form onSubmit={enviar_form}>
                        <div className="veiculos-lista-transferencia">
                            <label>Selecione o veículo</label>
                            {loading ? (
                                <div>Carregando veículos...</div>
                            ) : veiculos.length === 0 ? (
                                <div>Nenhum veículo disponível.</div>
                            ) : (
                                <ul>
                                    {veiculos.map((v: any) => (
                                        <li key={v.id}>
                                            <label>
                                                <input
                                                    type="radio"
                                                    name="veiculo"
                                                    checked={veiculoSelecionado === String(v.id)}
                                                    onChange={() => handleSelectVeiculo(String(v.id))}
                                                />
                                                {v.marca} {v.modelo} ({v.placa}) - {v.grupo} | Filial:{" "}
                                                {estoques.find((e) => e.value === String(v.estoqueId))?.label ||
                                                    v.estoqueId}
                                            </label>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                        <div className="grid">
                            <div className="column">
                                <div className="form-group">
                                    <label>Filial de origem</label>
                                    <div className="filiais-origem-box">{origemLabel}</div>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="estoqueDestino">Filial de destino</label>
                                    <InputSelect
                                        id="estoqueDestino"
                                        icon="mdi:store"
                                        options={estoques}
                                        value={estoqueDestino}
                                        onChange={(e) => setEstoqueDestino(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>
                        </div>
                        <button type="submit">Registrar transferência</button>
                    </form>
                </section>
            </section>
        </DashboardDefaults>
    );
};

export default NovaTransferencia;
