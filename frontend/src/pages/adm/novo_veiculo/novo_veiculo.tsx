import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import { InputSelect, InputText } from "../../../components/inputs";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createVeiculo } from "../../../services/veiculosService";
import { marcas, cores, grupos, estoques, statusVeiculoOptions } from "../../../utils/veiculoOptions";

import "./novo_veiculo.min.css";

const NovoVeiculo: React.FC = () => {
    const navigate = useNavigate();
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

    const pegarVeiculo = (name: string, value: string) => {
        setVeiculo((i) => ({ ...i, [name]: value }));
    };

    const enviar_form = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        // Validação básica
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

        console.log("Campos obrigatórios:", obrigatorios);

        const algumVazio = obrigatorios.some((v) => v === undefined || v === null || v === "");
        if (algumVazio) {
            alert("Preencha todos os campos obrigatórios antes de salvar.");
            return;
        }
        // Monta o objeto para API
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
        try {
            await createVeiculo(dados);
            
            setVeiculo({
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
            navigate("/adm");
        } catch (err) {
            alert("Erro ao cadastrar veículo. Verifique os dados e tente novamente.");
        }
    };

    return (
        <>
            <DashboardDefaults title="Adicionar novo veículo">
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
                                                // Aceita apenas números
                                                let value = e.target.value.replace(/\D/g, "").slice(0, 10);
                                                // Converte para centavos
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
                                                // Aceita apenas números
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
                                        <label htmlFor="estoqueId">Estoque</label>
                                        <select
                                            id="estoqueId"
                                            className="input-select"
                                            value={veiculo.estoqueId}
                                            onChange={(e) => pegarVeiculo("estoqueId", e.target.value)}
                                            required
                                        >
                                            <option value="">Selecione o estoque</option>
                                            {estoques.map((e) => (
                                                <option key={e.value} value={e.value}>
                                                    {e.label}
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
                                                // Permitir apenas letras e números, converter para maiúsculo
                                                let value = e.target.value.toUpperCase().replace(/[^A-Z0-9]/g, "");
                                                // Limitar a 7 caracteres
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

                            <button>Criar</button>
                        </form>
                    </section>
                </section>
            </DashboardDefaults>
        </>
    );
};

export default NovoVeiculo;
