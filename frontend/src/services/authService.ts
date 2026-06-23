import { jwtDecode } from "jwt-decode";

interface JwtPayload {
    usuario_id: number;
    role: string;
    sub: string; // email do usuário
    iat: number; // issued at
    exp: number; // expiração
}

export const getLoggedUserId = (): number | null => {
    const token = localStorage.getItem("token");
    if (!token) return null;

    try {
        const decoded = jwtDecode<JwtPayload>(token);
        return decoded.usuario_id;
    } catch (error) {
        console.error("Erro ao decodificar token:", error);
        return null;
    }
};
