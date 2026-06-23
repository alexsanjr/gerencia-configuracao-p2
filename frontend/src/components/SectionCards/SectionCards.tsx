import Newcard from "../Card/Card";
import calendar from "../../assets/calendar.png";
import devolver_car from "../../assets/devolver_car.png";
import car from "../../assets/car.png";
import "./SectionCards.css";

export default function SectionCard() {
    return (
        <>
            <section className="features-container">
                <Newcard
                    icon={calendar}
                    title="Reserve do seu jeito!"
                    description="Você pode alugar um automóvel de forma antecipada ou imediata!"
                />
                <Newcard
                    icon={devolver_car}
                    title="Devolução flexível"
                    description="Devolva o automóvel em qualquer uma de nossas filiais."
                />
                <Newcard
                    icon={car}
                    title="Frota diversificada"
                    description="Escolha entre uma ampla variedade de automóveis, desde modelos econômicos até veículos mais chiques."
                />
            </section>
        </>
    );
}
