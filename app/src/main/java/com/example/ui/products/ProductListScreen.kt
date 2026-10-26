package com.example.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.EmptyState
import com.example.core.designsystem.components.EntityCard
import com.example.core.designsystem.components.ExportButton
import com.example.core.designsystem.components.FilterBar
import com.example.core.utils.AlgerianData
import com.example.data.local.entities.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
  viewModel: ProductViewModel,
  onNavigateToCreate: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Catalogue Articles & Matériaux",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Marchandises liées aux Fournisseurs",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          ExportButton(
            onExport = { format -> viewModel.exportProducts(context, format) },
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
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text("Nouvel Article") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_product_fab")
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      FilterBar(
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        placeholder = "Rechercher par désignation, réf, fournisseur...",
        filterChips = listOf("TOUTES") + AlgerianData.CATEGORIES.take(4),
        selectedFilter = uiState.categoryFilter,
        onFilterSelected = viewModel::onCategoryFilterChange,
        filterToString = { if (it == "TOUTES") "Toutes (${uiState.products.size})" else it }
      )

      if (uiState.filteredProducts.isEmpty()) {
        EmptyState(
          icon = Icons.Default.Category,
          title = "Aucun article dans le catalogue",
          description = "Ajoutez des matériaux, équipements ou fournitures reliés aux fournisseurs agréés.",
          actionLabel = "Ajouter un article",
          onActionClick = onNavigateToCreate
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(uiState.filteredProducts, key = { it.id }) { product ->
            ProductListItem(product = product)
          }
        }
      }
    }
  }
}

@Composable
private fun ProductListItem(product: ProductEntity) {
  EntityCard(onClick = {}) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          product.designationFr,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (product.designationAr.isNotBlank()) {
          Text(
            product.designationAr,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
        Text(
          "Réf: ${product.reference} • Unité: ${product.unit} • TVA: ${product.tvaRate.toInt()}%",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        AmountText(product.lastPriceDzd)
        Text("Dernier prix", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (product.defaultSupplierName.isNotBlank()) {
        SuggestionChip(
          onClick = {},
          label = { Text("Fournisseur: ${product.defaultSupplierName}", style = MaterialTheme.typography.labelSmall) }
        )
      }
      Text(
        product.category,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
