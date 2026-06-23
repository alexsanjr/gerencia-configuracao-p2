import { useState } from "react";

import "./sidebar.min.css";
import { Icon } from "@iconify/react/dist/iconify.js";
import { NavLink } from "react-router-dom";
import userSvg from "../../../assets/user.svg";
// import useAuthStore from "../../context/auth_store/auth_store";

interface SidebarItem {
    icon: string;
    label: string;
    active?: boolean;
    route: string;
}

const Sidebar: React.FC = () => {
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    const items: SidebarItem[] = [
        { icon: "dashicons:car", label: "Frota", route: "/adm" },
        { icon: "dashicons-randomize", label: "Transferências", route: "/lista_transferencias" },
        { icon: "dashicons-calendar", label: "Reservas", route: "/lista_reservas" },
        { icon: "dashicons-calendar", label: "Manutencoes", route: "/lista_manutencao" },
    ];

    // const { logout } = useAuthStore();

    return (
        <>
            <section className={`sidebar gradient-bg ${mobileMenuOpen ? "active" : ""}`}>
                <div className="logo-container">
                    {/* <figure>
                        <img src="../../../../src/assets/Logo.svg?react" alt="Logo G4" />
                    </figure> */}
                    <span className="logo-text">Administração</span>
                </div>

                <nav className="nav">
                    {items.map((item, index) => (
                        <NavLink
                            to={item.route}
                            end
                            className={`sidebar-item ${item.active ? "active" : ""}`}
                            key={index}
                        >
                            <i className="icon">
                                <Icon icon={item.icon} />
                            </i>
                            <span>{item.label}</span>
                        </NavLink>
                    ))}
                </nav>

                <div className="user-profile">
                    <img src={userSvg} alt="User" />
                    <div className="user-info">
                        <p className="user-name">Admin user</p>
                        <p className="userRole">Admin</p>
                    </div>

                    <button className="btn-logout" onClick={() => (window.location.href = "/")}>
                        <i>
                            <Icon icon="wpf:shutdown" />
                        </i>
                    </button>
                </div>
            </section>

            <div className={`overlay ${mobileMenuOpen ? "active" : ""}`} onClick={() => setMobileMenuOpen(false)} />
        </>
    );
};

export default Sidebar;
