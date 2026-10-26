package com.example.ui.suppliers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.AppDatePicker
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.core.designsystem.components.ExportButton
import com.example.core.designsystem.components.RatingScoreBar
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.core.utils.AlgerianData
import com.example.core.utils.AlgerianFiscalValidator
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInteractionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDetailScreen(
  supplierId: String,
  viewModel: SupplierViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToEdit: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val supplier by viewModel.getSupplier(supplierId).collectAsStateWithLifecycle(initialValue = null)
  val bankAccounts by viewModel.getBankAccounts(supplierId).collectAsStateWithLifecycle(initialValue = emptyList())
  val documents by viewModel.getDocuments(supplierId).collectAsStateWithLifecycle(initialValue = emptyList())
  val interactions by viewModel.getInteractions(supplierId).collectAsStateWithLifecycle(initialValue = emptyList())

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var showStatusDialog by remember { mutableStateOf(false) }
  var showArchiveDialog by remember { mutableStateOf(false) }
  var showAddBankDialog by remember { mutableStateOf(false) }
  var showAddDocDialog by remember { mutableStateOf(false) }
  var showAddInteractionDialog by remember { mutableStateOf(false) }

  if (supplier == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Chargement du fournisseur...")
    }
    return
  }

  val sup = supplier!!
  val statement = viewModel.getAccountStatement(sup.id)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            sup.raisonSociale,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          IconButton(onClick = { onNavigateToEdit(sup.id) }) {
            Icon(Icons.Default.Edit, contentDescription = "Modifier")
          }
          ExportButton(
            onExport = { format -> viewModel.exportSupplierSheet(context, sup, format) },
            label = "Fiche"
          )
          IconButton(onClick = { showArchiveDialog = true }) {
            Icon(Icons.Default.Archive, contentDescription = "Archiver", tint = MaterialTheme.colorScheme.error)
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
      // 1. Supplier Header Card
      HeaderSummaryCard(
        supplier = sup,
        onStatusClick = { showStatusDialog = true },
        onCall = {
          val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sup.phone}"))
          context.startActivity(intent)
        },
        onMail = {
          val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${sup.email}"))
          context.startActivity(intent)
        }
      )

      // 2. Tabs Row
      val tabTitles = listOf(
        "Aperçu",
        "Comptes Bancaires (${bankAccounts.size})",
        "Documents (${documents.size})",
        "Évaluation",
        "Suivi & Chronologie (${interactions.size})",
        "Relevé de Compte"
      )

      ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        tabTitles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = { Text(title, style = MaterialTheme.typography.labelMedium) }
          )
        }
      }

      // 3. Tab Contents
      when (selectedTabIndex) {
        0 -> TabOverview(sup)
        1 -> TabBankAccounts(
          bankAccounts = bankAccounts,
          onAddAccount = { showAddBankDialog = true }
        )
        2 -> TabDocuments(
          documents = documents,
          onAddDocument = { showAddDocDialog = true }
        )
        3 -> TabEvaluation(
          supplier = sup,
          onSaveScores = { q, p, d, s, r ->
            viewModel.updateScores(sup.id, q, p, d, s, r)
            Toast.makeText(context, "Évaluation enregistrée et tracée dans l'audit log", Toast.LENGTH_SHORT).show()
          }
        )
        4 -> TabTimeline(
          interactions = interactions,
          onAddInteraction = { showAddInteractionDialog = true }
        )
        5 -> TabStatement(statement = statement, supplier = sup)
      }
    }
  }

  // --- Dialogs ---

  if (showStatusDialog) {
    StatusChangeDialog(
      currentStatus = sup.status,
      onDismiss = { showStatusDialog = false },
      onConfirm = { newStatus, reason ->
        viewModel.updateStatus(sup.id, newStatus, reason)
        showStatusDialog = false
      }
    )
  }

  if (showArchiveDialog) {
    ArchiveSupplierDialog(
      supplierName = sup.raisonSociale,
      onDismiss = { showArchiveDialog = false },
      onConfirm = { reason ->
        viewModel.archiveSupplier(sup.id, reason) {
          showArchiveDialog = false
          onNavigateBack()
        }
      }
    )
  }

  if (showAddBankDialog) {
    AddBankDialog(
      supplierId = sup.id,
      onDismiss = { showAddBankDialog = false },
      onConfirm = { account ->
        viewModel.addBankAccount(account)
        showAddBankDialog = false
      }
    )
  }

  if (showAddDocDialog) {
    AddDocumentDialog(
      supplierId = sup.id,
      onDismiss = { showAddDocDialog = false },
      onConfirm = { doc ->
        viewModel.addDocument(doc)
        showAddDocDialog = false
      }
    )
  }

  if (showAddInteractionDialog) {
    AddInteractionDialog(
      supplierId = sup.id,
      onDismiss = { showAddInteractionDialog = false },
      onConfirm = { inter ->
        viewModel.addInteraction(inter)
        showAddInteractionDialog = false
      }
    )
  }
}

