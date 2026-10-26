package com.example.ui.procurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.AppDatePicker
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierInvoiceEntity

// ---------------- DIALOG 1: MODIFIER FACTURE ----------------

@Composable
fun EditInvoiceDialog(
  invoice: SupplierInvoiceEntity,
  onDismiss: () -> Unit,
  onConfirm: (SupplierInvoiceEntity) -> Unit
) {
  var invoiceNumber by remember { mutableStateOf(invoice.invoiceNumber) }
  var invoiceDate by remember { mutableStateOf(invoice.invoiceDate) }
  var dueDate by remember { mutableStateOf(invoice.dueDate) }
  var totalHtStr by remember { mutableStateOf(invoice.totalHtDzd.toString()) }
  var totalTvaStr by remember { mutableStateOf(invoice.totalTvaDzd.toString()) }
  var totalTtcStr by remember { mutableStateOf(invoice.totalTtcDzd.toString()) }
  var paidAmountStr by remember { mutableStateOf(invoice.paidAmountDzd.toString()) }
  var paymentStatus by remember { mutableStateOf(invoice.paymentStatus) }
  var matchingStatus by remember { mutableStateOf(invoice.matchingStatus) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Modifier Facture : ${invoice.invoiceNumber}") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AppTextField(
          value = invoiceNumber,
          onValueChange = { invoiceNumber = it },
          label = "Numéro de Facture *",
          modifier = Modifier.testTag("edit_invoice_number")
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            AppDatePicker(
              value = invoiceDate,
              onDateSelected = { invoiceDate = it },
              label = "Date Facture"
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            AppDatePicker(
              value = dueDate,
              onDateSelected = { dueDate = it },
              label = "Date d'Échéance"
            )
          }
        }

        AppTextField(
          value = totalHtStr,
          onValueChange = {
            totalHtStr = it
            val ht = it.toDoubleOrNull() ?: 0.0
            val tva = ht * 0.19
            totalTvaStr = String.format(java.util.Locale.US, "%.2f", tva)
            totalTtcStr = String.format(java.util.Locale.US, "%.2f", ht + tva)
          },
          label = "Montant HT (DZD)",
          modifier = Modifier.testTag("edit_invoice_ht")
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            AppTextField(
              value = totalTvaStr,
              onValueChange = { totalTvaStr = it },
              label = "TVA 19% (DZD)"
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            AppTextField(
              value = totalTtcStr,
              onValueChange = { totalTtcStr = it },
              label = "Total TTC (DZD)",
              modifier = Modifier.testTag("edit_invoice_ttc")
            )
          }
        }

        AppTextField(
          value = paidAmountStr,
          onValueChange = {
            paidAmountStr = it
            val paid = it.toDoubleOrNull() ?: 0.0
            val ttc = totalTtcStr.toDoubleOrNull() ?: 0.0
            paymentStatus = when {
              paid >= ttc && ttc > 0 -> "PAYEE"
              paid > 0 -> "PARTIELLE"
              else -> "NON_PAYEE"
            }
          },
          label = "Montant Réglé (DZD)",
          modifier = Modifier.testTag("edit_invoice_paid")
        )

        AppDropdown(
          label = "Statut de Paiement",
          options = listOf("NON_PAYEE", "PARTIELLE", "PAYEE"),
          selectedOption = paymentStatus,
          onOptionSelected = { paymentStatus = it }
        )

        AppDropdown(
          label = "Rapprochement 3 Voies",
          options = listOf("CONFORME", "ECART_PRIX", "ECART_QTE"),
          selectedOption = matchingStatus,
          onOptionSelected = { matchingStatus = it }
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updated = invoice.copy(
            invoiceNumber = invoiceNumber.trim().ifBlank { invoice.invoiceNumber },
            invoiceDate = invoiceDate,
            dueDate = dueDate,
            totalHtDzd = totalHtStr.toDoubleOrNull() ?: invoice.totalHtDzd,
            totalTvaDzd = totalTvaStr.toDoubleOrNull() ?: invoice.totalTvaDzd,
            totalTtcDzd = totalTtcStr.toDoubleOrNull() ?: invoice.totalTtcDzd,
            paidAmountDzd = paidAmountStr.toDoubleOrNull() ?: invoice.paidAmountDzd,
            paymentStatus = paymentStatus,
            matchingStatus = matchingStatus
          )
          onConfirm(updated)
        },
        modifier = Modifier.testTag("edit_invoice_save_button")
      ) {
        Text("Enregistrer les modifications")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Annuler")
      }
    }
  )
}

// ---------------- DIALOG 2: MODIFIER BON DE COMMANDE (BC) ----------------

