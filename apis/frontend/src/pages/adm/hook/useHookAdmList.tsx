import { useQuery } from "@tanstack/react-query";
import { getAllVeiculos } from "../../../services/veiculosService";

const useHookAdmList = () => {
    const {
        data,
        refetch,
        // isLoading,
        // isError,
    } = useQuery({
        queryKey: ["veiculos"],
        queryFn: getAllVeiculos,
    });

    // O backend pode retornar { content: Veiculo[] }, então normaliza
    const Lista_veiculos = data?.content || data || [];

    const atualizarLista = () => {
        refetch();
    };

    console.log("Lista de veículos:", Lista_veiculos);
    return { Lista_veiculos, atualizarLista };
};

export default useHookAdmList;
