package com.example

import com.example.ui.navigation.BottomNavDestination
import com.example.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationUnitTest {

  @Test
  fun testBottomNavDestinationsCompletenessAndOrder() {
    val items = BottomNavDestination.items
    assertEquals(5, items.size)

    assertEquals("Dashboard", items[0].label)
    assertEquals(Screen.Dashboard.route, items[0].route)
    assertEquals("nav_tab_dashboard", items[0].testTag)

    assertEquals("Achats", items[1].label)
    assertEquals(Screen.Procurement.route, items[1].route)
    assertEquals("nav_tab_achats", items[1].testTag)

    assertEquals("Fournisseurs", items[2].label)
    assertEquals(Screen.Suppliers.route, items[2].route)
    assertEquals("nav_tab_fournisseurs", items[2].testTag)

    assertEquals("Projets", items[3].label)
    assertEquals(Screen.Projects.route, items[3].route)
    assertEquals("nav_tab_projets", items[3].testTag)

    assertEquals("Plus", items[4].label)
    assertEquals(Screen.Plus.route, items[4].route)
    assertEquals("nav_tab_plus", items[4].testTag)
  }

  @Test
  fun testUniqueRoutesAndTags() {
    val items = BottomNavDestination.items
    val uniqueRoutes = items.map { it.route }.toSet()
    val uniqueTags = items.map { it.testTag }.toSet()

    assertEquals(5, uniqueRoutes.size)
    assertEquals(5, uniqueTags.size)
  }

  @Test
  fun testScreenRoutesFormat() {
    assertEquals("dashboard", Screen.Dashboard.route)
    assertEquals("procurement", Screen.Procurement.route)
    assertEquals("suppliers", Screen.Suppliers.route)
    assertEquals("projects", Screen.Projects.route)
    assertEquals("plus", Screen.Plus.route)
    assertEquals("module_skeleton/achats", Screen.ModuleSkeleton.createRoute("achats"))
    assertEquals("empty_dashboard", Screen.DashboardEmpty.route)
    assertEquals("empty_achats", Screen.AchatsEmpty.route)
    assertEquals("empty_fournisseurs", Screen.FournisseursEmpty.route)
    assertEquals("empty_projets", Screen.ProjetsEmpty.route)
    assertEquals("empty_plus", Screen.PlusEmpty.route)
  }
}
