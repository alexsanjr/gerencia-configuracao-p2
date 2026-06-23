import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import { InputText } from "../../../components/inputs";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getVeiculoById, updateVeiculo } from "../../../services/veiculosService";

import "./editar_veiculo.min.css";

import { marcas, cores, grupos, estoques, statusVeiculoOptions } from "../../../utils/veiculoOptions";

const EditarVeiculo: React.FC = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [veiculo, setVeiculo] = useState({
        marca: "",
        modelo: "",
        grupo: "",
        ano: "",
        cor: "",
        valorDiaria: "",
        quilometragem: "",
        statusVeiculo: "",
        estoqueId: "",
        placa: "",
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchVeiculo() {
            if (!id) {
                alert("ID do veículo não informado.");
                navigate("/adm");
                return;
            }
            try {
                const v = await getVeiculoById(String(id));

                console.log("Veículo carregado:", v);

                setVeiculo({
                    marca: (() => {
                        if (marcas.some((m) => m.value === v.marca)) return v.marca;
                        if (typeof v.marca === "string") {
                            const marcaLower = v.marca.toLowerCase();
                            const marcaMatch = marcas.find((m) => m.value.toLowerCase() === marcaLower);
                            if (marcaMatch) return marcaMatch.value;
                        }
                        return "";
                    })(),
                    modelo: v.modelo || "",
                    grupo: v.grupo || "",
                    ano: v.ano ? String(v.ano) : "",
                    cor: (() => {
                        // Se já for igual ao value do select
                        if (cores.some((c) => c.value === v.cor)) return v.cor;
                        // Se vier como label (ex: PRETO), busca o value correspondente
                        if (typeof v.cor === "string") {
                            const corMatch = cores.find((c) => c.label.toUpperCase() === v.cor.toUpperCase());
                            if (corMatch) return corMatch.value;
                        }
                        if (typeof v.cor === "number") {
                            // Se vier como código, converte para string
                            if (cores[v.cor]) return cores[v.cor].value;
                        }
                        return "";
                    })(),
                    valorDiaria: v.valorDiaria || "",
                    // v.valor_Diaria !== undefined && v.valor_Diaria !== null
                    //     ? String(Number(v.valor_Diaria).toFixed(2)).replace(".", ",")
                    //     : "",
                    quilometragem:
                        v.quilometragem !== undefined && v.quilometragem !== null ? String(v.quilometragem) : "",
                    statusVeiculo: (() => {
                        // Se já for igual ao value do select
                        if (statusVeiculoOptions.some((s) => s.value === v.statusVeiculo)) return v.statusVeiculo;
                        // Se vier como label (ex: "EM_USO"), busca o value correspondente
                        if (typeof v.statusVeiculo === "string") {
                            // Tenta encontrar por label
                            const statusMatchLabel = statusVeiculoOptions.find(
                                (s) => s.label.toUpperCase() === v.statusVeiculo.toUpperCase(),
                            );
                            if (statusMatchLabel) return statusMatchLabel.value;
                            // Tenta encontrar por value (case-insensitive)
                            const statusMatchValue = statusVeiculoOptions.find(
                                (s) => s.value.toUpperCase() === v.statusVeiculo.toUpperCase(),
                            );
                            if (statusMatchValue) return statusMatchValue.value;
                        }
                        if (typeof v.statusVeiculo === "number") {
                            // Se vier como código numérico, mapeia para o value string (ordem: EM_USO, DISPONIVEL, RESERVADO, MANUTENCAO)
                            // Ajuste conforme a ordem correta do seu backend
                            const statusMap = ["EM_USO", "DISPONIVEL", "RESERVADO", "MANUTENCAO"];
                            return statusMap[v.statusVeiculo] || "";
                        }
                        return "";
                    })(),
                    estoqueId: v.estoqueId ? String(v.estoqueId) : "",
                    placa: v.placa.replace("-", "") || "",
                });
            } catch {
                alert("Erro ao carregar veículo.");
                navigate("/adm");
            } finally {
                setLoading(false);
            }
        }
        fetchVeiculo();
    }, [id, navigate]);

    const pegarVeiculo = (name: string, value: string) => {
        setVeiculo((i) => ({ ...i, [name]: value }));
    };

    const enviar_form = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const obrigatorios = [
            veiculo.marca,
            veiculo.modelo,
            veiculo.grupo,
            veiculo.ano,
            veiculo.cor,
            veiculo.valorDiaria,
            veiculo.quilometragem,
            veiculo.statusVeiculo,
            veiculo.estoqueId,
            veiculo.placa,
        ];
        if (obrigatorios.some((v) => v === undefined || v === null || v === "")) {
            alert("Preencha todos os campos obrigatórios antes de salvar.");
            return;
        }
        // Se o backend espera statusVeiculo como string (ex: "EM_USO"), mantenha assim. Se espera como número, descomente a linha abaixo e comente a de string.
        const dados = {
            marca: veiculo.marca,
            modelo: veiculo.modelo,
            grupo: veiculo.grupo,
            ano: Number(veiculo.ano),
            cor: Number(veiculo.cor),
            valorDiaria: Number(
                parseFloat(
                    (veiculo.valorDiaria ? String(veiculo.valorDiaria) : "0").replace(/\./g, "").replace(",", "."),
                ).toFixed(2),
            ),
            quilometragem: Number(veiculo.quilometragem),
            statusVeiculo: veiculo.statusVeiculo,
            estoqueId: Number(veiculo.estoqueId),
            placa: (veiculo.placa ? String(veiculo.placa) : "").replace(/^([A-Z]{3})(\d{4})$/, "$1-$2"),
        };

        console.log("Dados do veículo para atualização:", dados);

        try {
            if (!id) {
                alert("ID do veículo não informado.");
                return;
            }
            await updateVeiculo(String(id), dados);
            navigate("/adm");
        } catch {
            alert("Erro ao atualizar veículo. Verifique os dados e tente novamente.");
        }
    };

    if (loading) return <div>Carregando...</div>;

    return (
        <>
            <DashboardDefaults title="Editar veículo">
                <section className="criar-veiculo">
                    <section className="form-criar-veiculo">
                        <form onSubmit={enviar_form}>
                            <div className="grid">
                                <div className="column">
                                    <div className="form-group">
                                        <label htmlFor="marca">Marca</label>
                                        <select
                                            id="marca"
                                            className="input-select"
                                            value={veiculo.marca}
                                            onChange={(e) => pegarVeiculo("marca", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione a marca</option>
                                            {marcas.map((m) => (
                                                <option key={m.value} value={m.value}>
                                                    {m.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="modelo">Modelo</label>
                                        <InputText
                                            id="modelo"
                                            label=""
                                            icon="mdi:car-info"
                                            value={veiculo.modelo}
                                            onChange={(e) => pegarVeiculo("modelo", e.target.value)}
                                            placeholder="Ex: Gol, Onix, Uno..."
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="grupo">Grupo</label>
                                        <select
                                            id="grupo"
                                            className="input-select"
                                            value={veiculo.grupo}
                                            onChange={(e) => pegarVeiculo("grupo", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione o grupo</option>
                                            {grupos.map((g) => (
                                                <option key={g.value} value={g.value}>
                                                    {g.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="ano">Ano</label>
                                        <select
                                            id="ano"
                                            className="input-select"
                                            value={veiculo.ano}
                                            onChange={(e) => pegarVeiculo("ano", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione o ano</option>
                                            {Array.from({ length: new Date().getFullYear() - 1999 }, (_, i) => {
                                                const year = 2000 + i;
                                                return (
                                                    <option key={year} value={year}>
                                                        {year}
                                                    </option>
                                                );
                                            }).reverse()}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="cor">Cor</label>
                                        <select
                                            id="cor"
                                            className="input-select"
                                            value={veiculo.cor}
                                            onChange={(e) => pegarVeiculo("cor", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione a cor</option>
                                            {cores.map((c) => (
                                                <option key={c.value} value={c.value}>
                                                    {c.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                <div className="column">
                                    <div className="form-group">
                                        <label htmlFor="valorDiaria">Valor diário</label>
                                        <InputText
                                            id="valorDiaria"
                                            label=""
                                            icon="material-symbols:attach-money-rounded"
                                            value={
                                                veiculo.valorDiaria !== undefined &&
                                                veiculo.valorDiaria !== null &&
                                                veiculo.valorDiaria !== ""
                                                    ? Number(veiculo.valorDiaria).toLocaleString("pt-BR", {
                                                          style: "currency",
                                                          currency: "BRL",
                                                      })
                                                    : ""
                                            }
                                            onChange={(e) => {
                                                let value = e.target.value.replace(/\D/g, "").slice(0, 10);
                                                let numeric = value ? (parseInt(value, 10) / 100).toFixed(2) : "";
                                                pegarVeiculo("valorDiaria", numeric);
                                            }}
                                            placeholder="R$ 0,00"
                                            inputMode="numeric"
                                            maxLength={10}
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="quilometragem">KM</label>
                                        <InputText
                                            id="quilometragem"
                                            label=""
                                            icon="icon-park-solid:map-distance"
                                            value={veiculo.quilometragem}
                                            onChange={(e) => {
                                                let value = e.target.value.replace(/\D/g, "").slice(0, 10);
                                                pegarVeiculo("quilometragem", value);
                                            }}
                                            placeholder="Ex: 50000"
                                            inputMode="numeric"
                                            maxLength={10}
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="statusVeiculo">Status</label>
                                        <select
                                            id="statusVeiculo"
                                            className="input-select"
                                            value={veiculo.statusVeiculo}
                                            onChange={(e) => pegarVeiculo("statusVeiculo", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione o status</option>
                                            {statusVeiculoOptions.map((s) => (
                                                <option key={s.value} value={s.value}>
                                                    {s.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label htmlFor="placa">Placa</label>
                                        <InputText
                                            id="placa"
                                            label=""
                                            icon="solar:plate-bold"
                                            value={veiculo.placa}
                                            onChange={(e) => {
                                                let value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, "");
                                                value = value.slice(0, 7);
                                                pegarVeiculo("placa", value);
                                            }}
                                            placeholder="ABC1D23 ou ABC1234"
                                            maxLength={7}
                                            pattern="[A-Z]{3}[0-9][A-Z0-9][0-9]{2}"
                                            title="Placa no formato ABC1D23 ou ABC1234"
                                        />
                                    </div>
                                </div>
                            </div>
                            <button>Salvar</button>
                        </form>
                    </section>
                </section>
            </DashboardDefaults>
        </>
    );
};

export default EditarVeiculo;
