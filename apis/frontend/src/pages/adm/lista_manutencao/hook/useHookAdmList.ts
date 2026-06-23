import { useState, useEffect } from 'react';
import { getAllVeiculos } from '../../../../services/veiculosService';

interface Veiculo {
    id: number;
    marca: string;
    modelo: string;
    placa: string;
    statusVeiculo: string;
}

const useHookAdmList = () => {
    const [Lista_veiculos, setListaVeiculos] = useState<Veiculo[]>([]);

    const atualizarLista = async () => {
        try {
            const veiculos = await getAllVeiculos();
            setListaVeiculos(veiculos);
        } catch (error) {
            console.error('Erro ao atualizar lista de veículos:', error);
            alert('Erro ao carregar lista de veículos');
        }
    };

    useEffect(() => {
        atualizarLista();
    }, []);

    return { Lista_veiculos, atualizarLista };
};

export default useHookAdmList;
