package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.repository.SettingsRepository
import com.example.ui.suppliers.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  supplierViewModel: SupplierViewModel,
  settingsRepository: SettingsRepository,
  onNavigateToSuppliers: () -> Unit,
  onNavigateToProjects: () -> Unit,
  onNavigateToProcurement: () -> Unit,
  onNavigateToProducts: () -> Unit,
  onNavigateToCreateSupplier: () -> Unit,
  onNavigateToAudit: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToScanner: () -> Unit = {},
  onNavigateToProfile: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val uiState by supplierViewModel.listUiState.collectAsStateWithLifecycle()
  val settings by settingsRepository.getSettings().collectAsStateWithLifecycle(
    initialValue = com.example.data.local.entities.SettingsEntity()
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Acheteur DZ",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Procure-to-Pay Algérie",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          IconButton(
            onClick = onNavigateToScanner,
            modifier = Modifier.testTag("dashboard_scanner_button")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scanner Reçu IA", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(
            onClick = onNavigateToProfile,
            modifier = Modifier.testTag("dashboard_profile_button")
          ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Profil Acheteur Gmail", tint = MaterialTheme.colorScheme.primary)
          }
          IconButton(onClick = onNavigateToSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Paramètres")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Welcome & Operational Mode Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                "Tableau de Bord Acheteur",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                settings.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            StatusChip(
              text = if (settings.isWarehouseModuleEnabled) "Magasin ON" else "Chantier Direct",
              type = if (settings.isWarehouseModuleEnabled) StatusType.WARNING else StatusType.APPROVED
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (settings.isWarehouseModuleEnabled) {
              "Gestion de stock centralisée et réceptions chantier actives."
            } else {
              "Principe n°1 actif : Tous les achats sont livrés directement sur chantiers cibles (Pas de magasin à ce stade)."
            },
            style = MaterialTheme.typography.bodySmall
          )
        }
      }

      // Scanner IA Hero Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onNavigateToScanner),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                "Scanner Reçu / Facture IA",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                "Numérisation & extraction automatique (Gemini)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
              )
            }
          }
          FilledTonalButton(
            onClick = onNavigateToScanner,
            modifier = Modifier.padding(start = 8.dp)
          ) {
            Text("Scanner")
          }
        }
      }

      // 2. Metrics in DZD
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Indicateurs d'Achats & Dépenses (DZD)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Achats engagés (Mois) :", style = MaterialTheme.typography.bodyMedium)
            AmountText(4850000.0)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Réceptions chantier validées :", style = MaterialTheme.typography.bodyMedium)
            AmountText(4200000.0)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Factures réglées :", style = MaterialTheme.typography.bodyMedium)
            AmountText(3100000.0)
          }
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Solde Fournisseurs à Régler :", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            AmountText(850000.0, color = MaterialTheme.colorScheme.error)
          }
        }
      }

      // 3. Fournisseurs Quick Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onNavigateToSuppliers),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                "Référentiel Fournisseurs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            Text("Voir tout →", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            SummaryBadge("Total", uiState.totalCount.toString(), MaterialTheme.colorScheme.primary)
            SummaryBadge("Agréés", uiState.approvedCount.toString(), MaterialTheme.colorScheme.secondary)
            SummaryBadge("En cours", uiState.pendingCount.toString(), MaterialTheme.colorScheme.tertiary)
            SummaryBadge("Docs", "${uiState.expiringDocsCount} alertes", MaterialTheme.colorScheme.error)
          }
        }
      }

      // 4. Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilledTonalButton(
          onClick = onNavigateToCreateSupplier,
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Ajouter Fournisseur")
        }

        FilledTonalButton(
          onClick = onNavigateToAudit,
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Journal Audit")
        }
      }

      // 5. Procure-to-Pay Modules Roadmap
      Text(
        "Cycle Procure-to-Pay (Architecture & Modules)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      ModuleProgressItem(
        icon = Icons.Default.Business,
        title = "1. Fournisseurs & Sous-traitants",
        subtitle = "Agrément, fiscalité DZ (NIF/RC/RIB 20 ch.), scoring, suivi",
        status = "Prêt & Opérationnel",
        statusType = StatusType.APPROVED,
        onClick = onNavigateToSuppliers
      )

      ModuleProgressItem(
        icon = Icons.Default.Description,
        title = "2. Demandes d'Achat (DA)",
        subtitle = "Workflow d'approbation et affectation au chantier cible",
        status = "Opérationnel",
        statusType = StatusType.APPROVED,
        onClick = onNavigateToProcurement
      )

      ModuleProgressItem(
        icon = Icons.Default.ShoppingCart,
        title = "3. Bons de Commande (BC)",
        subtitle = "Liaison Fournisseur - Chantier - Articles avec TVA 19%/9%",
        status = "Opérationnel",
        statusType = StatusType.APPROVED,
        onClick = onNavigateToProcurement
      )

      ModuleProgressItem(
        icon = Icons.Default.LocationCity,
        title = "4. Réceptions Chantier (BL/BR)",
        subtitle = "Livraisons directes sur site sans passage par stock",
        status = "Opérationnel",
        statusType = StatusType.APPROVED,
        onClick = onNavigateToProcurement
      )

      ModuleProgressItem(
        icon = Icons.Default.Receipt,
        title = "5. Factures & Rapprochement 3 voies",
        subtitle = "Contrôle BC vs Réception Chantier vs Facture Fournisseur",
        status = "Opérationnel",
        statusType = StatusType.APPROVED,
        onClick = onNavigateToProcurement
      )
    }
  }
}

@Composable
private fun SummaryBadge(title: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
    Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}

@Composable
private fun ModuleProgressItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  status: String,
  statusType: StatusType,
  onClick: () -> Unit
) {
  OutlinedCard(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
          Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      StatusChip(status, statusType)
    }
  }
}
