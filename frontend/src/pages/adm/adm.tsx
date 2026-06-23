import ListaFrota from "./lista_frota/lista_frota";
import ListaReservas from "./lista_reservas/lista_reservas";
import ListaTransferencia from "./lista_transferencia/lista_transferencia";
import { useState } from "react";

const Adm: React.FC = () => {
    const [pagina, setPagina] = useState<"frota" | "transferencia" | "reservas">("frota");

    return (
        <>
            {pagina === "frota" && <ListaFrota />}
            {pagina === "transferencia" && <ListaTransferencia />}
            {pagina === "reservas" && <ListaReservas />}
        </>
    );
};

export default Adm;
