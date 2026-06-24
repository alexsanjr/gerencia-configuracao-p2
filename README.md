# Você Aluga — Infraestrutura e DevOps

Infraestrutura criada para o projeto original [alexsanjr/voce-aluga](https://github.com/alexsanjr/voce-aluga).
O repositório original contém a lógica de negócio; este repositório concentra os artefatos de Docker, Azure, Kubernetes, Ansible e CI/CD.

## Aplicações

| Aplicação | Tecnologia | Responsabilidade | Publicação |
| --- | --- | --- | --- |
| Frontend | React + Vite | Interface web para clientes e administração | Container Docker em VM Azure |
| Backend | Spring Boot 3 / Java 21 | API REST, autenticação, reservas, veículos e pagamentos | AKS, com duas réplicas |

O frontend usa o caminho relativo `/api`. Portanto, não possui dependência de `localhost` no ambiente publicado.

## Arquitetura

```text
Internet
   |
   v
NGINX Ingress / Gateway (IP público)
   |-- /      -> frontend Docker na VM Azure
   '-- /api/* -> Service do backend no AKS -> Azure PostgreSQL

AKS (namespace voce-aluga)
   |-- backend: Deployment, 2 réplicas, HPA, probes e limites
   '-- PostgreSQL demonstrativo: primário + réplica via StatefulSet
```

O gateway é a única entrada HTTP pública da aplicação. A VM do frontend permite HTTP somente a partir do IP de saída do AKS.

## Endereços de homologação

| Serviço | Endereço |
| --- | --- |
| Gateway e frontend | `http://20.88.36.19/` |
| Saúde do backend | `http://20.88.36.19/api/actuator/health` |
| Veículos pela API | `http://20.88.36.19/api/veiculos` |

> Os endereços dependem da infraestrutura Azure em execução. A VM possui IP estático e o IP do gateway deve ser confirmado com `kubectl -n ingress-nginx get service ingress-nginx-controller`.

## Estrutura

```text
apis/
  backend/                 # Spring Boot e Dockerfile
  frontend/                # React/Vite e Dockerfile multi-stage
terraform/                 # Resource Group, PostgreSQL, AKS e VM Docker
kubernetes/                # Namespace, backend, gateway e PostgreSQL primário/réplica
ansible/                   # Deploy da VM Docker e do AKS
.github/workflows/         # Pipeline GitHub Actions self-hosted
```

## Provisionamento Azure

Pré-requisitos: Azure CLI autenticada, Terraform e uma chave SSH pública.

```bash
terraform -chdir=terraform init
terraform -chdir=terraform plan -out=tfplan
terraform -chdir=terraform apply tfplan
```

As variáveis locais ficam em `terraform/terraform.tfvars`, que não é versionado. Use [terraform.tfvars.example](terraform/terraform.tfvars.example) como referência.

Após o apply, conecte o `kubectl`:

```bash
az aks get-credentials \
  --resource-group rg-voce-aluga-dev \
  --name aks-voce-aluga-dev \
  --overwrite-existing
```

## Kubernetes

O namespace `voce-aluga` possui `ResourceQuota` e `LimitRange`. O backend usa `RollingUpdate`, duas réplicas, `readinessProbe`, `livenessProbe` e HPA de CPU em 60%.

Os Secrets reais não são versionados. Crie-os a partir dos exemplos:

```bash
cp kubernetes/backend/backend-secret.example.yaml kubernetes/backend/backend-secret.yaml
cp kubernetes/database/postgresql-secret.example.yaml kubernetes/database/postgresql-secret.yaml
```

Em seguida, aplique os recursos:

```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/resource-quota.yaml
kubectl apply -f kubernetes/limit-range.yaml
kubectl apply -f kubernetes/backend/backend-secret.yaml
kubectl apply -f kubernetes/backend/
kubectl apply -f kubernetes/gateway/
kubectl apply -f kubernetes/database/postgresql-secret.yaml
kubectl apply -f kubernetes/database/postgresql-primary.yaml
kubectl apply -f kubernetes/database/postgresql-replica.yaml
```

O PostgreSQL no AKS é demonstrativo e não é a base ativa do backend. A aplicação usa o Azure PostgreSQL provisionado pelo Terraform.

## Docker e Ansible

As imagens publicadas no Docker Hub privado são:

```text
alexsanjr/voce-aluga-frontend
alexsanjr/voce-aluga-backend
```

Para deploy local via Ansible, crie um inventário a partir do exemplo e exporte as credenciais do Docker Hub:

```bash
cp ansible/inventory/homologacao.ini.example ansible/inventory/homologacao.ini
export DOCKER_HUB_USERNAME="alexsanjr"
export DOCKER_HUB_TOKEN="seu-token"

ANSIBLE_CONFIG=ansible/ansible.cfg \
ansible-playbook --private-key ~/.ssh/id_ed25519_vm \
  ansible/playbooks/deploy-docker.yml \
  -e frontend_image=alexsanjr/voce-aluga-frontend:latest
```

O deploy do backend usa o contexto atual do `kubectl`:

```bash
ANSIBLE_CONFIG=ansible/ansible.cfg \
ansible-playbook -i localhost, ansible/playbooks/deploy-k8s.yml
```

## CI/CD

O workflow [pipeline.yml](.github/workflows/pipeline.yml) usa um runner GitHub Actions self-hosted Linux e executa:

1. testes Maven do backend e Vitest do frontend;
2. SonarQube em container com Quality Gate bloqueante;
3. SAST com Semgrep;
4. build e push da imagem privada do frontend;
5. deploy da VM e do AKS pelos playbooks Ansible;
6. DAST com OWASP ZAP contra o gateway;
7. publicação do relatório ZAP como artefato.

Secrets necessários no GitHub Actions:

```text
SONAR_TOKEN
DOCKER_HUB_USERNAME
DOCKER_HUB_TOKEN
FRONTEND_VM_IP
SSH_PRIVATE_KEY
```

O runner precisa ter Docker, Ansible, Azure CLI, `kubectl` autenticado no AKS e um container persistente chamado `sonarqube` acessível em `http://127.0.0.1:9000`.

## Evidências de aceite

```bash
# Dois workers AKS
kubectl get nodes

# Backend com duas réplicas e HPA
kubectl -n voce-aluga get deployment,pods,hpa

# Primário e réplica PostgreSQL
kubectl -n voce-aluga get statefulset,pods,pvc

# Gateway
curl http://20.88.36.19/api/actuator/health
curl http://20.88.36.19/api/veiculos
```

## Situação dos requisitos

Implementados: duas aplicações, gateway, Docker multi-stage e registry privado, AKS com dois workers, backend com Deployment/HPA/probes/quotas, PostgreSQL Azure via Terraform, cluster de leitura PostgreSQL no AKS, Ansible e pipeline com testes, SonarQube, Semgrep e ZAP.

Pendente para aderência literal ao requisito de Terraform: criar uma VM exclusiva para o runner de CI. Atualmente o runner self-hosted executa na máquina local pois o Azure students limita a apenas 3 ips públicos para uso; a VM provisionada pelo Terraform hospeda o frontend Docker.
