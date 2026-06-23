INSERT INTO filial (nome, local) VALUES ('Filial 1', 'Rio de Janeiro');
INSERT INTO filial (nome, local) VALUES ('Filial 2', 'São Paulo');
INSERT INTO filial (nome, local) VALUES ('Filial 3', 'Salvador');


-- Inserir base do usuário
INSERT INTO usuario (nome, documento, data_de_nascimento, email, password, telefone) VALUES ('Carlos Gerente', '12345678900', '1985-05-15', 'carlos@email.com', '$2a$10$TDeYzJ7x6TDrs6EgIuLIluUr/EHuouYlWRBBRjXalmiwsfX1vCyYe', '21999999999');
INSERT INTO usuario (nome, documento, data_de_nascimento, email, password, telefone) VALUES ('Ana Admin', '11122233300', '1982-03-10', 'ana@email.com', '$2a$10$TDeYzJ7x6TDrs6EgIuLIluUr/EHuouYlWRBBRjXalmiwsfX1vCyYe', '21888888888');
INSERT INTO usuario (nome, documento, data_de_nascimento, email, password, telefone) VALUES ('Bruno Cliente', '77788899900', '1992-09-22', 'bruno@email.com', '$2a$10$TDeYzJ7x6TDrs6EgIuLIluUr/EHuouYlWRBBRjXalmiwsfX1vCyYe', '21911112222');


-- Funcionário
INSERT INTO funcionario (id, cargo, filial_id) VALUES (1, 'Gerente Geral', 1);

INSERT INTO funcionario (id, cargo, filial_id) VALUES (2, 'TI', 2);

-- Cliente
INSERT INTO cliente (id, pontos_fidelidade) VALUES (3, 120);


-- Gerente e Administrador
INSERT INTO gerente (id) VALUES (1);
INSERT INTO administrador (id) VALUES (2);


INSERT INTO motorista (cnh, nome, cpf, data_nascimento) VALUES ('12323243434','Bruno Cliente', '12345678900', '1992-09-22');


INSERT INTO filial (nome, local) VALUES ('Filial 1', 'Rio de Janeiro');
INSERT INTO filial (nome, local) VALUES ('Filial 2', 'São Paulo');
INSERT INTO filial (nome, local) VALUES ('Filial 3', 'Salvador');

INSERT INTO estoque (nome, filial_id) VALUES ('Estoque 1',1);
INSERT INTO estoque (nome, filial_id) VALUES ('Estoque 2',2);
INSERT INTO estoque (nome, filial_id) VALUES ('Estoque 3',3);

-- Veiculos Reservado
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id) VALUES ('Toyota', 'Corolla', 0, 2022, 2, 199.9, 34800, 2, 'ABC-1234', 1);

-- Veiculos Em Uso
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id)VALUES ('Chevrolet', 'Onix', 2, 2021, 4, 159.50, 45200, 0, 'DEF-4321', 1);

-- Veiculos Manutencao
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id)VALUES ('Ford', 'Ka', 2, 2020, 1, 129.99, 67800, 3, 'GHI-8765', 2);

-- Veiculos Disponiveis
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id)VALUES ('Honda', 'Civic', 1, 2023, 3, 249.90, 12800, 1, 'XYZ-5678', 2);
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id) VALUES ('Volkswagen', 'Golf', 0, 2022, 1, 189.9, 28500, 1, 'DEF-5678', 1);
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id) VALUES ('Hyundai', 'HB20', 0, 2023, 3, 179.5, 12300, 1, 'GHI-9012', 2);
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id) VALUES ('Nissan', 'Sentra', 0, 2022, 4, 195.0, 22100, 1, 'JKL-3456', 3);
INSERT INTO veiculo (marca, modelo, grupo, ano, cor, valor_Diaria, quilometragem, status_Veiculo, placa, estoque_id) VALUES ('Fiat', 'Cronos', 0, 2021, 0, 185.0, 31200, 1, 'MNO-7890', 1);


INSERT INTO TRANSFERENCIA_VEICULOS (status, data, estoque_destino_id, estoque_origem_id) VALUES (1, TIMESTAMP WITH TIME ZONE '2022-07-25T15:00:00Z', 1, 2);

INSERT INTO TRANSFERENCIA_VEICULOS_VEICULO (transferencia_id, veiculo_id) VALUES (1 ,1);
INSERT INTO TRANSFERENCIA_VEICULOS_VEICULO (transferencia_id, veiculo_id) VALUES (1 ,2);

INSERT INTO ESTACAO_DE_SERVICO (local, nome) VALUES ('São Paulo - SP', 'Filial Centro');
INSERT INTO ESTACAO_DE_SERVICO (local, nome) VALUES ( 'Rio de Janeiro - RJ', 'Filial Centro');
INSERT INTO ESTACAO_DE_SERVICO (local, nome) VALUES ('Curitiba - PR', 'Pátio Sul');

INSERT INTO Agendar_Manutencao (data_manutencao, estacao_de_servico_id, veiculo_id, motivo_manutencao) VALUES (TIMESTAMP WITH TIME ZONE '2025-07-03T10:00:00Z', 2, 3, 'pneu furado');

INSERT INTO Reserva (categoria, status, data_reserva, data_vencimento, filial_id, usuario_id, veiculo_id, motorista_id) VALUES (0, 1, DATE '2025-07-03', DATE '2025-07-10', 1, 3, 1, 1);
INSERT INTO Reserva (categoria, status, data_reserva, data_vencimento, filial_id,  usuario_id, veiculo_id, motorista_id) VALUES (1, 2, DATE '2025-07-03', DATE '2025-07-10', 2, 3, 2, 1);
INSERT INTO Reserva (categoria, status, data_reserva, data_vencimento, filial_id,  usuario_id) VALUES (1, 2, DATE '2025-07-03', DATE '2025-07-10', 2, 1);