@Composable
fun EditBcDialog(
  bc: PurchaseOrderEntity,
  onDismiss: () -> Unit,
  onConfirm: (PurchaseOrderEntity) -> Unit
) {
  var status by remember { mutableStateOf(bc.status) }
  var paymentTerms by remember { mutableStateOf(bc.paymentTerms) }
  var expectedDeliveryDate by remember { mutableStateOf(bc.expectedDeliveryDate) }
  var deliverySiteAddress by remember { mutableStateOf(bc.deliverySiteAddress) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Modifier Bon de Commande : ${bc.bcNumber}") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          "Fournisseur : ${bc.supplierName} • Chantier : ${bc.projectName}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )

        AppDropdown(
          label = "Statut de la commande",
          options = listOf("BROUILLON", "ENVOYE", "CONFIRME", "PARTIELLEMENT_RECU", "RECU", "CLOTURE", "ANNULE"),
          selectedOption = status,
          onOptionSelected = { status = it }
        )

        AppDropdown(
          label = "Conditions de paiement",
          options = listOf("Comptant à la livraison", "30 jours", "45 jours", "60 jours", "Chèque à terme (traite)"),
          selectedOption = paymentTerms,
          onOptionSelected = { paymentTerms = it }
        )

        AppDatePicker(
          value = expectedDeliveryDate,
          onDateSelected = { expectedDeliveryDate = it },
          label = "Date de livraison prévue"
        )

        AppTextField(
          value = deliverySiteAddress,
          onValueChange = { deliverySiteAddress = it },
          label = "Adresse de livraison sur chantier"
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updated = bc.copy(
            status = status,
            paymentTerms = paymentTerms,
            expectedDeliveryDate = expectedDeliveryDate,
            deliverySiteAddress = deliverySiteAddress
          )
          onConfirm(updated)
        }
      ) {
        Text("Enregistrer les modifications")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Annuler")
      }
    }
  )
}

// ---------------- DIALOG 3: MODIFIER DEMANDE D'ACHAT (DA) ----------------

@Composable
fun EditDaDialog(
  da: PurchaseRequestEntity,
  onDismiss: () -> Unit,
  onConfirm: (PurchaseRequestEntity) -> Unit
) {
  var requesterName by remember { mutableStateOf(da.requesterName) }
  var department by remember { mutableStateOf(da.department) }
  var priority by remember { mutableStateOf(da.priority) }
  var status by remember { mutableStateOf(da.status) }
  var totalEstimatedStr by remember { mutableStateOf(da.totalEstimatedDzd.toString()) }
  var justification by remember { mutableStateOf(da.justification) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Modifier Demande d'Achat : ${da.daNumber}") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AppTextField(
          value = requesterName,
          onValueChange = { requesterName = it },
          label = "Nom du demandeur *"
        )

        AppTextField(
          value = department,
          onValueChange = { department = it },
          label = "Département / Corps d'état"
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Column(modifier = Modifier.weight(1f)) {
            AppDropdown(
              label = "Priorité",
              options = listOf("BASSE", "NORMALE", "URGENTE", "BLOQUANTE"),
              selectedOption = priority,
              onOptionSelected = { priority = it }
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            AppDropdown(
              label = "Statut",
              options = listOf("BROUILLON", "EN_ATTENTE_VALIDATION", "APPROUVEE", "COMMANDEE", "REJETEE"),
              selectedOption = status,
              onOptionSelected = { status = it }
            )
          }
        }

        AppTextField(
          value = totalEstimatedStr,
          onValueChange = { totalEstimatedStr = it },
          label = "Montant estimé (DZD)"
        )

        AppTextField(
          value = justification,
          onValueChange = { justification = it },
          label = "Justification du besoin",
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updated = da.copy(
            requesterName = requesterName.trim().ifBlank { da.requesterName },
            department = department,
            priority = priority,
            status = status,
            totalEstimatedDzd = totalEstimatedStr.toDoubleOrNull() ?: da.totalEstimatedDzd,
            justification = justification
          )
          onConfirm(updated)
        }
      ) {
        Text("Enregistrer les modifications")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Annuler")
      }
    }
  )
}

// ---------------- DIALOG 4: MODIFIER RÉCEPTION (BR/BL) ----------------

@Composable
fun EditReceptionDialog(
  reception: ReceptionEntity,
  onDismiss: () -> Unit,
  onConfirm: (ReceptionEntity) -> Unit
) {
  var blNumber by remember { mutableStateOf(reception.blNumber) }
  var status by remember { mutableStateOf(reception.status) }
  var notes by remember { mutableStateOf(reception.notes) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Modifier Réception : ${reception.brNumber}") },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          "Fournisseur : ${reception.supplierName} • Chantier : ${reception.destinationName}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary
        )

        AppTextField(
          value = blNumber,
          onValueChange = { blNumber = it },
          label = "Numéro Bon de Livraison (BL) *"
        )

        AppDropdown(
          label = "Conformité de réception",
          options = listOf("CONFORME", "AVEC_RESERVES", "REJETE"),
          selectedOption = status,
          onOptionSelected = { status = it }
        )

        AppTextField(
          value = notes,
          onValueChange = { notes = it },
          label = "Notes / Réserves constatées",
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val updated = reception.copy(
            blNumber = blNumber.trim().ifBlank { reception.blNumber },
            status = status,
            notes = notes
          )
          onConfirm(updated)
        }
      ) {
        Text("Enregistrer les modifications")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Annuler")
      }
    }
  )
}
