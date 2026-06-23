import NavBar from "../../components/NavBar/NavBar";
import Banner from "../../components/Banner/Banner";
import Footer from "../../components/Footer/Footer";

import "./style.min.css";
import HomeAluguel from "./HomeAluguel/HomeAluguel";
import HomeEscolha from "./HomeEscolha/HomeEscolha";
import HomeRate from "./Rate/Rate";

export default function Home() {
    return (
        <>
            <NavBar />
            <section className="container-body">
                <Banner />

                <div className="center">
                    <HomeAluguel />
                    <HomeEscolha />
                    <HomeRate />
                </div>
            </section>
            <Footer />
        </>
    );
}
