import { Icon } from "@iconify/react/dist/iconify.js";
import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";
import useHookAdmList from "../hook/useHookAdmList";
import { deleteVeiculo } from "../../../services/veiculosService";
import { useState } from "react";

import "./adm.min.css";
import { useNavigate } from "react-router-dom";
import { InputSelect } from "../../../components/inputs";
import { locais } from "../../../utils/veiculoOptions";

const ListaFrota: React.FC = () => {
    const { Lista_veiculos, atualizarLista } = useHookAdmList();

    const handleDelete = async (id: string | number) => {
        if (window.confirm("Tem certeza que deseja excluir este veículo?")) {
            try {
                await deleteVeiculo(id);
                if (typeof atualizarLista === "function") atualizarLista();
            } catch (err) {
                alert("Erro ao excluir veículo.");
            }
        }
    };

    const navigate = useNavigate();

    const [filialSelecionada, setFilialSelecionada] = useState("");

    // Filtra veículos pela filial selecionada (filialId)
    const veiculosFiltrados = filialSelecionada
        ? Lista_veiculos.filter((v: any) => String(v.filialId) === filialSelecionada)
        : Lista_veiculos;

    return (
        <>
            <DashboardDefaults title="Gerenciamento de frota">
                <section className="container-table-frota">
                    <div className="title">
                        <button onClick={() => navigate(`/novoveiculo`)}>
                            <i>
                                <Icon icon="fluent-emoji-high-contrast:plus" />
                            </i>
                            <p>Adicionar veículo</p>
                        </button>

                        <InputSelect
                            options={locais}
                            icon="icon-park-solid:city"
                            value={filialSelecionada}
                            onChange={(e) => setFilialSelecionada(e.target.value)}
                        />
                    </div>

                    <div className="table">
                        <div className="arredondar">
                            {veiculosFiltrados && (
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Marca</th>
                                            <th>Modelo</th>
                                            <th>Grupo</th>
                                            <th>Cor</th>
                                            <th>Ano</th>
                                            <th>KM</th>
                                            <th>Placa</th>
                                            <th style={{ textAlign: "center" }}>Status</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {veiculosFiltrados.map((item: any) => (
                                            <tr key={item.id}>
                                                <td>{item.id}</td>
                                                <td>{item.marca}</td>
                                                <td>{item.modelo}</td>
                                                <td>{item.grupo}</td>
                                                <td>{item.cor}</td>
                                                <td>{item.ano}</td>
                                                <td>{item.quilometragem}</td>
                                                <td>{item.placa}</td>
                                                <td className="status">
                                                    <span className={item.statusVeiculo}>{item.statusVeiculo}</span>
                                                </td>
                                                <td className="edit">
                                                    <div>
                                                        <button onClick={() => navigate(`/editarveiculo/${item.id}`)}>
                                                            <i>
                                                                <Icon icon="tabler:edit" style={{ color: "black" }} />
                                                            </i>
                                                        </button>
                                                        <button className="trash" onClick={() => handleDelete(item.id)}>
                                                            <i>
                                                                <Icon icon="iconamoon:trash-fill" />
                                                            </i>
                                                        </button>
                                                    </div>
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
        </>
    );
};

export default ListaFrota;
