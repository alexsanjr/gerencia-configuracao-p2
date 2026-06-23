// components/InputField.jsx
import React from "react";
import { Icon } from "@iconify/react";

import "./styles.css";

interface Props extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    icon: string;
}

const InputText: React.FC<Props> = ({ label, icon, ...rest }) => {
    return (
        <div className="input-wrapper">
            {label && <label className="input-label">{label}</label>}
            <div className="input-container">
                {icon && <Icon icon={icon} className="input-icon" />}
                <input className="input-field" {...rest} />
            </div>
        </div>
    );
};

export default InputText;
