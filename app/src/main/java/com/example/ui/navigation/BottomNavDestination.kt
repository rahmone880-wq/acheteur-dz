package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation destinations for Acheteur DZ.
 * Encapsulates routes, labels, Material 3 selected & unselected icons, and automated test tags.
 */
sealed class BottomNavDestination(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  object Dashboard : BottomNavDestination(
    route = Screen.Dashboard.route,
    label = "Dashboard",
    selectedIcon = Icons.Filled.Dashboard,
    unselectedIcon = Icons.Outlined.Dashboard,
    testTag = "nav_tab_dashboard"
  )

  object Achats : BottomNavDestination(
    route = Screen.Procurement.route,
    label = "Achats",
    selectedIcon = Icons.Filled.ShoppingCart,
    unselectedIcon = Icons.Outlined.ShoppingCart,
    testTag = "nav_tab_achats"
  )

  object Fournisseurs : BottomNavDestination(
    route = Screen.Suppliers.route,
    label = "Fournisseurs",
    selectedIcon = Icons.Filled.Business,
    unselectedIcon = Icons.Outlined.Business,
    testTag = "nav_tab_fournisseurs"
  )

  object Projets : BottomNavDestination(
    route = Screen.Projects.route,
    label = "Projets",
    selectedIcon = Icons.Filled.Architecture,
    unselectedIcon = Icons.Outlined.Architecture,
    testTag = "nav_tab_projets"
  )

  object Plus : BottomNavDestination(
    route = Screen.Plus.route,
    label = "Plus",
    selectedIcon = Icons.Filled.MoreHoriz,
    unselectedIcon = Icons.Outlined.MoreHoriz,
    testTag = "nav_tab_plus"
  )

  companion object {
    val items: List<BottomNavDestination> = listOf(
      Dashboard,
      Achats,
      Fournisseurs,
      Projets,
      Plus
    )
  }
}