// ---------------- TAB 0: APERÇU ----------------

@Composable
private fun TabOverview(supplier: SupplierEntity) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      SectionCard(title = "Identification Fiscale & Légale (Algérie)") {
        DetailRow("NIF (15 chiffres)", supplier.nif.ifBlank { "Non renseigné" })
        DetailRow("NIS (15 chiffres)", supplier.nis.ifBlank { "Non renseigné" })
        DetailRow("Registre de Commerce (RC)", "${supplier.rcNumber} (Date: ${supplier.rcDate})")
        DetailRow("Article d'imposition (AI)", supplier.articleImposition.ifBlank { "Non renseigné" })
        if (supplier.carteArtisanNin.isNotBlank()) {
          DetailRow("Carte Artisan / NIN", supplier.carteArtisanNin)
        }
        DetailRow("Forme juridique", supplier.formeJuridique)
      }
    }

    item {
      SectionCard(title = "Coordonnées & Localisation") {
        DetailRow("Contact principal", supplier.contactPerson.ifBlank { "Non renseigné" })
        DetailRow("Téléphone", supplier.phone.ifBlank { "Non renseigné" })
        DetailRow("Email", supplier.email.ifBlank { "Non renseigné" })
        DetailRow("Site Web", supplier.website.ifBlank { "Non renseigné" })
        DetailRow("Adresse", supplier.adresse.ifBlank { "Non renseigné" })
        DetailRow("Wilaya & Commune", "${supplier.commune}, ${supplier.wilayaName} (${supplier.wilayaCode})")
      }
    }

    item {
      SectionCard(title = "Conditions Commerciales") {
        DetailRow("Modalités de paiement", "${supplier.paymentTermsDays} jours")
        DetailRow("Délai de livraison moyen", "${supplier.leadTimeDays} jours")
        DetailRow("Zones de livraison", supplier.deliveryZones)
        DetailRow("Catégories / Spécialités", supplier.categoriesTags)
      }
    }

    item {
      SectionCard(title = "Audit & Versioning") {
        DetailRow("Version courante", "v${supplier.version} (immutable)")
        DetailRow("Créé par", supplier.createdBy)
        DetailRow("Dernière modification", supplier.updatedBy)
      }
    }
  }
}

// ---------------- TAB 1: COMPTES BANCAIRES ----------------

@Composable
private fun TabBankAccounts(
  bankAccounts: List<SupplierBankAccountEntity>,
  onAddAccount: () -> Unit
) {
  val context = LocalContext.current
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Comptes Bancaires & CCP",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Button(onClick = onAddAccount) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Ajouter RIB")
        }
      }
    }

    if (bankAccounts.isEmpty()) {
      item {
        OutlinedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
          Text(
            "Aucun compte bancaire ou CCP enregistré pour ce fournisseur.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      items(bankAccounts, key = { it.id }) { acc ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
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
                Icon(
                  Icons.Default.AccountBalance,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = acc.bankName + if (acc.agency.isNotBlank()) " (${acc.agency})" else "",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
              if (acc.isPrimary) {
                StatusChip("Principal", StatusType.APPROVED)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RIB 20 digits formatted with copy button
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  "RIB (20 chiffres) :",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  AlgerianFiscalValidator.formatRib(acc.rib),
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
              IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("RIB", acc.rib)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "RIB copié dans le presse-papier", Toast.LENGTH_SHORT).show()
              }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copier le RIB")
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            DetailRow("Titulaire", acc.holderName)
          }
        }
      }
    }
  }
}

