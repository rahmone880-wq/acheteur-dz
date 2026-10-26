package com.example.ui.procurement

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import com.example.ui.components.RevisionHistoryDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.AppDatePicker
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.core.designsystem.components.EmptyState
import com.example.core.designsystem.components.ExportButton
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.CameraAlt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurementHubScreen(
  viewModel: ProcurementViewModel,
  onNavigateToScanner: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val purchaseRequests by viewModel.purchaseRequests.collectAsStateWithLifecycle()
  val purchaseOrders by viewModel.purchaseOrders.collectAsStateWithLifecycle()
  val receptions by viewModel.receptions.collectAsStateWithLifecycle()
  val invoices by viewModel.invoices.collectAsStateWithLifecycle()
  val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
  val projects by viewModel.projects.collectAsStateWithLifecycle()
  val products by viewModel.products.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) }
  var showNewDaDialog by remember { mutableStateOf(false) }
  var showNewBcDialog by remember { mutableStateOf(false) }
  var initialDaForBc by remember { mutableStateOf<PurchaseRequestEntity?>(null) }
  var showNewReceptionDialog by remember { mutableStateOf(false) }
  var showNewInvoiceDialog by remember { mutableStateOf(false) }

  var editingInvoice by remember { mutableStateOf<SupplierInvoiceEntity?>(null) }
  var editingBc by remember { mutableStateOf<PurchaseOrderEntity?>(null) }
  var editingDa by remember { mutableStateOf<PurchaseRequestEntity?>(null) }
  var editingReception by remember { mutableStateOf<ReceptionEntity?>(null) }
  var historyTarget by remember { mutableStateOf<Pair<String, Pair<String, String>>?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Cycle Achats (Procure-to-Pay)",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "DA • Bons de Commande • Réceptions • Rapprochement",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        actions = {
          IconButton(
            onClick = onNavigateToScanner,
            modifier = Modifier.testTag("procurement_scanner_button")
          ) {
            Icon(
              Icons.Default.CameraAlt,
              contentDescription = "Scanner Reçu IA",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          ExportButton(
            onExport = { format -> viewModel.exportOrders(context, format) },
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
        onClick = {
          when (selectedTab) {
            0 -> showNewDaDialog = true
            1 -> showNewBcDialog = true
            2 -> showNewReceptionDialog = true
            3 -> showNewInvoiceDialog = true
          }
        },
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = {
          Text(
            when (selectedTab) {
              0 -> "Nouvelle DA"
              1 -> "Nouveau Bon de Commande"
              2 -> "Réceptionner sur Chantier"
              3 -> "Enregistrer Facture"
              else -> "Nouveau"
            }
          )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val tabs = listOf(
        "1. Demandes (DA) (${purchaseRequests.size})",
        "2. Commandes (BC) (${purchaseOrders.size})",
        "3. Réceptions (${receptions.size})",
        "4. Factures & Rapprochement (${invoices.size})"
      )

      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, style = MaterialTheme.typography.labelMedium) }
          )
        }
      }

      when (selectedTab) {
        0 -> DaListTab(
          requests = purchaseRequests,
          onNewDa = { showNewDaDialog = true },
          onGenerateBc = { da ->
            initialDaForBc = da
            showNewBcDialog = true
          },
          onEditDa = { editingDa = it },
          onViewHistory = { da ->
            historyTarget = Pair("Demande d'Achat : ${da.daNumber}", Pair("DA", da.id))
          }
        )
        1 -> BcListTab(
          orders = purchaseOrders,
          onNewBc = {
            initialDaForBc = null
            showNewBcDialog = true
          },
          onEditBc = { editingBc = it },
          onViewHistory = { bc ->
            historyTarget = Pair("Bon de Commande : ${bc.bcNumber}", Pair("BC", bc.id))
          },
          onSendEmail = { bc ->
            viewModel.emailPurchaseOrder(context, bc)
          }
        )
        2 -> ReceptionListTab(
          receptions = receptions,
          onNewReception = { showNewReceptionDialog = true },
          onEditReception = { editingReception = it },
          onViewHistory = { rec ->
            historyTarget = Pair("Réception Chantier : ${rec.brNumber}", Pair("RECEPTION", rec.id))
          }
        )
        3 -> InvoiceListTab(
          invoices = invoices,
          onNewInvoice = { showNewInvoiceDialog = true },
          onEditInvoice = { editingInvoice = it },
          onViewHistory = { inv ->
            historyTarget = Pair("Facture Fournisseur : ${inv.invoiceNumber}", Pair("INVOICE", inv.id))
          }
        )
      }
    }
  }

  // DIALOGS FOR CREATION
  if (showNewDaDialog) {
    NewDaDialog(
      projects = projects,
      onDismiss = { showNewDaDialog = false },
      onConfirm = { da ->
        viewModel.createPurchaseRequest(da) {
          Toast.makeText(context, "Demande d'achat ${da.daNumber} créée", Toast.LENGTH_SHORT).show()
          showNewDaDialog = false
        }
      }
    )
  }

  if (showNewBcDialog) {
    NewBcDialog(
      suppliers = suppliers,
      projects = projects,
      products = products,
      purchaseRequests = purchaseRequests,
      initialDa = initialDaForBc,
      onDismiss = {
        showNewBcDialog = false
        initialDaForBc = null
      },
      onConfirm = { bc, lines ->
        viewModel.createPurchaseOrder(bc, lines) {
          val linkMsg = if (bc.daNumber.isNotBlank()) " (Lié à la DA ${bc.daNumber})" else ""
          Toast.makeText(context, "Bon de commande ${bc.bcNumber} émis$linkMsg", Toast.LENGTH_SHORT).show()
          showNewBcDialog = false
          initialDaForBc = null
        }
      }
    )
  }

  if (showNewReceptionDialog) {
    NewReceptionDialog(
      purchaseOrders = purchaseOrders,
      onDismiss = { showNewReceptionDialog = false },
      onConfirm = { reception ->
        viewModel.recordReception(reception) {
          Toast.makeText(context, "Réception ${reception.brNumber} enregistrée directement sur chantier", Toast.LENGTH_SHORT).show()
          showNewReceptionDialog = false
        }
      }
    )
  }

  if (showNewInvoiceDialog) {
    NewInvoiceDialog(
      purchaseOrders = purchaseOrders,
      onDismiss = { showNewInvoiceDialog = false },
      onConfirm = { invoice ->
        viewModel.recordInvoice(invoice) {
          Toast.makeText(context, "Facture ${invoice.invoiceNumber} enregistrée avec rapprochement 3 voies", Toast.LENGTH_SHORT).show()
          showNewInvoiceDialog = false
        }
      }
    )
  }

  // DIALOGS FOR EDITING
  editingInvoice?.let { inv ->
    EditInvoiceDialog(
      invoice = inv,
      onDismiss = { editingInvoice = null },
      onConfirm = { updated ->
        viewModel.updateInvoice(updated) {
          Toast.makeText(context, "Facture ${updated.invoiceNumber} modifiée (Origine & modifications tracées)", Toast.LENGTH_SHORT).show()
          editingInvoice = null
        }
      }
    )
  }

  editingBc?.let { bc ->
    EditBcDialog(
      bc = bc,
      onDismiss = { editingBc = null },
      onConfirm = { updated ->
        viewModel.updatePurchaseOrder(updated) {
          Toast.makeText(context, "Bon de commande ${updated.bcNumber} modifié", Toast.LENGTH_SHORT).show()
          editingBc = null
        }
      }
    )
  }

  editingDa?.let { da ->
    EditDaDialog(
      da = da,
      onDismiss = { editingDa = null },
      onConfirm = { updated ->
        viewModel.updatePurchaseRequest(updated) {
          Toast.makeText(context, "Demande d'achat ${updated.daNumber} modifiée", Toast.LENGTH_SHORT).show()
          editingDa = null
        }
      }
    )
  }

  editingReception?.let { rec ->
    EditReceptionDialog(
      reception = rec,
      onDismiss = { editingReception = null },
      onConfirm = { updated ->
        viewModel.updateReception(updated) {
          Toast.makeText(context, "Réception ${updated.brNumber} modifiée", Toast.LENGTH_SHORT).show()
          editingReception = null
        }
      }
    )
  }

  // DIALOG FOR REVISION HISTORY (ORIGINE VS MODIFIÉ)
  historyTarget?.let { (title, entityPair) ->
    val (type, id) = entityPair
    val logs by viewModel.getEntityAuditLogs(type, id).collectAsStateWithLifecycle(emptyList())
    RevisionHistoryDialog(
      title = title,
      logs = logs,
      onDismiss = { historyTarget = null }
    )
  }
}

