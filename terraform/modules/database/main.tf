resource "azurerm_postgresql_flexible_server" "this" {
  name                   = var.server_name
  resource_group_name    = var.resource_group_name
  location               = var.location
  version                = "16"
  administrator_login    = var.administrator_login
  administrator_password = var.administrator_password

  sku_name              = "GP_Standard_D2s_v3"
  storage_mb            = 32768
  backup_retention_days = 7

  public_network_access_enabled = true
  geo_redundant_backup_enabled  = false

  tags = var.tags
}

resource "azurerm_postgresql_flexible_server_database" "this" {
  name      = var.database_name
  server_id = azurerm_postgresql_flexible_server.this.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allowed" {
  for_each = var.allowed_ip_cidrs

  name             = "allow-${replace(replace(each.value, "/", "-"), ".", "-")}"
  server_id        = azurerm_postgresql_flexible_server.this.id
  start_ip_address = cidrhost(each.value, 0)
  end_ip_address   = cidrhost(each.value, -1)
}
