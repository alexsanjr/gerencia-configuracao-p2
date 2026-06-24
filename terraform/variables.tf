variable "location" {
  description = "Regiao Azure onde os recursos serao provisionados."
  type        = string
  default     = "northcentralus"
}

variable "resource_group_name" {
  description = "Nome do Resource Group exclusivo do trabalho."
  type        = string
  default     = "rg-voce-aluga-dev"
}

variable "postgresql_server_name" {
  description = "Nome globalmente unico do Azure PostgreSQL Flexible Server."
  type        = string
}

variable "database_name" {
  description = "Banco logico usado pelo backend Spring."
  type        = string
  default     = "voce_aluga"
}

variable "postgresql_administrator_login" {
  description = "Usuario administrador inicial do PostgreSQL."
  type        = string
  default     = "vocealugaadmin"
}

variable "postgresql_administrator_password" {
  description = "Senha do administrador do PostgreSQL. Defina por TF_VAR_postgresql_administrator_password."
  type        = string
  sensitive   = true
}

variable "allowed_ip_cidrs" {
  description = "CIDRs publicos autorizados a acessar o PostgreSQL. Nao deixe vazio antes de conectar a aplicacao."
  type        = set(string)
  default     = []
}

variable "aks_cluster_name" {
  description = "Nome do cluster Azure Kubernetes Service."
  type        = string
  default     = "aks-voce-aluga-dev"
}

variable "aks_node_count" {
  description = "Quantidade fixa de workers do AKS; o trabalho exige no minimo dois."
  type        = number
  default     = 2

  validation {
    condition     = var.aks_node_count >= 2
    error_message = "O AKS deve ter ao menos dois nodes worker."
  }
}

variable "aks_node_vm_size" {
  description = "SKU das VMs worker do AKS."
  type        = string
  default     = "Standard_D2s_v3"
}

variable "frontend_vm_name" {
  description = "Nome da VM que executara o container Docker do frontend."
  type        = string
  default     = "vm-voce-aluga-frontend"
}

variable "frontend_vm_size" {
  description = "SKU da VM do frontend."
  type        = string
  default     = "Standard_D2as_v4"
}

variable "frontend_vm_admin_username" {
  description = "Usuario administrador da VM do frontend."
  type        = string
  default     = "azureuser"
}

variable "frontend_vm_admin_ssh_public_key" {
  description = "Chave publica SSH do administrador da VM."
  type        = string
  sensitive   = true
}

variable "allowed_ssh_cidrs" {
  description = "CIDRs autorizados a acessar a VM por SSH."
  type        = set(string)
}

variable "allowed_frontend_cidrs" {
  description = "CIDRs autorizados a acessar o frontend. Use o IP de saida do AKS."
  type        = set(string)
}

variable "tags" {
  description = "Tags aplicadas aos recursos Azure."
  type        = map(string)
  default = {
    project     = "voce-aluga"
    environment = "homologacao"
    managed_by  = "terraform"
  }
}