// ---------------- TAB 0: DEMANDES D'ACHAT (DA) ----------------
 
@Composable
private fun DaListTab(
  requests: List<PurchaseRequestEntity>,
  onNewDa: () -> Unit,
  onGenerateBc: (PurchaseRequestEntity) -> Unit,
  onEditDa: (PurchaseRequestEntity) -> Unit,
  onViewHistory: (PurchaseRequestEntity) -> Unit
) {
  if (requests.isEmpty()) {
    EmptyState(
      icon = Icons.Default.Description,
      title = "Aucune demande d'achat (DA)",
      description = "Initiez une demande d'achat pour formaliser l'expression de besoin sur vos chantiers ou services.",
      actionLabel = "Créer une DA",
      onActionClick = onNewDa,
      modifier = Modifier.padding(top = 40.dp)
    )
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(requests, key = { it.id }) { da ->
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
              Text(da.daNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              val statusType = when (da.status) {
                "COMMANDEE" -> StatusType.INFO
                "REJETEE" -> StatusType.ERROR
                "VALIDEE" -> StatusType.APPROVED
                else -> StatusType.PENDING
              }
              StatusChip(da.status, statusType)
            }
            Text("Chantier / Projet : ${da.projectName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text("Demandeur : ${da.requesterName} (${da.department})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (da.justification.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text("Motif : ${da.justification}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Priorité : ${da.priority}", style = MaterialTheme.typography.labelSmall, color = if (da.priority == "URGENTE") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
              AmountText(da.totalEstimatedDzd)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { onViewHistory(da) },
                modifier = Modifier.testTag("btn_history_da_${da.id}")
              ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Historique", style = MaterialTheme.typography.labelSmall)
              }
              OutlinedButton(
                onClick = { onEditDa(da) },
                modifier = Modifier.testTag("btn_edit_da_${da.id}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Modifier", style = MaterialTheme.typography.labelSmall)
              }
              FilledTonalButton(
                onClick = { onGenerateBc(da) },
                modifier = Modifier.weight(1f).testTag("btn_link_da_to_bc_${da.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.AddShoppingCart,
                  contentDescription = null,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (da.status == "COMMANDEE") "Autre BC" else "Émettre BC",
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }
          }
        }
      }
    }
  }
}

