import { Icon } from "@iconify/react/dist/iconify.js";
import "./Banner.min.css";
import SplitText from "./text/text-animation";
import Vehicle from "../../assets/background-banner.png";

export default function Banner() {
    return (
        <header className="container-banner">
            <section className="container-header">
                <div className="text">
                    <div className="text-animation">
                        <SplitText
                            text="Aluguel de veículos é com a Você Aluga!"
                            className="text-an"
                            delay={100}
                            duration={0.6}
                            ease="power3.out"
                            splitType="chars"
                            from={{ opacity: 0, y: 40 }}
                            to={{ opacity: 1, y: 0 }}
                            threshold={0.5}
                            rootMargin="-100px"
                            textAlign="start"
                        />
                    </div>

                    <p className={`termino`}>
                        Experimente luxo, conforto e desempenho com nossa frota premium de veículos a preços imbatíveis.
                    </p>

                    <div className="btns">
                        <a href="/aluguel">
                            <button className="aproveite">
                                Reserve agora!{" "}
                                <i>
                                    <Icon icon="cuida:arrow-right-outline" />
                                </i>
                            </button>
                        </a>
                    </div>
                </div>

                <div className="vehicle">
                    <figure>
                        <img src={Vehicle} alt="" />

                        <figcaption>
                            <i>
                                <Icon icon="material-symbols:trophy-rounded" />
                            </i>

                            <div className="text-description">
                                <span>Premium</span>
                                <p>Melhor Serviço de Locação de 2025</p>
                            </div>
                        </figcaption>
                    </figure>
                </div>
            </section>
        </header>
    );
}
