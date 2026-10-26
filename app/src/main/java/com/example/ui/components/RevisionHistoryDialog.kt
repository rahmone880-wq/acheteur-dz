package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.audit.AuditSnapshotHelper
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.local.entities.AuditLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RevisionHistoryDialog(
  title: String,
  logs: List<AuditLogEntity>,
  onDismiss: () -> Unit
) {
  val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Historique & Traçabilité",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Fermer")
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Registre complet des modifications : comparaison entre l'origine et les valeurs modifiées.",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        if (logs.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "Aucune modification enregistrée pour cet élément.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 420.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(logs, key = { it.id }) { log ->
              RevisionCard(log = log, sdf = sdf)
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer")
      }
    }
  )
}

@Composable
private fun RevisionCard(
  log: AuditLogEntity,
  sdf: SimpleDateFormat
) {
  var isExpanded by remember { mutableStateOf(false) }
  val dateStr = remember(log.timestamp) { sdf.format(Date(log.timestamp)) }

  val oldMap = remember(log.oldValueJson) { AuditSnapshotHelper.parseJsonToMap(log.oldValueJson) }
  val newMap = remember(log.newValueJson) { AuditSnapshotHelper.parseJsonToMap(log.newValueJson) }
  val hasSnapshots = oldMap.isNotEmpty() || newMap.isNotEmpty()

  OutlinedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.outlinedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val (chipType, chipLabel) = when (log.action) {
          "CREATE" -> StatusType.APPROVED to "ORIGINE (CRÉATION)"
          "UPDATE" -> StatusType.PENDING to "MODIFIÉ"
          "STATUS_CHANGE" -> StatusType.WARNING to "STATUT"
          else -> StatusType.DRAFT to log.action
        }
        StatusChip(chipLabel, chipType)

        Text(
          text = dateStr,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = log.description,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Auteur : ${log.who}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = log.device,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }

      if (hasSnapshots) {
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
          onClick = { isExpanded = !isExpanded },
          modifier = Modifier.align(Alignment.End)
        ) {
          Text(
            if (isExpanded) "Masquer les détails Origine / Modifié ▲" else "Voir Origine vs Modifié ▼",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        if (isExpanded) {
          HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = DividerDefaults.color.copy(alpha = 0.5f)
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 1. Origine (Old Value)
            if (oldMap.isNotEmpty()) {
              SnapshotSideBox(
                badgeTitle = "ORIGINE (Avant modification)",
                badgeColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
                dataMap = oldMap
              )
            }

            // 2. Modifié (New Value)
            if (newMap.isNotEmpty()) {
              SnapshotSideBox(
                badgeTitle = if (oldMap.isEmpty()) "VALEUR INITIALE ENREGISTRÉE" else "MODIFIÉ (Nouvelle valeur)",
                badgeColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                dataMap = newMap
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SnapshotSideBox(
  badgeTitle: String,
  badgeColor: Color,
  containerColor: Color,
  dataMap: Map<String, String>
) {
  Surface(
    color = containerColor,
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = badgeTitle,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = badgeColor
      )
      Spacer(modifier = Modifier.height(6.dp))

      dataMap.forEach { (key, value) ->
        val friendlyKey = when (key) {
          "invoiceNumber" -> "N° Facture"
          "invoiceDate" -> "Date Facture"
          "dueDate" -> "Échéance"
          "totalHtDzd" -> "Total HT (DA)"
          "totalTvaDzd" -> "TVA (DA)"
          "totalTtcDzd" -> "Total TTC (DA)"
          "paidAmountDzd" -> "Montant Réglé (DA)"
          "paymentStatus" -> "Statut Paiement"
          "matchingStatus" -> "Rapprochement"
          "supplierName" -> "Fournisseur"
          "projectName" -> "Chantier / Projet"
          "bcNumber" -> "N° Bon Commande"
          "daNumber" -> "N° Demande Achat"
          "paymentTerms" -> "Conditions Règlement"
          "status" -> "Statut"
          "totalEstimatedDzd" -> "Montant Estimé (DA)"
          "requesterName" -> "Demandeur"
          "priority" -> "Priorité"
          "blNumber" -> "N° Bon Livraison (BL)"
          "brNumber" -> "N° Réception (BR)"
          "notes" -> "Remarques"
          "raisonSociale" -> "Raison Sociale"
          "phone" -> "Téléphone"
          "email" -> "Email"
          "adresse" -> "Adresse"
          "version" -> "Version"
          else -> key
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "$friendlyKey :",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}
