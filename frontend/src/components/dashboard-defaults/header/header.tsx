interface HeaderProps {
    onMenuToggle: () => void;
    title: string;
}

import { Icon } from "@iconify/react/dist/iconify.js";
import "./header.min.css";

const Header = ({ onMenuToggle, title }: HeaderProps) => {
    return (
        <header className="header-admin">
            <div className="header-content">
                <div className="left-section">
                    <button className="menu-button" onClick={onMenuToggle}>
                        {/* ICONE AQUI */}
                        BAR
                    </button>

                    <h1 className="title">{title}</h1>
                </div>

                <div className="right-section">
                    {/*<div className="notification">
                        {/* ICONE AQUI */
                    /*}
                        <Icon icon="fluent:alert-24-filled" fontSize={18} />
                        <span className="notification-badge"></span>
                    </div>

                    <div className="user">
                        <img src="https://randomuser.me/api/portraits/men/32.jpg" alt="User" />
                        <span className="user-name">Admin user</span>
                    </div> */}
                </div>
            </div>
        </header>
    );
};

export default Header;
