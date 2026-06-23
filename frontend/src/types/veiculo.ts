export interface Veiculo {
    id: number;
    modelo: string;
    marca: string;
    placa: string;
    ano: number;
    cor: string;
    filial: string;
    statusVeiculo: string;
    imagemUrl?: string;
    imagens?: string[]; // Para múltiplas imagens
    [key: string]: any;
}
