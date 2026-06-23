import "./Card.css";

interface newcard {
    icon: string;
    title: string;
    description: string;
}

export default function Card({ icon, title, description }: newcard) {
    return (
        <div className="feature-card">
            <div className="feature-icon">
                <img src={icon} alt="Ícone" />
            </div>
            <div className="feature-text">
                <p className="feature-title">{title}</p>
                <p className="feature-description">{description}</p>
            </div>
        </div>
    );
}
