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

variable "tags" {
  description = "Tags aplicadas aos recursos Azure."
  type        = map(string)
  default = {
    project     = "voce-aluga"
    environment = "homologacao"
    managed_by  = "terraform"
  }
}
