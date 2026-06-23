import { Routes, Route } from "react-router-dom";
import PrivateRoute from "./PrivateRoute";
import Home from "../pages/Home/Home";
import Login from "../pages/Login/Login";
import Reserva from "../pages/Reserva/Reserva";
import Register from "../pages/Register/Register";
import Pagamento from "../pages/Pagamento/Pagamento";
import Aluguel from "../pages/Aluguel/Aluguel";
import MinhasReservas from "../pages/MinhasReservas/MinhasReservas";
import CadastroMotorista from "../pages/CadastroMotorista/CadastroMotorista";

import { lazy } from "react";
import EditarVeiculo from "../pages/adm/editar_veiculo/editar_veiculo";
import ListaTransferencia from "../pages/adm/lista_transferencia/lista_transferencia";
import NovaTransferencia from "../pages/adm/nova_transferencia/nova_transferencia";
import ListaReservas from "../pages/adm/lista_reservas/lista_reservas";
import ListaManutencao from "../pages/adm/lista_manutencao/lista_manutencao";
import NovaManutencao from "../pages/adm/nova_manutencao/nova_manutencao";

const Adm = lazy(() => import("../pages/adm/adm"));
const NovoVeiculo = lazy(() => import("../pages/adm/novo_veiculo/novo_veiculo"));

export default function Router() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
                path="/reserva"
                element={
                    <PrivateRoute>
                        <Reserva />
                    </PrivateRoute>
                }
            />

            <Route
                path="/cadastro-motorista"
                element={
                    <PrivateRoute>
                        <CadastroMotorista />
                    </PrivateRoute>
                }
            />

            <Route
                path="/minhas-reservas"
                element={
                    <PrivateRoute>
                        <MinhasReservas />
                    </PrivateRoute>
                }
            />

            <Route
                path="/pagamento"
                element={
                    // <PrivateRoute>
                        <Pagamento />
                    // </PrivateRoute>
                }
            />

            <Route
                path="/aluguel"
                element={
                    <PrivateRoute>
                        <Aluguel />
                    </PrivateRoute>
                }
            />

            <Route
                path="/adm"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <Adm />
                    </PrivateRoute>
                }
            />

            <Route
                path="/novoveiculo"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <NovoVeiculo />
                    </PrivateRoute>
                }
            />

            <Route
                path="/editarveiculo/:id"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <EditarVeiculo />
                    </PrivateRoute>
                }
            />

            <Route
                path="/lista_transferencias"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <ListaTransferencia />
                    </PrivateRoute>
                }
            />

            <Route
                path="/nova_transferencia"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <NovaTransferencia />
                    </PrivateRoute>
                }
            />

            <Route
                path="/lista_reservas"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <ListaReservas />
                    </PrivateRoute>
                }
            />

            <Route
                path="/lista_manutencao"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <ListaManutencao />
                    </PrivateRoute>
                }
            />

            <Route
                path="/nova_manutencao"
                element={
                    <PrivateRoute role={["ROLE_ADMININISTRADOR", "ROLE_GERENTE"]}>
                        <NovaManutencao />
                    </PrivateRoute>
                }
            />
        </Routes>
    );
}
