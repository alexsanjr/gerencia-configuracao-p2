// components/SelectField.jsx
import React from "react";
import { Icon } from "@iconify/react";

interface Options {
    label: string;
    value: string;
}

interface Props extends React.SelectHTMLAttributes<HTMLSelectElement> {
    label?: string;
    icon: string;
    options: Options[];
}

const InputSelect: React.FC<Props> = ({ label, options = [], value, onChange, icon }) => {
    return (
        <div className="input-wrapper">
            {label && <label className="input-label">{label}</label>}
            <div className="input-container">
                {icon && <Icon icon={icon} className="input-icon" />}
                <select value={value} onChange={onChange} className="input-field">
                    {options.map((option, index) => (
                        <option key={index} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
};

export default InputSelect;
