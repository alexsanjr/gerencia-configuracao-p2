import { Icon } from "@iconify/react/dist/iconify.js";
import { NavLink } from "react-router-dom";

import "./Rate.min.css";

const HomeRate: React.FC = () => {
    return (
        <>
            <section className="container-rate">
                <section className="container-deg">
                    <div className="desc">
                        <strong>Pronto para uma viagem inesquecível?</strong>
                        <p>
                            Junte-se a milhares de clientes satisfeitos que já experimentaram a diferença do Você Aluga.
                        </p>

                        <NavLink to={"/aluguel"}>
                            Reserve agora{" "}
                            <i>
                                <Icon icon="cuida:arrow-right-outline" />
                            </i>
                        </NavLink>
                    </div>

                    <div className="container-rate">
                        <div className="rate">
                            <span className="rate__um"></span>
                            <span className="rate__dois"></span>
                            <span className="rate__tres">
                                <span>4.9</span>
                                <div className="stars">
                                    {Array.from({ length: 5 }).map((_, index) => (
                                        <i key={index}>
                                            <Icon icon="material-symbols:star-rounded" />
                                        </i>
                                    ))}
                                </div>
                                <p>Avaliação dos clientes</p>
                            </span>
                        </div>
                    </div>
                </section>
            </section>
        </>
    );
};

export default HomeRate;
