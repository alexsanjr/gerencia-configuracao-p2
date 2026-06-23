import { Icon } from "@iconify/react/dist/iconify.js";

import "./HomeEscolha.min.css";

const HomeEscolha: React.FC = () => {
    return (
        <>
            <section className="escolhas">
                <div className="title">
                    <strong>
                        Por que escolher a <span>você aluga</span>
                    </strong>
                    <p>Nós nos esforçamos ao máximo para tornar sua experiência de aluguel excepcional.</p>
                </div>

                <ul className="cards">
                    <li>
                        <div className="icon">
                            <i className="reserva">
                                <Icon icon="material-symbols:bolt-rounded" />
                            </i>
                        </div>

                        <strong>Reserva Instantânea</strong>
                        <p>
                            Reserve seu veículo em segundos com nossa plataforma fácil de usar. Receba confirmação
                            instantânea e tranquilidade.
                        </p>
                    </li>
                    <li>
                        <div className="icon">
                            <i className="protecao">
                                <Icon icon="material-symbols:calendar-month" />
                            </i>
                        </div>

                        <strong>Reserve do seu jeito!</strong>
                        <p>
                            Você pode alugar um automóvel de forma antecipada ou imediata!
                        </p>
                    </li>
                    <li>
                        <div className="icon">
                            <i className="atendimento">
                                <Icon icon="material-symbols:location-on" />
                            </i>
                        </div>

                        <strong>Devolução flexível</strong>
                        <p>
                            Devolva o automóvel em qualquer uma de nossas filiais.
                        </p>
                    </li>
                </ul>
            </section>
        </>
    );
};

export default HomeEscolha;
