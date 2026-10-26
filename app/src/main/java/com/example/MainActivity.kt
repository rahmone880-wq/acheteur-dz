package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.repository.AuditRepository
import com.example.data.repository.ProcurementRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.SupplierRepository
import com.example.data.seed.DatabaseSeeder
import com.example.ui.audit.AuditLogScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.navigation.Screen
import com.example.ui.procurement.ProcurementHubScreen
import com.example.ui.procurement.ProcurementViewModel
import com.example.ui.products.ProductEditScreen
import com.example.ui.products.ProductListScreen
import com.example.ui.products.ProductViewModel
import com.example.ui.projects.ProjectDetailScreen
import com.example.ui.projects.ProjectEditScreen
import com.example.ui.projects.ProjectListScreen
import com.example.ui.projects.ProjectViewModel
import com.example.ui.settings.SettingsScreen
import com.example.ui.profile.UserProfileScreen
import com.example.ui.suppliers.SupplierDetailScreen
import com.example.ui.suppliers.SupplierEditScreen
import com.example.core.ai.configureAppCheck
import com.example.ui.scanner.ReceiptScannerScreen
import com.example.ui.scanner.ReceiptScannerViewModel
import com.example.ui.suppliers.SupplierListScreen
import com.example.ui.suppliers.SupplierViewModel
import com.example.ui.navigation.BottomNavDestination
import com.example.ui.navigation.skeletons.AchatsEmptyScreen
import com.example.ui.navigation.skeletons.DashboardEmptyScreen
import com.example.ui.navigation.skeletons.FournisseursEmptyScreen
import com.example.ui.navigation.skeletons.ModuleSkeletonScreen
import com.example.ui.navigation.skeletons.PlusEmptyScreen
import com.example.ui.navigation.skeletons.ProjetsEmptyScreen
import com.example.ui.plus.PlusScreen
import com.example.ui.transfer.DataTransferScreen
import com.example.ui.transfer.DataTransferViewModel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AcheteurDZTheme


class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Configure Firebase App Check before any Firebase SDK access
    configureAppCheck(this, intent)
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      AcheteurDZTheme {
        AcheteurApp()
      }
    }
  }
}

