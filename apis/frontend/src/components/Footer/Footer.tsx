import "./Footer.css";
import logo from "/logo_rodape.png";
import phoneIcon from "../../assets/phone.svg";

export default function Footer() {
    return (
        <footer className="footer">
            <div className="footer-content">
                <div className="footer-section logo-section">
                    <img src={logo} alt="Logo" className="footer-logo" />
                </div>

                <div className="footer-section links-section">
                    <h4>PARA VOCÊ</h4>
                    <ul>
                        <li>
                            <a href="#">Home</a>
                        </li>
                        <span>|</span>
                        <li>
                            <a href="#">Frota</a>
                        </li>
                        <span>|</span>
                        <li>
                            <a href="#">Reserva</a>
                        </li>
                        <span>|</span>
                        <li>
                            <a href="#">Minhas reservas</a>
                        </li>
                    </ul>
                </div>

                <div className="footer-section contact-section">
                    <h4>CONTATO:</h4>
                    <div className="phone">
                        <img src={phoneIcon} alt="Telefone" />
                        <span>Central de reservas · 0800 956 2020</span>
                    </div>
                </div>
            </div>

            <div className="copyright">© 2025 Você-Aluga. Todos os direitos reservados.</div>
        </footer>
    );
}