// ---------------- TAB 1: BONS DE COMMANDE (BC) ----------------

@Composable
private fun BcListTab(
  orders: List<PurchaseOrderEntity>,
  onNewBc: () -> Unit,
  onEditBc: (PurchaseOrderEntity) -> Unit,
  onViewHistory: (PurchaseOrderEntity) -> Unit,
  onSendEmail: (PurchaseOrderEntity) -> Unit
) {
  if (orders.isEmpty()) {
    EmptyState(
      icon = Icons.Default.ShoppingCart,
      title = "Aucun bon de commande (BC)",
      description = "Créez un bon de commande officiel à adresser à votre fournisseur agréé avec calcul TVA et timbre fiscal.",
      actionLabel = "Créer un Bon de Commande",
      onActionClick = onNewBc,
      modifier = Modifier.padding(top = 40.dp)
    )
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(orders, key = { it.id }) { bc ->
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
              Text(bc.bcNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              StatusChip(bc.status, StatusType.APPROVED)
            }

            // Linked DA identification badge
            if (bc.daNumber.isNotBlank() || !bc.purchaseRequestId.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("bc_linked_da_badge_${bc.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Rattaché à la DA : ${bc.daNumber.ifBlank { "DA liée" }}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
            } else {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Achat direct (sans DA préalable)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Fournisseur : ${bc.supplierName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text("Chantier cible : ${bc.projectName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text("Date : ${bc.orderDate} • Conditions : ${bc.paymentTerms}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total TTC (avec TVA & timbre) :", style = MaterialTheme.typography.bodyMedium)
              AmountText(bc.totalTtcDzd)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { onViewHistory(bc) },
                modifier = Modifier.weight(1f).testTag("btn_history_bc_${bc.id}")
              ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Historique", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { onEditBc(bc) },
                modifier = Modifier.weight(1f).testTag("btn_edit_bc_${bc.id}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Modifier", style = MaterialTheme.typography.labelSmall)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            FilledTonalButton(
              onClick = { onSendEmail(bc) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_email_bc_${bc.id}"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Transmettre le BC par Gmail", style = MaterialTheme.typography.labelMedium)
            }
          }
        }
      }
    }
  }
}

// ---------------- TAB 2: RÉCEPTIONS (BL/BR) ----------------

@Composable
private fun ReceptionListTab(
  receptions: List<ReceptionEntity>,
  onNewReception: () -> Unit,
  onEditReception: (ReceptionEntity) -> Unit,
  onViewHistory: (ReceptionEntity) -> Unit
) {
  if (receptions.isEmpty()) {
    EmptyState(
      icon = Icons.Default.LocationCity,
      title = "Aucune réception sur chantier",
      description = "Enregistrez la réception des matériaux sur site avec le numéro de Bon de Livraison (BL) fournisseur.",
      actionLabel = "Réceptionner sur Chantier",
      onActionClick = onNewReception,
      modifier = Modifier.padding(top = 40.dp)
    )
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(receptions, key = { it.id }) { rec ->
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
              Text(rec.brNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              StatusChip("Livré Chantier", StatusType.APPROVED)
            }
            Text("Chantier de destination : ${rec.destinationName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text("Fournisseur : ${rec.supplierName} • BL N° : ${rec.blNumber}", style = MaterialTheme.typography.bodySmall)
            Text("Date réception : ${rec.receptionDate} • Réceptionnaire : ${rec.receivedBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (rec.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text("Notes : ${rec.notes}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { onViewHistory(rec) },
                modifier = Modifier.weight(1f).testTag("btn_history_rec_${rec.id}")
              ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Historique", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { onEditReception(rec) },
                modifier = Modifier.weight(1f).testTag("btn_edit_rec_${rec.id}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Modifier", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }
      }
    }
  }
}

// ---------------- TAB 3: FACTURES & RAPPROCHEMENT ----------------

@Composable
private fun InvoiceListTab(
  invoices: List<SupplierInvoiceEntity>,
  onNewInvoice: () -> Unit,
  onEditInvoice: (SupplierInvoiceEntity) -> Unit,
  onViewHistory: (SupplierInvoiceEntity) -> Unit
) {
  if (invoices.isEmpty()) {
    EmptyState(
      icon = Icons.Default.Receipt,
      title = "Aucune facture fournisseur",
      description = "Enregistrez les factures reçues pour valider le rapprochement 3-voies (DA - BC - Réception) avant paiement.",
      actionLabel = "Enregistrer une Facture",
      onActionClick = onNewInvoice,
      modifier = Modifier.padding(top = 40.dp)
    )
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(invoices, key = { it.id }) { inv ->
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
              Text(inv.invoiceNumber, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              StatusChip(inv.paymentStatus, if (inv.paymentStatus == "PAYEE") StatusType.APPROVED else StatusType.WARNING)
            }
            Text("Fournisseur : ${inv.supplierName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text("Projet / Chantier imputé : ${inv.projectName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text("Date : ${inv.invoiceDate} • Échéance : ${inv.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Rapprochement 3 voies :", style = MaterialTheme.typography.labelSmall)
              Text(inv.matchingStatus, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Facturé TTC :", style = MaterialTheme.typography.bodyMedium)
              AmountText(inv.totalTtcDzd)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = { onViewHistory(inv) },
                modifier = Modifier.weight(1f).testTag("btn_history_inv_${inv.id}")
              ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Historique", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { onEditInvoice(inv) },
                modifier = Modifier.weight(1f).testTag("btn_edit_inv_${inv.id}")
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Modifier", style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        }
      }
    }
  }
}

// ---------------- DIALOG: NOUVELLE DA ----------------

@Composable
private fun NewDaDialog(
  projects: List<ProjectEntity>,
  onDismiss: () -> Unit,
  onConfirm: (PurchaseRequestEntity) -> Unit
) {
  var requesterName by remember { mutableStateOf("") }
  var department by remember { mutableStateOf("Génie Civil / Chantier") }
  var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
  var justification by remember { mutableStateOf("") }
  var estimatedAmountStr by remember { mutableStateOf("") }
  var priority by remember { mutableStateOf("NORMALE") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Nouvelle Demande d'Achat (DA)") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppTextField(
          value = requesterName,
          onValueChange = { requesterName = it },
          label = "Nom du demandeur *",
          placeholder = "Ex : Chef de chantier / Conducteur de travaux"
        )

        AppDropdown(
          label = "Chantier / Projet cible *",
          options = projects,
          selectedOption = selectedProject,
          onOptionSelected = { selectedProject = it },
          optionToString = { "${it.code} - ${it.name}" }
        )

        AppTextField(
          value = justification,
          onValueChange = { justification = it },
          label = "Justification du besoin",
          placeholder = "Ex : Approvisionnement ferraillage niveau R+2"
        )

        AppTextField(
          value = estimatedAmountStr,
          onValueChange = { estimatedAmountStr = it },
          label = "Montant estimé (DA)",
          placeholder = "Ex : 1500000"
        )

        AppDropdown(
          label = "Priorité",
          options = listOf("NORMALE", "URGENTE", "FAIBLE"),
          selectedOption = priority,
          onOptionSelected = { priority = it }
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (requesterName.isNotBlank() && selectedProject != null) {
          val sdf = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US)
          val daNum = "DA-${sdf.format(Date())}"
          onConfirm(
            PurchaseRequestEntity(
              daNumber = daNum,
              requesterName = requesterName,
              department = department,
              projectId = selectedProject!!.id,
              projectName = selectedProject!!.name,
              priority = priority,
              justification = justification,
              totalEstimatedDzd = estimatedAmountStr.toDoubleOrNull() ?: 0.0
            )
          )
        }
      }) {
        Text("Valider la DA")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

// ---------------- DIALOG: NOUVEAU BC ----------------

@Composable
private fun NewBcDialog(
  suppliers: List<SupplierEntity>,
  projects: List<ProjectEntity>,
  products: List<ProductEntity>,
  purchaseRequests: List<PurchaseRequestEntity>,
  initialDa: PurchaseRequestEntity? = null,
  onDismiss: () -> Unit,
  onConfirm: (PurchaseOrderEntity, List<PurchaseOrderLineEntity>) -> Unit
) {
  var selectedDa by remember { mutableStateOf(initialDa) }
  var selectedSupplier by remember { mutableStateOf(suppliers.firstOrNull()) }
  var selectedProject by remember {
    mutableStateOf(
      initialDa?.let { da -> projects.find { it.id == da.projectId } } ?: projects.firstOrNull()
    )
  }
  var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }
  var quantityStr by remember { mutableStateOf("100") }
  var unitPriceStr by remember { mutableStateOf("1500") }
  var paymentTerms by remember { mutableStateOf("30 jours") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Émettre un Bon de Commande (BC)")
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Liaison DA (Demande d'Achat)
        AppDropdown(
          label = "Lier à une Demande d'Achat (DA)",
          options = listOf<PurchaseRequestEntity?>(null) + purchaseRequests,
          selectedOption = selectedDa,
          onOptionSelected = { da ->
            selectedDa = da
            if (da != null) {
              val matchingPrj = projects.find { it.id == da.projectId }
              if (matchingPrj != null) selectedProject = matchingPrj
            }
          },
          optionToString = {
            it?.let { da -> "${da.daNumber} - ${da.projectName} (${da.requesterName})" }
              ?: "Aucune DA (Achat direct sans DA)"
          }
        )

        if (selectedDa != null) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().testTag("new_bc_linked_da_indicator")
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "DA liée : ${selectedDa!!.daNumber} • Chantier : ${selectedDa!!.projectName}",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
              if (selectedDa!!.justification.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  "Motif : ${selectedDa!!.justification}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        AppDropdown(
          label = "Fournisseur retenu *",
          options = suppliers,
          selectedOption = selectedSupplier,
          onOptionSelected = { selectedSupplier = it },
          optionToString = { "${it.raisonSociale} (${it.wilayaName})" }
        )

        AppDropdown(
          label = "Chantier / Projet de livraison direct *",
          options = projects,
          selectedOption = selectedProject,
          onOptionSelected = { selectedProject = it },
          optionToString = { "${it.code} - ${it.name}" }
        )

        AppDropdown(
          label = "Article du catalogue *",
          options = products,
          selectedOption = selectedProduct,
          onOptionSelected = {
            selectedProduct = it
            if (it.lastPriceDzd > 0) unitPriceStr = it.lastPriceDzd.toString()
          },
          optionToString = { "${it.designationFr} (${it.reference})" }
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          AppTextField(
            value = quantityStr,
            onValueChange = { quantityStr = it },
            label = "Quantité",
            modifier = Modifier.weight(1f)
          )
          AppTextField(
            value = unitPriceStr,
            onValueChange = { unitPriceStr = it },
            label = "Prix Unitaire HT",
            modifier = Modifier.weight(1f)
          )
        }

        AppDropdown(
          label = "Modalités de règlement",
          options = listOf("Comptant", "30 jours", "45 jours", "60 jours"),
          selectedOption = paymentTerms,
          onOptionSelected = { paymentTerms = it }
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (selectedSupplier != null && selectedProject != null && selectedProduct != null) {
          val sdf = SimpleDateFormat("yyMMdd-HHmm", Locale.US)
          val bcNum = "BC-2026-${sdf.format(Date())}"
          val qty = quantityStr.toDoubleOrNull() ?: 1.0
          val pu = unitPriceStr.toDoubleOrNull() ?: 1.0
          val totalHt = qty * pu
          val tvaRate = selectedProduct!!.tvaRate
          val totalTva = totalHt * (tvaRate / 100.0)
          val totalTtc = totalHt + totalTva

          val bc = PurchaseOrderEntity(
            bcNumber = bcNum,
            purchaseRequestId = selectedDa?.id,
            daNumber = selectedDa?.daNumber ?: "",
            supplierId = selectedSupplier!!.id,
            supplierName = selectedSupplier!!.raisonSociale,
            projectId = selectedProject!!.id,
            projectName = selectedProject!!.name,
            orderDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(Date()),
            totalHtDzd = totalHt,
            totalTvaDzd = totalTva,
            totalTtcDzd = totalTtc,
            paymentTerms = paymentTerms
          )

          val line = PurchaseOrderLineEntity(
            purchaseOrderId = bc.id,
            productId = selectedProduct!!.id,
            productReference = selectedProduct!!.reference,
            designation = selectedProduct!!.designationFr,
            unit = selectedProduct!!.unit,
            quantity = qty,
            unitPriceHt = pu,
            tvaRate = tvaRate,
            totalHt = totalHt,
            totalTtc = totalTtc
          )

          onConfirm(bc, listOf(line))
        }
      }) {
        Text("Créer le BC")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

// ---------------- DIALOG: NOUVELLE RÉCEPTION ----------------

@Composable
private fun NewReceptionDialog(
  purchaseOrders: List<PurchaseOrderEntity>,
  onDismiss: () -> Unit,
  onConfirm: (ReceptionEntity) -> Unit
) {
  var selectedBc by remember { mutableStateOf(purchaseOrders.firstOrNull()) }
  var blNumber by remember { mutableStateOf("") }
  var receivedBy by remember { mutableStateOf("Mourad Belkacem (Chef Chantier)") }
  var notes by remember { mutableStateOf("Livraison directe conforme aux spécifications") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Réception directe sur Chantier (BR)") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDropdown(
          label = "Bon de Commande rattaché *",
          options = purchaseOrders,
          selectedOption = selectedBc,
          onOptionSelected = { selectedBc = it },
          optionToString = { "${it.bcNumber} - ${it.projectName} (${it.supplierName})" }
        )

        AppTextField(
          value = blNumber,
          onValueChange = { blNumber = it },
          label = "Numéro Bon de Livraison (BL Fournisseur) *",
          placeholder = "Ex : BL-778841"
        )

        AppTextField(
          value = receivedBy,
          onValueChange = { receivedBy = it },
          label = "Réceptionnaire sur chantier",
          placeholder = "Nom du responsable ayant signé le BL"
        )

        AppTextField(
          value = notes,
          onValueChange = { notes = it },
          label = "Observations / Contrôle qualité"
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (selectedBc != null && blNumber.isNotBlank()) {
          val sdf = SimpleDateFormat("yyMMdd-HHmm", Locale.US)
          val brNum = "BR-2026-${sdf.format(Date())}"
          onConfirm(
            ReceptionEntity(
              blNumber = blNumber,
              brNumber = brNum,
              purchaseOrderId = selectedBc!!.id,
              bcNumber = selectedBc!!.bcNumber,
              supplierId = selectedBc!!.supplierId,
              supplierName = selectedBc!!.supplierName,
              destinationType = "PROJET",
              destinationId = selectedBc!!.projectId,
              destinationName = selectedBc!!.projectName,
              receptionDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(Date()),
              receivedBy = receivedBy,
              status = "CONFORME",
              notes = notes
            )
          )
        }
      }) {
        Text("Valider la Réception")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

// ---------------- DIALOG: NOUVELLE FACTURE ----------------

@Composable
private fun NewInvoiceDialog(
  purchaseOrders: List<PurchaseOrderEntity>,
  onDismiss: () -> Unit,
  onConfirm: (SupplierInvoiceEntity) -> Unit
) {
  var selectedBc by remember { mutableStateOf(purchaseOrders.firstOrNull()) }
  var invoiceNumber by remember { mutableStateOf("") }
  var dueDate by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Enregistrer Facture & Rapprochement") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDropdown(
          label = "Bon de Commande associé *",
          options = purchaseOrders,
          selectedOption = selectedBc,
          onOptionSelected = { selectedBc = it },
          optionToString = { "${it.bcNumber} - ${it.projectName} (${it.supplierName})" }
        )

        AppTextField(
          value = invoiceNumber,
          onValueChange = { invoiceNumber = it },
          label = "Numéro Facture Fournisseur *",
          placeholder = "Ex : FACT-2026-991"
        )

        AppDatePicker(
          value = dueDate,
          onDateSelected = { dueDate = it },
          label = "Date d'échéance de paiement"
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (selectedBc != null && invoiceNumber.isNotBlank()) {
          onConfirm(
            SupplierInvoiceEntity(
              invoiceNumber = invoiceNumber,
              invoiceDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(Date()),
              dueDate = dueDate.ifBlank { "30 jours" },
              supplierId = selectedBc!!.supplierId,
              supplierName = selectedBc!!.supplierName,
              projectId = selectedBc!!.projectId,
              projectName = selectedBc!!.projectName,
              purchaseOrderId = selectedBc!!.id,
              bcNumber = selectedBc!!.bcNumber,
              totalHtDzd = selectedBc!!.totalHtDzd,
              totalTvaDzd = selectedBc!!.totalTvaDzd,
              totalTtcDzd = selectedBc!!.totalTtcDzd,
              matchingStatus = "CONFORME"
            )
          )
        }
      }) {
        Text("Rapprocher & Enregistrer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}
