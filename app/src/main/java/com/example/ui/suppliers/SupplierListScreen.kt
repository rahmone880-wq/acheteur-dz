package com.example.ui.suppliers

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.EmptyState
import com.example.core.designsystem.components.EntityCard
import com.example.core.designsystem.components.ExportButton
import com.example.core.designsystem.components.FilterBar
import com.example.core.designsystem.components.RatingScoreBar
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.local.entities.SupplierEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierListScreen(
  viewModel: SupplierViewModel,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToCreate: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.listUiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Fournisseurs & Sous-traitants",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Référentiel & Agrément Algérie",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          ExportButton(
            onExport = { format -> viewModel.exportSuppliers(context, format) },
            label = "Exporter"
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onNavigateToCreate,
        icon = { Icon(Icons.Default.Add, contentDescription = "Nouveau") },
        text = { Text("Nouveau Fournisseur") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_supplier_fab")
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Quick Stats Header
      StatsHeaderRow(
        total = uiState.totalCount,
        approved = uiState.approvedCount,
        pending = uiState.pendingCount,
        expiring = uiState.expiringDocsCount
      )

      // 2. Search & Filter Bar
      FilterBar(
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        placeholder = "Rechercher par nom, NIF, RC, wilaya...",
        filterChips = listOf("TOUS", "AGREE", "EN_COURS", "SUSPENDU", "BLACKLISTE"),
        selectedFilter = uiState.statusFilter,
        onFilterSelected = viewModel::onStatusFilterChange,
        filterToString = { filter ->
          when (filter) {
            "TOUS" -> "Tous (${uiState.totalCount})"
            "AGREE" -> "Agréés (${uiState.approvedCount})"
            "EN_COURS" -> "En cours (${uiState.pendingCount})"
            "SUSPENDU" -> "Suspendus"
            "BLACKLISTE" -> "Blacklistés"
            else -> filter
          }
        }
      )

      // 3. Suppliers List
      if (uiState.filteredSuppliers.isEmpty()) {
        EmptyState(
          icon = Icons.Default.Business,
          title = if (uiState.searchQuery.isBlank()) "Aucun fournisseur enregistré" else "Aucun résultat trouvé",
          description = if (uiState.searchQuery.isBlank())
            "Commencez par ajouter vos fournisseurs et sous-traitants pour le cycle d'achat."
          else
            "Essayez d'autres mots-clés ou modifiez vos filtres de recherche.",
          actionLabel = if (uiState.searchQuery.isBlank()) "Créer un fournisseur" else null,
          onActionClick = if (uiState.searchQuery.isBlank()) onNavigateToCreate else null
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(uiState.filteredSuppliers, key = { it.id }) { supplier ->
            SupplierListItem(
              supplier = supplier,
              onClick = { onNavigateToDetail(supplier.id) },
              onCall = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${supplier.phone}"))
                context.startActivity(intent)
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatsHeaderRow(
  total: Int,
  approved: Int,
  pending: Int,
  expiring: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    StatItemCard(
      title = "Total",
      count = total.toString(),
      icon = Icons.Default.Business,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "Agréés",
      count = approved.toString(),
      icon = Icons.Default.CheckCircle,
      tint = MaterialTheme.colorScheme.secondary,
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "En cours",
      count = pending.toString(),
      icon = Icons.Default.HourglassTop,
      tint = MaterialTheme.colorScheme.tertiary,
      modifier = Modifier.weight(1f)
    )
    StatItemCard(
      title = "Alertes docs",
      count = expiring.toString(),
      icon = Icons.Default.Warning,
      tint = MaterialTheme.colorScheme.error,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun StatItemCard(
  title: String,
  count: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ),
    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = count,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun SupplierListItem(
  supplier: SupplierEntity,
  onClick: () -> Unit,
  onCall: () -> Unit,
  modifier: Modifier = Modifier
) {
  EntityCard(
    onClick = onClick,
    modifier = modifier,
    testTag = "supplier_card_${supplier.id}"
  ) {
    // Top Row: Name, Forme Juridique, and StatusChip
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = supplier.raisonSociale,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${supplier.formeJuridique} • ${supplier.commune}, ${supplier.wilayaName} (${supplier.wilayaCode})",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      val statusType = when (supplier.status) {
        "AGREE" -> StatusType.APPROVED
        "EN_COURS" -> StatusType.PENDING
        "SUSPENDU" -> StatusType.SUSPENDED
        "BLACKLISTE" -> StatusType.BLACKLISTED
        else -> StatusType.DRAFT
      }
      val statusLabel = when (supplier.status) {
        "AGREE" -> "Agréé"
        "EN_COURS" -> "En cours"
        "SUSPENDU" -> "Suspendu"
        "BLACKLISTE" -> "Blacklisté"
        else -> supplier.status
      }

      StatusChip(text = statusLabel, type = statusType)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Activity description
    if (supplier.activite.isNotBlank()) {
      Text(
        text = supplier.activite,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
      )
      Spacer(modifier = Modifier.height(6.dp))
    }

    // Fiscal and Contact chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (supplier.nif.isNotBlank()) {
        SuggestionChip(
          onClick = onClick,
          label = { Text("NIF: ${supplier.nif}", style = MaterialTheme.typography.labelSmall) }
        )
      }
      if (supplier.rcNumber.isNotBlank()) {
        SuggestionChip(
          onClick = onClick,
          label = { Text("RC: ${supplier.rcNumber}", style = MaterialTheme.typography.labelSmall) }
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Bottom Row: Rating & Call Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        RatingScoreBar(rating = supplier.overallRating, starSize = 16.dp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${supplier.overallRating}/5",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      if (supplier.phone.isNotBlank()) {
        IconButton(
          onClick = onCall,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "Appeler",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}
