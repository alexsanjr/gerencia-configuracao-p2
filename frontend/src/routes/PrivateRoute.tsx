import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";

import { parseJwt } from "../utils/jwt";

interface PrivateRouteProps {
    children: ReactNode;
    role?: string | string[];
}

export default function PrivateRoute({ children, role }: PrivateRouteProps) {
    const token = localStorage.getItem("token");
    if (!token) {
        return <Navigate to="/login" replace />;
    }
    if (role) {
        const payload = parseJwt(token);
        if (!payload) {
            return <Navigate to="/aluguel" replace />;
        }
        if (Array.isArray(role)) {
            if (!role.includes(payload.role)) {
                return <Navigate to="/aluguel" replace />;
            }
        } else {
            if (payload.role !== role) {
                return <Navigate to="/aluguel" replace />;
            }
        }
    }
    return <>{children}</>;
}