// ---------------- TAB 2: DOCUMENTS & ALERTES ----------------

@Composable
private fun TabDocuments(
  documents: List<SupplierDocumentEntity>,
  onAddDocument: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Documents Légaux & Alertes d'Expiration",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Button(onClick = onAddDocument) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Ajouter Document")
        }
      }
    }

    if (documents.isEmpty()) {
      item {
        OutlinedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
          Text(
            "Aucun document enregistré. Ajoutez le Registre de Commerce, l'extrait de rôle C20, ou l'attestation CASNOS/CNAS.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      items(documents, key = { it.id }) { doc ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "Type : ${doc.docType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              if (doc.expiryDate.isNotBlank()) {
                StatusChip("Expire: ${doc.expiryDate}", StatusType.WARNING)
              } else {
                StatusChip("Permanent", StatusType.APPROVED)
              }
            }

            if (doc.referenceNumber.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              DetailRow("Réf. / N°", doc.referenceNumber)
            }
            if (doc.issueDate.isNotBlank()) {
              DetailRow("Date délivrance", doc.issueDate)
            }
            if (doc.notes.isNotBlank()) {
              DetailRow("Observations", doc.notes)
            }
          }
        }
      }
    }
  }
}

// ---------------- TAB 3: ÉVALUATION (SCORING) ----------------

@Composable
private fun TabEvaluation(
  supplier: SupplierEntity,
  onSaveScores: (Double, Double, Double, Double, Double) -> Unit
) {
  var quality by remember { mutableDoubleStateOf(supplier.qualityScore) }
  var price by remember { mutableDoubleStateOf(supplier.priceScore) }
  var delivery by remember { mutableDoubleStateOf(supplier.deliveryScore) }
  var afterSales by remember { mutableDoubleStateOf(supplier.afterSalesScore) }
  var reliability by remember { mutableDoubleStateOf(supplier.reliabilityScore) }

  val liveAverage = (quality + price + delivery + afterSales + reliability) / 5.0
  val formattedAvg = Math.round(liveAverage * 10.0) / 10.0

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            "Note Globale de Performance",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "$formattedAvg / 5.0",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(4.dp))
          RatingScoreBar(rating = liveAverage, starSize = 24.dp)
        }
      }
    }

    item {
      SectionCard(title = "Critères d'évaluation détaillée") {
        ScoreSliderItem(title = "1. Qualité des produits & prestations", value = quality) { quality = it }
        ScoreSliderItem(title = "2. Compétitivité des prix & remises", value = price) { price = it }
        ScoreSliderItem(title = "3. Respect des délais de livraison", value = delivery) { delivery = it }
        ScoreSliderItem(title = "4. Service Après-Vente (SAV) & réactivité", value = afterSales) { afterSales = it }
        ScoreSliderItem(title = "5. Fiabilité, solvabilité & conformité fiscale", value = reliability) { reliability = it }
      }
    }

    item {
      Button(
        onClick = { onSaveScores(quality, price, delivery, afterSales, reliability) },
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Star, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Enregistrer l'évaluation & tracer dans l'audit log")
      }
    }
  }
}

@Composable
private fun ScoreSliderItem(
  title: String,
  value: Double,
  onValueChange: (Double) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
      Text("${Math.round(value * 10.0) / 10.0}/5", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
    }
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(Math.round(it * 10.0) / 10.0) },
      valueRange = 1f..5f,
      steps = 7
    )
  }
}

// ---------------- TAB 4: SUIVI & CHRONOLOGIE (TIMELINE) ----------------

