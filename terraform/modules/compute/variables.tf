variable "resource_group_name" {
  type = string
}

variable "location" {
  type = string
}

variable "vm_name" {
  type = string
}

variable "vm_size" {
  type = string
}

variable "admin_username" {
  type = string
}

variable "admin_ssh_public_key" {
  type      = string
  sensitive = true
}

variable "allowed_ssh_cidrs" {
  type = set(string)

  validation {
    condition     = length(var.allowed_ssh_cidrs) > 0
    error_message = "Informe ao menos um CIDR autorizado para SSH."
  }
}

variable "allowed_frontend_cidrs" {
  type = set(string)

  validation {
    condition     = length(var.allowed_frontend_cidrs) > 0
    error_message = "Informe ao menos um CIDR autorizado para o gateway acessar o frontend."
  }
}

variable "tags" {
  type = map(string)
}
