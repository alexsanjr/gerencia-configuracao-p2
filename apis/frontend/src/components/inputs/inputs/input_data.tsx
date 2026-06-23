// components/DatePickerField.jsx
import React from "react";
import { Icon } from "@iconify/react";

interface Props extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    icon: string;
}

const InputDate: React.FC<Props> = ({ label, icon = "mdi:calendar", ...rest }) => {
    return (
        <div className="input-wrapper">
            {label && <label className="input-label">{label}</label>}
            <div className="input-container">
                <Icon icon={icon} className="input-icon" />
                <input type="date" className="input-field" {...rest} />
            </div>
        </div>
    );
};

export default InputDate;