@Composable
fun AcheteurApp() {
  val context = LocalContext.current
  val navController = rememberNavController()

  // Initialize Database and Repositories
  val database = remember { AppDatabase.getInstance(context) }
  val supplierRepository = remember { SupplierRepository(database) }
  val projectRepository = remember { ProjectRepository(database) }
  val productRepository = remember { ProductRepository(database) }
  val procurementRepository = remember { ProcurementRepository(database) }
  val auditRepository = remember { AuditRepository(database) }
  val settingsRepository = remember { SettingsRepository(database) }

  // Seed sample data on first launch
  LaunchedEffect(Unit) {
    DatabaseSeeder.seedIfEmpty(
      supplierRepository = supplierRepository,
      projectRepository = projectRepository,
      productRepository = productRepository,
      procurementRepository = procurementRepository,
      settingsRepository = settingsRepository
    )
  }

  // ViewModels
  val supplierViewModel: SupplierViewModel = viewModel {
    SupplierViewModel(supplierRepository)
  }
  val projectViewModel: ProjectViewModel = viewModel {
    ProjectViewModel(projectRepository)
  }
  val productViewModel: ProductViewModel = viewModel {
    ProductViewModel(productRepository, supplierRepository)
  }
  val procurementViewModel: ProcurementViewModel = viewModel {
    ProcurementViewModel(procurementRepository, supplierRepository, projectRepository, productRepository, auditRepository)
  }
  val receiptScannerViewModel: ReceiptScannerViewModel = viewModel {
    ReceiptScannerViewModel(
      procurementRepository = procurementRepository,
      supplierRepository = supplierRepository,
      projectRepository = projectRepository,
      auditRepository = auditRepository
    )
  }
  val dataTransferViewModel: DataTransferViewModel = remember {
    DataTransferViewModel(database)
  }

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val bottomNavItems = BottomNavDestination.items
  val showBottomBar = currentRoute in bottomNavItems.map { it.route }

  Scaffold(
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          windowInsets = WindowInsets.navigationBars,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          bottomNavItems.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val icon = if (isSelected) destination.selectedIcon else destination.unselectedIcon
            NavigationBarItem(
              icon = { Icon(icon, contentDescription = destination.label) },
              label = { Text(destination.label) },
              selected = isSelected,
              onClick = {
                if (currentRoute != destination.route) {
                  navController.navigate(destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              modifier = Modifier.testTag(destination.testTag)
            )
          }
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Dashboard.route,
      modifier = Modifier.padding(innerPadding)
    ) {
      // 1. Dashboard
      composable(Screen.Dashboard.route) {
        DashboardScreen(
          supplierViewModel = supplierViewModel,
          settingsRepository = settingsRepository,
          onNavigateToSuppliers = { navController.navigate(Screen.Suppliers.route) },
          onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
          onNavigateToProcurement = { navController.navigate(Screen.Procurement.route) },
          onNavigateToProducts = { navController.navigate(Screen.Products.route) },
          onNavigateToCreateSupplier = { navController.navigate(Screen.SupplierEdit.createRoute()) },
          onNavigateToAudit = { navController.navigate(Screen.AuditLog.route) },
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
          onNavigateToScanner = { navController.navigate(Screen.ReceiptScanner.route) },
          onNavigateToProfile = { navController.navigate(Screen.UserProfile.route) }
        )
      }

      // 2. Chantiers / Projets
      composable(Screen.Projects.route) {
        ProjectListScreen(
          viewModel = projectViewModel,
          onNavigateToDetail = { projectId ->
            navController.navigate(Screen.ProjectDetail.createRoute(projectId))
          },
          onNavigateToCreate = { navController.navigate(Screen.ProjectEdit.route) }
        )
      }

      composable(
        route = Screen.ProjectDetail.route,
        arguments = listOf(navArgument("projectId") { type = NavType.StringType })
      ) { backStackEntry ->
        val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
        ProjectDetailScreen(
          projectId = projectId,
          viewModel = projectViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      composable(Screen.ProjectEdit.route) {
        ProjectEditScreen(
          viewModel = projectViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 3. Achats (Procure-to-Pay Hub: DA, BC, Réceptions, Factures)
      composable(Screen.Procurement.route) {
        ProcurementHubScreen(
          viewModel = procurementViewModel,
          onNavigateToScanner = { navController.navigate(Screen.ReceiptScanner.route) }
        )
      }

      // 4. Fournisseurs
      composable(Screen.Suppliers.route) {
        SupplierListScreen(
          viewModel = supplierViewModel,
          onNavigateToDetail = { supplierId ->
            navController.navigate(Screen.SupplierDetail.createRoute(supplierId))
          },
          onNavigateToCreate = {
            navController.navigate(Screen.SupplierEdit.createRoute())
          }
        )
      }

      composable(
        route = Screen.SupplierDetail.route,
        arguments = listOf(navArgument("supplierId") { type = NavType.StringType })
      ) { backStackEntry ->
        val supplierId = backStackEntry.arguments?.getString("supplierId") ?: ""
        SupplierDetailScreen(
          supplierId = supplierId,
          viewModel = supplierViewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToEdit = { id ->
            navController.navigate(Screen.SupplierEdit.createRoute(id))
          }
        )
      }

      composable(
        route = Screen.SupplierEdit.route,
        arguments = listOf(
          navArgument("supplierId") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
          }
        )
      ) { backStackEntry ->
        val supplierId = backStackEntry.arguments?.getString("supplierId")
        SupplierEditScreen(
          supplierId = supplierId,
          viewModel = supplierViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 5. Catalogue Articles & Matériaux
      composable(Screen.Products.route) {
        ProductListScreen(
          viewModel = productViewModel,
          onNavigateToCreate = { navController.navigate(Screen.ProductEdit.route) }
        )
      }

      composable(Screen.ProductEdit.route) {
        ProductEditScreen(
          viewModel = productViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 6. Audit & Paramètres
      composable(Screen.AuditLog.route) {
        AuditLogScreen(
          auditRepository = auditRepository,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      composable(Screen.Settings.route) {
        SettingsScreen(
          settingsRepository = settingsRepository,
          supplierRepository = supplierRepository,
          onNavigateBack = { navController.popBackStack() },
          onNavigateToAudit = { navController.navigate(Screen.AuditLog.route) },
          onNavigateToProfile = { navController.navigate(Screen.UserProfile.route) }
        )
      }

      // 6b. Profil Acheteur & Connexion Gmail
      composable(Screen.UserProfile.route) {
        UserProfileScreen(
          settingsRepository = settingsRepository,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 7. Scanner Reçu & Facture IA (Gemini 3.8 Flash)
      composable(Screen.ReceiptScanner.route) {
        ReceiptScannerScreen(
          viewModel = receiptScannerViewModel,
          onNavigateBack = { navController.popBackStack() },
          onInvoiceCreated = {
            navController.navigate(Screen.Procurement.route) {
              popUpTo(Screen.Dashboard.route)
            }
          },
          onReceptionCreated = {
            navController.navigate(Screen.Procurement.route) {
              popUpTo(Screen.Dashboard.route)
            }
          }
        )
      }

      // 8. Plus (Menu hub pour Catalogue, Scanner, Audit, Paramètres et Squelettes)
      composable(Screen.Plus.route) {
        PlusScreen(
          onNavigateToProfile = { navController.navigate(Screen.UserProfile.route) },
          onNavigateToProducts = { navController.navigate(Screen.Products.route) },
          onNavigateToScanner = { navController.navigate(Screen.ReceiptScanner.route) },
          onNavigateToAudit = { navController.navigate(Screen.AuditLog.route) },
          onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
          onNavigateToTransfer = { navController.navigate(Screen.DataTransfer.route) },
          onNavigateToSkeleton = { moduleKey ->
            navController.navigate(Screen.ModuleSkeleton.createRoute(moduleKey))
          }
        )
      }

      // 8b. Transfert & Fusion Mobile
      composable(Screen.DataTransfer.route) {
        DataTransferScreen(
          viewModel = dataTransferViewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 9. Module Skeletons (Aperçu des écrans squelettes)
      composable(
        route = Screen.ModuleSkeleton.route,
        arguments = listOf(navArgument("moduleKey") { type = NavType.StringType })
      ) { backStackEntry ->
        val moduleKey = backStackEntry.arguments?.getString("moduleKey") ?: "dashboard"
        ModuleSkeletonScreen(
          moduleKey = moduleKey,
          onNavigateBack = { navController.popBackStack() },
          onAction = {
            when (moduleKey.lowercase()) {
              "achats", "procurement" -> navController.navigate(Screen.Procurement.route)
              "fournisseurs", "suppliers" -> navController.navigate(Screen.SupplierEdit.createRoute())
              "projets", "projects" -> navController.navigate(Screen.ProjectEdit.route)
              else -> navController.navigate(Screen.Dashboard.route)
            }
          }
        )
      }

      // 10. Initial Empty Screens for each of the 5 modules
      composable(Screen.DashboardEmpty.route) {
        DashboardEmptyScreen(
          onNavigateBack = { navController.popBackStack() },
          onActionClick = { navController.navigate(Screen.Dashboard.route) }
        )
      }

      composable(Screen.AchatsEmpty.route) {
        AchatsEmptyScreen(
          onNavigateBack = { navController.popBackStack() },
          onActionClick = { navController.navigate(Screen.Procurement.route) }
        )
      }

      composable(Screen.FournisseursEmpty.route) {
        FournisseursEmptyScreen(
          onNavigateBack = { navController.popBackStack() },
          onActionClick = { navController.navigate(Screen.SupplierEdit.createRoute()) }
        )
      }

      composable(Screen.ProjetsEmpty.route) {
        ProjetsEmptyScreen(
          onNavigateBack = { navController.popBackStack() },
          onActionClick = { navController.navigate(Screen.ProjectEdit.route) }
        )
      }

      composable(Screen.PlusEmpty.route) {
        PlusEmptyScreen(
          onNavigateBack = { navController.popBackStack() },
          onActionClick = { navController.navigate(Screen.Plus.route) }
        )
      }
    }
  }
}