@Composable
private fun TabTimeline(
  interactions: List<SupplierInteractionEntity>,
  onAddInteraction: () -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Historique des Échanges & Achats",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Button(onClick = onAddInteraction) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Ajouter Note")
        }
      }
    }

    if (interactions.isEmpty()) {
      item {
        OutlinedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
          Text(
            "Aucune interaction enregistrée. Notez les appels, devis reçus, commandes, réclamations ou visites.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      items(interactions, key = { it.id }) { item ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
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
                Icon(Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  item.type,
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Text(
                item.createdBy,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(item.subject, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

            if (item.details.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(item.details, style = MaterialTheme.typography.bodyMedium)
            }

            if (item.amountDzd != null && item.amountDzd > 0) {
              Spacer(modifier = Modifier.height(6.dp))
              AmountText(amount = item.amountDzd)
            }

            if (item.outcome.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text("Résultat : ${item.outcome}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
          }
        }
      }
    }
  }
}

// ---------------- TAB 5: RELEVÉ DE COMPTE ----------------

@Composable
private fun TabStatement(
  statement: com.example.data.repository.SupplierAccountStatement,
  supplier: SupplierEntity
) {
  val context = LocalContext.current
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      SectionCard(title = "Synthèse Financière (DZD)") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Total Commandé (BC validés) :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.totalOrderedDzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Total Reçu sur chantier (BL/BR) :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.totalReceivedDzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Total Facturé :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.totalInvoicedDzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Total Réglé (Virements/Chèques) :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.totalPaidDzd)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Solde Fournisseur Dû :", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          AmountText(
            amount = statement.currentBalanceDzd,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
          )
        }
      }
    }

    item {
      SectionCard(title = "Balance Âgée des Factures") {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("0 - 30 jours (courant) :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.aging0To30Dzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("30 - 60 jours :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.aging30To60Dzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("60 - 90 jours :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.aging60To90Dzd)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("+ 90 jours (en retard) :", style = MaterialTheme.typography.bodyMedium)
          AmountText(statement.aging90PlusDzd, color = MaterialTheme.colorScheme.error)
        }
      }
    }

    item {
      FilledTonalButton(
        onClick = {
          val report = "RELEVE DE COMPTE FOURNISSEUR : ${supplier.raisonSociale}\nSolde actuel : ${statement.currentBalanceDzd} DA\nTotal commandé : ${statement.totalOrderedDzd} DA\nTotal payé : ${statement.totalPaidDzd} DA"
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
          }
          context.startActivity(Intent.createChooser(sendIntent, "Partager le relevé"))
        },
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.History, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Partager le relevé de compte (WhatsApp / Email)")
      }
    }
  }
}

// ---------------- REUSABLE HELPERS ----------------

