package com.example.ui.projects

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierInvoiceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
  projectId: String,
  viewModel: ProjectViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val project by viewModel.getProject(projectId).collectAsStateWithLifecycle(initialValue = null)
  val orderLines by viewModel.getOrderLinesForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
  val purchaseOrders by viewModel.getPurchaseOrdersForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
  val receptions by viewModel.getReceptionsForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())
  val invoices by viewModel.getInvoicesForProject(projectId).collectAsStateWithLifecycle(initialValue = emptyList())

  var selectedTab by remember { mutableIntStateOf(0) }

  if (project == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Chargement du projet...")
    }
    return
  }

  val prj = project!!
  val remainingBudget = (prj.totalBudgetDzd - prj.committedBudgetDzd).coerceAtLeast(0.0)
  val consumptionPercent = if (prj.totalBudgetDzd > 0) {
    (prj.committedBudgetDzd / prj.totalBudgetDzd).coerceIn(0.0, 1.0).toFloat()
  } else 0f

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              prj.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              "Code : ${prj.code} • ${prj.locationCommune}",
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
    ) {
      // 1. Budget Summary Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "Suivi Budgétaire & Achats Directs Chantier",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            StatusChip(prj.status, StatusType.APPROVED)
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Budget Total Alloué :", style = MaterialTheme.typography.bodyMedium)
            AmountText(prj.totalBudgetDzd, fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Achats Engagés (BC) :", style = MaterialTheme.typography.bodyMedium)
            AmountText(prj.committedBudgetDzd, color = MaterialTheme.colorScheme.secondary)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Facturé :", style = MaterialTheme.typography.bodyMedium)
            AmountText(prj.invoicedBudgetDzd)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Solde Budgétaire Restant :", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            AmountText(remainingBudget, color = if (remainingBudget < prj.totalBudgetDzd * 0.15) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
          }

          Spacer(modifier = Modifier.height(4.dp))
          LinearProgressIndicator(
            progress = { consumptionPercent },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (consumptionPercent > 0.85f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          )
        }
      }

      // 2. Tabs
      val tabs = listOf(
        "Marchandises Achetées (${orderLines.size})",
        "Commandes BC (${purchaseOrders.size})",
        "Réceptions Chantier (${receptions.size})",
        "Factures (${invoices.size})"
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

      // 3. Tab Contents
      when (selectedTab) {
        0 -> TabPurchasedGoods(orderLines = orderLines)
        1 -> TabProjectOrders(orders = purchaseOrders)
        2 -> TabProjectReceptions(receptions = receptions)
        3 -> TabProjectInvoices(invoices = invoices)
      }
    }
  }
}

// TAB 0: ARTICLES & MARCHANDISES ACHETÉES POUR CE CHANTIER
@Composable
private fun TabPurchasedGoods(orderLines: List<PurchaseOrderLineEntity>) {
  if (orderLines.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
      Text(
        "Aucune marchandise commandée pour ce chantier pour le moment.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(orderLines, key = { it.id }) { line ->
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
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  line.designation,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (line.productReference.isNotBlank()) {
                  Text(
                    "Réf: ${line.productReference}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              AmountText(line.totalTtc, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                "Quantité : ${line.quantity} ${line.unit}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Text(
                "P.U HT : ${line.unitPriceHt} DA (TVA ${line.tvaRate.toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            if (line.receivedQuantity > 0) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "✓ Reçu sur chantier : ${line.receivedQuantity} ${line.unit}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }
        }
      }
    }
  }
}

// TAB 1: BONS DE COMMANDE
@Composable
private fun TabProjectOrders(orders: List<PurchaseOrderEntity>) {
  if (orders.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
      Text("Aucun bon de commande émis pour ce chantier.")
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
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
            Text("Fournisseur : ${bc.supplierName}", style = MaterialTheme.typography.bodyMedium)
            Text("Date commande : ${bc.orderDate} • Délais : ${bc.paymentTerms}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total TTC :", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              AmountText(bc.totalTtcDzd)
            }
          }
        }
      }
    }
  }
}

// TAB 2: RÉCEPTIONS CHANTIER
@Composable
private fun TabProjectReceptions(receptions: List<ReceptionEntity>) {
  if (receptions.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
      Text("Aucune livraison réceptionnée sur chantier pour le moment.")
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
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
              StatusChip(rec.status, StatusType.APPROVED)
            }
            Text("BL Fournisseur N° : ${rec.blNumber} (${rec.supplierName})", style = MaterialTheme.typography.bodyMedium)
            Text("Date réception : ${rec.receptionDate} • Réceptionné par : ${rec.receivedBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (rec.notes.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text("Constat : ${rec.notes}", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
  }
}

// TAB 3: FACTURES CHANTIER
@Composable
private fun TabProjectInvoices(invoices: List<SupplierInvoiceEntity>) {
  if (invoices.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
      Text("Aucune facture enregistrée pour ce chantier.")
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
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
            Text("Fournisseur : ${inv.supplierName}", style = MaterialTheme.typography.bodyMedium)
            Text("Date : ${inv.invoiceDate} • Échéance : ${inv.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Rapprochement 3 voies : ${inv.matchingStatus}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Facturé TTC :", style = MaterialTheme.typography.bodyMedium)
              AmountText(inv.totalTtcDzd)
            }
          }
        }
      }
    }
  }
}
