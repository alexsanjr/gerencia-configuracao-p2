output "postgresql_server_fqdn" {
  description = "Host para DB_URL do backend."
  value       = module.database.fqdn
}

output "postgresql_database_name" {
  value = module.database.database_name
}

output "postgresql_connection_url" {
  description = "URL JDBC sem credenciais; use-a na variavel DB_URL do backend."
  value       = "jdbc:postgresql://${module.database.fqdn}:5432/${module.database.database_name}?sslmode=require"
}

output "aks_cluster_name" {
  description = "Nome do AKS para obter as credenciais kubectl."
  value       = module.kubernetes.cluster_name
}

output "aks_resource_group_name" {
  description = "Resource Group onde o AKS foi criado."
  value       = azurerm_resource_group.voce_aluga.name
}

output "frontend_vm_public_ip" {
  description = "IP publico da VM Docker. O acesso HTTP fica restrito ao gateway."
  value       = module.compute.public_ip_address
}

output "frontend_vm_private_ip" {
  description = "IP privado da VM Docker."
  value       = module.compute.private_ip_address
}

output "frontend_vm_name" {
  value = module.compute.vm_name
}
