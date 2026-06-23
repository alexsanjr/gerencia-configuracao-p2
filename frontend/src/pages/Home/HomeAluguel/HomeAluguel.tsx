import { useEffect, useState } from "react";
import { getAllVeiculosDisponivel } from "../../../services/veiculosService";
import type { Veiculo } from "../../../types/veiculo";
import AluguelCard from "../../AluguelCard/AluguelCard";
import { useNavigate } from "react-router-dom";
import "./HomeAluguel.css";

const HomeAluguel: React.FC = () => {
    const [veiculos, setVeiculos] = useState<Veiculo[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        buscarVeiculos();
    }, []);

    async function buscarVeiculos() {
        setLoading(true);
        try {
            const data = await getAllVeiculosDisponivel();
            setVeiculos((data || []).slice(0, 3));
        } finally {
            setLoading(false);
        }
    }

    const navigate = useNavigate();

    function handleReservar(veiculo: Veiculo) {
        navigate("/reserva", { state: { veiculo } });
    }

    return (
        <section className="home-aluguel">
            <div className="container">
                <div className="title-separator">
                    <strong>
                        Nossa Frota <span>Premium</span>
                    </strong>
                    <p>
                        Escolha entre nossa ampla seleção de veículos de alta qualidade para atender a todas as
                        necessidades e orçamentos.
                    </p>
                </div>
                <div className="aluguel-cards-list">
                    {loading ? (
                        <span>Carregando veículos...</span>
                    ) : veiculos.length === 0 ? (
                        <span>Nenhum veículo em destaque.</span>
                    ) : (
                        veiculos.map((v) => <AluguelCard key={v.id} veiculo={v} onReservar={handleReservar} />)
                    )}
                </div>
            </div>
        </section>
    );
};

export default HomeAluguel;
