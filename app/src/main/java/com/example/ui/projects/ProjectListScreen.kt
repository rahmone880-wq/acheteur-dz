package com.example.ui.projects

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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.local.entities.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
  viewModel: ProjectViewModel,
  onNavigateToDetail: (String) -> Unit,
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
              "Chantiers & Projets Cibles",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Achats dirigés directement sur site",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          ExportButton(
            onExport = { format -> viewModel.exportProjects(context, format) },
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
        text = { Text("Nouveau Projet / Chantier") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.testTag("add_project_fab")
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Global Budget Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Budget Global des Projets :", style = MaterialTheme.typography.bodyMedium)
            AmountText(uiState.totalBudgetGlobalDzd)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Engagé / Commandé :", style = MaterialTheme.typography.bodyMedium)
            AmountText(uiState.totalCommittedGlobalDzd, color = MaterialTheme.colorScheme.secondary)
          }
        }
      }

      // 2. Search Field
      OutlinedTextField(
        value = uiState.searchQuery,
        onValueChange = viewModel::onSearchQueryChange,
        placeholder = { Text("Rechercher par nom de chantier, code, client...") },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      )

      // 3. Projects List
      if (uiState.filteredProjects.isEmpty()) {
        EmptyState(
          icon = Icons.Default.Architecture,
          title = "Aucun chantier enregistré",
          description = "Ajoutez un projet pour y affecter les commandes et réceptions d'achats.",
          actionLabel = "Créer un chantier",
          onActionClick = onNavigateToCreate
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(uiState.filteredProjects, key = { it.id }) { project ->
            ProjectListItem(project = project, onClick = { onNavigateToDetail(project.id) })
          }
        }
      }
    }
  }
}

@Composable
private fun ProjectListItem(
  project: ProjectEntity,
  onClick: () -> Unit
) {
  val consumptionPercent = if (project.totalBudgetDzd > 0) {
    (project.committedBudgetDzd / project.totalBudgetDzd).coerceIn(0.0, 1.0).toFloat()
  } else 0f

  EntityCard(onClick = onClick) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = project.name,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "${project.code} • Client: ${project.client}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      StatusChip(project.status, StatusType.APPROVED)
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${project.locationCommune} (Wilaya ${project.locationWilaya}) • Responsable: ${project.manager}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Budget progress
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("Engagé :", style = MaterialTheme.typography.labelSmall)
      AmountText(project.committedBudgetDzd, style = MaterialTheme.typography.labelSmall)
      Text("sur Budget :", style = MaterialTheme.typography.labelSmall)
      AmountText(project.totalBudgetDzd, style = MaterialTheme.typography.labelSmall)
      Text("${(consumptionPercent * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
    }

    Spacer(modifier = Modifier.height(4.dp))
    LinearProgressIndicator(
      progress = { consumptionPercent },
      modifier = Modifier.fillMaxWidth().height(6.dp),
      color = if (consumptionPercent > 0.85f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
      trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
  }
}
