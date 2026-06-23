resource "azurerm_resource_group" "voce_aluga" {
  name     = var.resource_group_name
  location = var.location

  tags = var.tags
}

module "database" {
  source = "./modules/database"

  resource_group_name    = azurerm_resource_group.voce_aluga.name
  location               = azurerm_resource_group.voce_aluga.location
  server_name            = var.postgresql_server_name
  database_name          = var.database_name
  administrator_login    = var.postgresql_administrator_login
  administrator_password = var.postgresql_administrator_password
  allowed_ip_cidrs       = var.allowed_ip_cidrs

  tags = var.tags
}
