import "./NavBar.min.css";
import logoImg from "/logo_header.png";
import { useState, useEffect } from "react";
import { parseJwt } from "../../utils/jwt";
import { Icon } from "@iconify/react";


export default function NavBar() {
    const [isLogged, setIsLogged] = useState(() => !!localStorage.getItem("token"));
    const [role, setRole] = useState<string | null>(null);

    function handleLogout(e: React.MouseEvent) {
        e.preventDefault();
        localStorage.removeItem("token");
        setIsLogged(false);
        setRole(null);
        window.location.href = "/login";
    }

    // Atualiza o estado se o token mudar em outra aba
    useEffect(() => {
        function updateAuth() {
            const token = localStorage.getItem("token");
            setIsLogged(!!token);
            if (token) {
                const payload = parseJwt(token);
                setRole(payload?.role || null);
            } else {
                setRole(null);
            }
        }
        updateAuth();
        window.addEventListener("storage", updateAuth);
        return () => window.removeEventListener("storage", updateAuth);
    }, []);

    return (
        <div className="container-navbar">
            <div className="limit">
                <a href="/" className="logo">
                    <figure>
                        <img src={logoImg} alt="Logo" />
                    </figure>
                </a>

                <nav className="navegacao">
                    <ul className="menus">
                        <li className={`menu-item ${window.location.pathname === '/' ? 'active' : ''}`}>
                            <a href="/">
                                <Icon icon="tabler:home" />
                                <span>HOME</span>
                            </a>
                        </li>
                        <li className={`menu-item ${window.location.pathname === '/aluguel' ? 'active' : ''}`}>
                            <a href="/aluguel">
                                <Icon icon="tabler:car" />
                                <span>RESERVAR AGORA</span>
                            </a>
                        </li>
                        <li className={`menu-item ${window.location.pathname === '/minhas-reservas' ? 'active' : ''}`}>
                            <a href="/minhas-reservas">
                                <Icon icon="tabler:list" />
                                <span>MINHAS RESERVAS</span>
                            </a>
                        </li>
                        {isLogged && (role === "ROLE_ADMININISTRADOR" || role === "ROLE_GERENTE") && (
                            <li className={`menu-item ${window.location.pathname.startsWith('/adm') ? 'active' : ''}`}>
                                <a href="/adm">
                                    <Icon icon="tabler:dashboard" />
                                    <span>PAINEL</span>
                                </a>
                            </li>
                        )}
                        {isLogged ? (
                            <li className="menu-item logout">
                                <a href="#" onClick={handleLogout}>
                                    <Icon icon="tabler:logout" />
                                    <span>SAIR</span>
                                </a>
                            </li>
                        ) : (
                            <li className="menu-item login">
                                <a href="/login">
                                    <Icon icon="tabler:login" />
                                    <span>ENTRAR</span>
                                </a>
                            </li>
                        )}
                    </ul>
                </nav>
            </div>
        </div>
    );
}
