package com.example.ui.audit

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.core.audit.AuditSnapshotHelper
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.ExportButton
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.core.utils.ExportService
import com.example.data.local.entities.AuditLogEntity
import com.example.data.repository.AuditRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditLogScreen(
  auditRepository: AuditRepository,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val logs by auditRepository.getAllAuditLogs().collectAsStateWithLifecycle(initialValue = emptyList())

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Journal d'Audit Immuable",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Traçabilité complète (Append-only)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          ExportButton(
            onExport = {
              val headers = listOf("Horodatage", "Action", "Entité", "ID", "Auteur", "Description")
              val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH)
              val csv = ExportService.generateCsv(headers, logs) {
                listOf(
                  sdf.format(Date(it.timestamp)),
                  it.action,
                  it.entityType,
                  it.entityId,
                  it.who,
                  it.description
                )
              }
              ExportService.shareExport(context, "Journal_Audit_AcheteurDZ.csv", csv, "text/csv")
            },
            label = "Exporter"
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    if (logs.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Aucun événement dans le journal d'audit", style = MaterialTheme.typography.titleMedium)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(logs, key = { it.id }) { log ->
          AuditLogItem(log)
        }
      }
    }
  }
}

@Composable
private fun AuditLogItem(log: AuditLogEntity) {
  val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH)
  val dateFormatted = sdf.format(Date(log.timestamp))

  var isExpanded by remember { mutableStateOf(false) }
  val oldMap = remember(log.oldValueJson) { AuditSnapshotHelper.parseJsonToMap(log.oldValueJson) }
  val newMap = remember(log.newValueJson) { AuditSnapshotHelper.parseJsonToMap(log.newValueJson) }
  val hasSnapshots = oldMap.isNotEmpty() || newMap.isNotEmpty()

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          val (chipType, chipLabel) = when (log.action) {
            "CREATE" -> StatusType.APPROVED to "CRÉATION"
            "UPDATE" -> StatusType.PENDING to "MODIFICATION"
            "STATUS_CHANGE" -> StatusType.WARNING to "STATUT"
            "ARCHIVE" -> StatusType.SUSPENDED to "ARCHIVAGE"
            "RESTORE" -> StatusType.APPROVED to "RESTAURATION"
            else -> StatusType.DRAFT to log.action
          }
          StatusChip(chipLabel, chipType)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            log.entityType,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          dateFormatted,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(log.description, style = MaterialTheme.typography.bodyMedium)

      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Auteur : ${log.who}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(log.device, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
      }

      if (hasSnapshots) {
        Spacer(modifier = Modifier.height(6.dp))
        TextButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.align(Alignment.End)
        ) {
          Text(
            if (isExpanded) "Masquer comparaison ▲" else "Comparer Origine vs Modifié ▼",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        if (isExpanded) {
          HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (oldMap.isNotEmpty()) {
              Surface(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(
                    "ORIGINE (Avant modification) :",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                  )
                  oldMap.forEach { (k, v) ->
                    Text("$k = $v", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }

            if (newMap.isNotEmpty()) {
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(
                    if (oldMap.isEmpty()) "VALEUR INITIALE :" else "MODIFIÉ (Nouvelle valeur) :",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                  newMap.forEach { (k, v) ->
                    Text("$k = $v", style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
