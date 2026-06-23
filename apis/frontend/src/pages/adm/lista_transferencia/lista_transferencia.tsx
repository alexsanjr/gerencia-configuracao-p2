import { Icon } from "@iconify/react/dist/iconify.js";
import DashboardDefaults from "../../../components/dashboard-defaults/dashboard-defaults";

import { useEffect, useState } from "react";
import { getTransferencias } from "../../../services/tranferenciaService";
import { estoques } from "../../../utils/veiculoOptions";
import { useNavigate } from "react-router-dom";

import "./lista_transferencia.min.css";

const ListaTransferencia: React.FC = () => {
    const [transferencias, setTransferencias] = useState<any[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchTransferencias() {
            try {
                const data = await getTransferencias();
                // Garante que transferencias seja sempre array
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
                setTransferencias(lista);
            } catch {
                setTransferencias([]);
            }
        }
        fetchTransferencias();
    }, []);

    // Mapeia id do estoque para nome
    const getEstoqueNome = (id: number) => {
        const found = estoques.find((e) => String(e.value) === String(id));
        return found ? found.label : id;
    };

    // Formata data para dd/mm/yyyy
    const formatarData = (dataIso: string) => {
        if (!dataIso) return "";
        const d = new Date(dataIso);
        return d.toLocaleDateString("pt-BR");
    };

    return (
        <>
            <DashboardDefaults title="Transferências de veículos">
                <section className="container-table-frota">
                    <div className="title">
                        <button onClick={() => navigate(`/nova_transferencia`)}>
                            <i>
                                <Icon icon="fluent-emoji-high-contrast:plus" />
                            </i>
                            <p>Nova transferência</p>
                        </button>
                    </div>

                    <div className="table">
                        <div className="arredondar">
                            {transferencias && (
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Veículos</th>
                                            <th>Origem</th>
                                            <th>Destino</th>
                                            <th>Data</th>
                                            <th>Status</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {transferencias.map((item: any) => (
                                            <tr key={item.id}>
                                                <td>{item.id}</td>
                                                <td>
                                                    {Array.isArray(item.idVeiculos)
                                                        ? item.idVeiculos.join(", ")
                                                        : item.idVeiculos}
                                                </td>
                                                <td>{getEstoqueNome(item.estoqueOrigem)}</td>
                                                <td>{getEstoqueNome(item.estoqueDestino)}</td>
                                                <td>{formatarData(item.data)}</td>
                                                <td className="status">
                                                    <span className={item.status}>{item.status}</span>
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

export default ListaTransferencia;