@Composable
private fun HeaderSummaryCard(
  supplier: SupplierEntity,
  onStatusClick: () -> Unit,
  onCall: () -> Unit,
  onMail: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = supplier.raisonSociale,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${supplier.formeJuridique} • ${supplier.commune}, ${supplier.wilayaName}",
            style = MaterialTheme.typography.bodyMedium,
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

        Column(horizontalAlignment = Alignment.End) {
          StatusChip(statusLabel, statusType)
          TextButton(onClick = onStatusClick) {
            Text("Changer statut", style = MaterialTheme.typography.labelSmall)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          RatingScoreBar(rating = supplier.overallRating, starSize = 18.dp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${supplier.overallRating}/5",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (supplier.phone.isNotBlank()) {
            OutlinedButton(onClick = onCall) {
              Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Appeler")
            }
          }
          if (supplier.email.isNotBlank()) {
            OutlinedButton(onClick = onMail) {
              Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Email")
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      content()
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

// ---------------- DIALOGS ----------------

@Composable
private fun StatusChangeDialog(
  currentStatus: String,
  onDismiss: () -> Unit,
  onConfirm: (newStatus: String, reason: String) -> Unit
) {
  var selectedStatus by remember { mutableStateOf(currentStatus) }
  var reason by remember { mutableStateOf("") }
  var reasonError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Changer le statut fournisseur") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppDropdown(
          label = "Nouveau statut",
          options = listOf("AGREE", "EN_COURS", "SUSPENDU", "BLACKLISTE"),
          selectedOption = selectedStatus,
          onOptionSelected = { selectedStatus = it },
          optionToString = {
            when (it) {
              "AGREE" -> "Agréé"
              "EN_COURS" -> "En cours d'agrément"
              "SUSPENDU" -> "Suspendu"
              "BLACKLISTE" -> "Blacklisté"
              else -> it
            }
          }
        )

        AppTextField(
          value = reason,
          onValueChange = {
            reason = it
            reasonError = null
          },
          label = "Motif obligatoire (tracé dans l'audit log)",
          placeholder = "Ex : Validation de l'audit usine / Retards répétés",
          errorMessage = reasonError,
          singleLine = false,
          maxLines = 3
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (reason.isBlank()) {
          reasonError = "Le motif est requis pour l'audit log"
        } else {
          onConfirm(selectedStatus, reason)
        }
      }) {
        Text("Confirmer le changement")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

@Composable
private fun ArchiveSupplierDialog(
  supplierName: String,
  onDismiss: () -> Unit,
  onConfirm: (reason: String) -> Unit
) {
  var reason by remember { mutableStateOf("") }
  var reasonError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Archiver le fournisseur") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Attention : aucune donnée n'est supprimée définitivement (soft delete). Le fournisseur $supplierName sera archivé et tracé.")
        AppTextField(
          value = reason,
          onValueChange = {
            reason = it
            reasonError = null
          },
          label = "Motif d'archivage",
          placeholder = "Ex : Cessation d'activité / Remplacement",
          errorMessage = reasonError
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (reason.isBlank()) {
          reasonError = "Le motif est obligatoire"
        } else {
          onConfirm(reason)
        }
      }) {
        Text("Archiver")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

@Composable
private fun AddBankDialog(
  supplierId: String,
  onDismiss: () -> Unit,
  onConfirm: (SupplierBankAccountEntity) -> Unit
) {
  var bankName by remember { mutableStateOf(AlgerianData.ALGERIAN_BANKS.first().first) }
  var agency by remember { mutableStateOf("") }
  var rib by remember { mutableStateOf("") }
  var holderName by remember { mutableStateOf("") }
  var ribError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Ajouter un compte bancaire / CCP") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDropdown(
          label = "Banque / CCP",
          options = AlgerianData.ALGERIAN_BANKS.map { it.first },
          selectedOption = bankName,
          onOptionSelected = { bankName = it }
        )
        AppTextField(
          value = agency,
          onValueChange = { agency = it },
          label = "Agence / Guichet (Ex: Chéraga 612)"
        )
        AppTextField(
          value = rib,
          onValueChange = {
            rib = it
            ribError = null
          },
          label = "RIB (20 chiffres en Algérie)",
          placeholder = "00100612012345678912",
          errorMessage = ribError
        )
        AppTextField(
          value = holderName,
          onValueChange = { holderName = it },
          label = "Titulaire du compte"
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (!AlgerianFiscalValidator.validateRib(rib)) {
          ribError = "Le RIB doit comporter exactement 20 chiffres"
        } else if (holderName.isBlank()) {
          ribError = "Veuillez préciser le titulaire du compte"
        } else {
          onConfirm(
            SupplierBankAccountEntity(
              supplierId = supplierId,
              bankName = bankName,
              agency = agency,
              rib = rib.replace(" ", ""),
              holderName = holderName,
              isPrimary = false
            )
          )
        }
      }) {
        Text("Enregistrer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

@Composable
private fun AddDocumentDialog(
  supplierId: String,
  onDismiss: () -> Unit,
  onConfirm: (SupplierDocumentEntity) -> Unit
) {
  var docType by remember { mutableStateOf("RC") }
  var title by remember { mutableStateOf("") }
  var referenceNumber by remember { mutableStateOf("") }
  var issueDate by remember { mutableStateOf("") }
  var expiryDate by remember { mutableStateOf("") }

  val docTypes = listOf(
    "RC" to "Registre de Commerce (Extrait CNRC)",
    "REGULARITE_FISCALE" to "Extrait de rôle (C20) apuré",
    "CASNOS_CNAS" to "Mise à jour CASNOS / CNAS",
    "NIF_NIS" to "Attestation d'immatriculation fiscale",
    "RIB" to "Attestation bancaire / RIB officiel",
    "CONTRACT" to "Convention / Contrat cadre",
    "AUTRE" to "Autre document"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Ajouter un document légal") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDropdown(
          label = "Type de document",
          options = docTypes.map { it.first },
          selectedOption = docType,
          onOptionSelected = {
            docType = it
            title = docTypes.firstOrNull { d -> d.first == it }?.second ?: ""
          },
          optionToString = { code -> docTypes.firstOrNull { it.first == code }?.second ?: code }
        )

        AppTextField(
          value = title,
          onValueChange = { title = it },
          label = "Intitulé du document"
        )

        AppTextField(
          value = referenceNumber,
          onValueChange = { referenceNumber = it },
          label = "Numéro de référence"
        )

        AppDatePicker(
          value = issueDate,
          onDateSelected = { issueDate = it },
          label = "Date de délivrance"
        )

        AppDatePicker(
          value = expiryDate,
          onDateSelected = { expiryDate = it },
          label = "Date d'expiration (pour alerte)"
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (title.isBlank()) {
          title = docTypes.firstOrNull { it.first == docType }?.second ?: "Document"
        }
        onConfirm(
          SupplierDocumentEntity(
            supplierId = supplierId,
            docType = docType,
            title = title,
            referenceNumber = referenceNumber,
            issueDate = issueDate,
            expiryDate = expiryDate
          )
        )
      }) {
        Text("Ajouter")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}

@Composable
private fun AddInteractionDialog(
  supplierId: String,
  onDismiss: () -> Unit,
  onConfirm: (SupplierInteractionEntity) -> Unit
) {
  val interactionTypes = listOf(
    "APPEL" to "Appel téléphonique",
    "REUNION" to "Réunion / Visite",
    "EMAIL" to "Email échangé",
    "RFQ" to "Demande de prix (RFQ)",
    "DEVIS" to "Devis / Offre reçue",
    "COMMANDE" to "Bon de Commande",
    "RECEPTION" to "Livraison sur chantier",
    "RECLAMATION" to "Réclamation / Litige",
    "NOTE" to "Note interne"
  )

  var type by remember { mutableStateOf("APPEL") }
  var subject by remember { mutableStateOf("") }
  var details by remember { mutableStateOf("") }
  var outcome by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Nouvelle interaction / Suivi") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppDropdown(
          label = "Type d'interaction",
          options = interactionTypes.map { it.first },
          selectedOption = type,
          onOptionSelected = { type = it },
          optionToString = { code -> interactionTypes.firstOrNull { it.first == code }?.second ?: code }
        )

        AppTextField(
          value = subject,
          onValueChange = { subject = it },
          label = "Objet / Titre"
        )

        AppTextField(
          value = details,
          onValueChange = { details = it },
          label = "Détails de l'échange",
          singleLine = false,
          maxLines = 3
        )

        AppTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = "Montant en DA (si devis ou commande)",
          placeholder = "Ex : 450000"
        )

        AppTextField(
          value = outcome,
          onValueChange = { outcome = it },
          label = "Résultat / Décision"
        )
      }
    },
    confirmButton = {
      Button(onClick = {
        if (subject.isNotBlank()) {
          onConfirm(
            SupplierInteractionEntity(
              supplierId = supplierId,
              type = type,
              subject = subject,
              details = details,
              outcome = outcome,
              amountDzd = amountStr.toDoubleOrNull()
            )
          )
        }
      }) {
        Text("Enregistrer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Annuler") }
    }
  )
}
