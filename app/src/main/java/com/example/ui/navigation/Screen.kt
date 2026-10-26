package com.example.ui.navigation

sealed class Screen(val route: String) {
  object Dashboard : Screen("dashboard")
  object Projects : Screen("projects")
  object ProjectDetail : Screen("project_detail/{projectId}") {
    fun createRoute(projectId: String) = "project_detail/$projectId"
  }
  object ProjectEdit : Screen("project_edit")
  object Procurement : Screen("procurement")
  object Suppliers : Screen("suppliers")
  object SupplierDetail : Screen("supplier_detail/{supplierId}") {
    fun createRoute(supplierId: String) = "supplier_detail/$supplierId"
  }
  object SupplierEdit : Screen("supplier_edit?supplierId={supplierId}") {
    fun createRoute(supplierId: String? = null) =
      if (supplierId != null) "supplier_edit?supplierId=$supplierId" else "supplier_edit"
  }
  object Products : Screen("products")
  object ProductEdit : Screen("product_edit")
  object AuditLog : Screen("audit_log")
  object Settings : Screen("settings")
  object UserProfile : Screen("user_profile")
  object ReceiptScanner : Screen("receipt_scanner")
  object Plus : Screen("plus")
  object ModuleSkeleton : Screen("module_skeleton/{moduleKey}") {
    fun createRoute(moduleKey: String) = "module_skeleton/$moduleKey"
  }
  object DashboardEmpty : Screen("empty_dashboard")
  object AchatsEmpty : Screen("empty_achats")
  object FournisseursEmpty : Screen("empty_fournisseurs")
  object ProjetsEmpty : Screen("empty_projets")
  object PlusEmpty : Screen("empty_plus")
  object DataTransfer : Screen("data_transfer")
}

