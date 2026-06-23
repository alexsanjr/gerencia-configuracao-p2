import React from "react";
import { useNavigate } from "react-router-dom";
import type { Veiculo } from "../../types/veiculo";
import car from "../../assets/background-banner.png";
import "./AluguelCard.min.css";
import { Icon } from "@iconify/react/dist/iconify.js";

interface AluguelCardProps {
    veiculo: Veiculo;
    onReservar?: (veiculo: Veiculo) => void;
}

const AluguelCard: React.FC<AluguelCardProps> = ({ veiculo, onReservar }) => {
    const navigate = useNavigate();

    function handleReservar() {
        if (onReservar) {
            onReservar(veiculo);
        } else {
            navigate("/reserva", { state: { veiculo } });
        }
    }

    return (
        <div className="aluguel-card aluguel-card-elegante">
            <div className="aluguel-card-imgbox">
                <img src={veiculo.imagemUrl || car} alt={veiculo.modelo} className="aluguel-card-img" />
            </div>
            <div className="aluguel-card-info">
                <div className="divisor">
                    <div className="title">
                        <h3 className="aluguel-card-title">
                            {veiculo.marca} {veiculo.modelo}
                        </h3>
                    </div>

                    <div className="valor">
                        <p>Por apenas</p>
                        <span>
                            R$ 350<i>/Dia</i>
                        </span>
                    </div>
                </div>

                <div className="aluguel-card-details">
                    <span>
                        <i>
                            <Icon icon="solar:calendar-bold" />
                        </i>
                        <b>{veiculo.ano}</b>
                    </span>
                    <span>
                        <i>
                            <Icon icon="ic:baseline-color-lens" />
                        </i>
                        <b>{veiculo.cor}</b>
                    </span>
                    <span>
                        <i>
                            <Icon icon="ic:baseline-color-lens" />
                        </i>
                        <b>{veiculo.placa}</b>
                    </span>
                </div>
            </div>

            <div className="btn-submit-container">
                <button className="aluguel-card-btn" onClick={handleReservar}>
                    Reservar agora
                </button>
            </div>
        </div>
    );
};

export default AluguelCard;
