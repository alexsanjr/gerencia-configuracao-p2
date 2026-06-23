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
