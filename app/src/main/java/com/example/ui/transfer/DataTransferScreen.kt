package com.example.ui.transfer

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.transfer.DataTransferManager
import com.example.core.transfer.TransferPackage
import com.example.core.transfer.TransferScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTransferScreen(
  viewModel: DataTransferViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) }
  var pasteInputText by remember { mutableStateOf("") }

  // File Picker Launcher for receiving transfer packages (.adz, .json, or any stream)
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      viewModel.parseFileUri(context, uri)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Transfert & Fusion Mobile",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Partage direct entre téléphones Acheteur DZ",
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
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Envoyer (Partager)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          },
          modifier = Modifier.testTag("tab_export_send")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Recevoir (Fusionner)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          },
          modifier = Modifier.testTag("tab_import_receive")
        )
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("transfer_content_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        if (selectedTab == 0) {
          // ==================== TAB 0: ENVOYER (PARTAGER) ====================
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.large,
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
              )
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                  )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                  Text(
                    "Envoyer les données vers un autre téléphone",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    "Exportez un package pour Quick Share, Bluetooth, WhatsApp ou Email. Le destinataire pourra l'intégrer en un clic.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          item {
            Text(
              "PÉRIMÈTRE DES DONNÉES À ENVOYER",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              TransferScope.values().forEach { scope ->
                val isSelected = uiState.selectedScope == scope
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setScope(scope) }
                    .testTag("scope_card_${scope.name}"),
                  shape = MaterialTheme.shapes.medium,
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                  ),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                  )
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        scope.labelFr,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                      )
                      Text(
                        scope.labelAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                    if (isSelected) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                  }
                }
              }
            }
          }

          // Summary Statistics of current local records
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium,
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  "Données prêtes au transfert :",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  MiniStat(Icons.Default.Business, "Fournisseurs", uiState.localSuppliersCount.toString())
                  MiniStat(Icons.Default.Architecture, "Chantiers", uiState.localProjectsCount.toString())
                  MiniStat(Icons.Default.ShoppingCart, "Bons de Commande", uiState.localOrdersCount.toString())
                  MiniStat(Icons.Default.Category, "Articles", uiState.localProductsCount.toString())
                }
              }
            }
          }

          item {
            Button(
              onClick = { viewModel.exportAndShare(context) },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_export_share"),
              shape = MaterialTheme.shapes.medium,
              enabled = !uiState.isExporting
            ) {
              if (uiState.isExporting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Préparation du package...")
              } else {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Envoyer vers l'autre téléphone (Partager)")
              }
            }
          }

        } else {
          // ==================== TAB 1: RECEVOIR (FUSIONNER) ====================
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.large,
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
              )
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.SyncAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(28.dp)
                  )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                  Text(
                    "Recevoir & Fusionner les données",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    "Intégrez les données reçues d'un autre utilisateur. Les fiches existantes sont mises à jour sans écraser votre historique.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          // Option 1: File selection
          item {
            OutlinedCard(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  "OPTION 1 : CHARGER UN FICHIER REÇU",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  "Fichier transféré via Quick Share, WhatsApp, Bluetooth ou Téléchargements (.adz ou .json).",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = { filePickerLauncher.launch("*/*") },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_pick_import_file"),
                  shape = MaterialTheme.shapes.medium
                ) {
                  Icon(Icons.Default.FileDownload, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Sélectionner le fichier reçu")
                }
              }
            }
          }

          // Option 2: Paste code
          item {
            OutlinedCard(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.medium
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  "OPTION 2 : COLLER UN CODE TEXTUEL",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                  value = pasteInputText,
                  onValueChange = { pasteInputText = it },
                  label = { Text("Code JSON du package") },
                  placeholder = { Text("Collez ici le texte du transfert...") },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("input_paste_transfer_json"),
                  maxLines = 4
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      val clip = clipboardManager.getText()?.text
                      if (!clip.isNullOrBlank()) {
                        pasteInputText = clip
                      }
                    },
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Coller le texte")
                  }
                  Button(
                    onClick = { viewModel.parsePastedText(pasteInputText) },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("btn_parse_pasted_code"),
                    enabled = pasteInputText.isNotBlank()
                  ) {
                    Text("Analyser")
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // ==================== PREVIEW & CONFIRMATION DIALOG ====================
  uiState.previewPackage?.let { pkg ->
    AlertDialog(
      onDismissRequest = { viewModel.dismissPreview() },
      icon = {
        Icon(
          Icons.Default.SyncAlt,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(36.dp)
        )
      },
      title = {
        Text(
          "Package Prêt à la Fusion",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Origine : ${pkg.senderDevice}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            "Date : ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date(pkg.timestamp))}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Éléments à intégrer dans votre application :",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )

          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              if (pkg.suppliers.isNotEmpty()) Text("• ${pkg.suppliers.size} Fournisseurs & Sous-traitants", style = MaterialTheme.typography.bodySmall)
              if (pkg.projects.isNotEmpty()) Text("• ${pkg.projects.size} Chantiers & Projets", style = MaterialTheme.typography.bodySmall)
              if (pkg.products.isNotEmpty()) Text("• ${pkg.products.size} Articles du catalogue", style = MaterialTheme.typography.bodySmall)
              if (pkg.purchaseRequests.isNotEmpty()) Text("• ${pkg.purchaseRequests.size} Demandes d'Achat (DA)", style = MaterialTheme.typography.bodySmall)
              if (pkg.purchaseOrders.isNotEmpty()) Text("• ${pkg.purchaseOrders.size} Bons de Commande (BC)", style = MaterialTheme.typography.bodySmall)
              if (pkg.receptions.isNotEmpty()) Text("• ${pkg.receptions.size} Réceptions de chantier", style = MaterialTheme.typography.bodySmall)
              if (pkg.invoices.isNotEmpty()) Text("• ${pkg.invoices.size} Factures", style = MaterialTheme.typography.bodySmall)
            }
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Les doublons seront mis à jour avec les dernières informations sans supprimer vos données existantes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { viewModel.confirmMerge() },
          modifier = Modifier.testTag("btn_confirm_merge")
        ) {
          Text("Fusionner les Données")
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.dismissPreview() }) {
          Text("Annuler")
        }
      }
    )
  }

  // ==================== MERGE SUCCESS REPORT DIALOG ====================
  uiState.mergeReport?.let { report ->
    AlertDialog(
      onDismissRequest = { viewModel.dismissReport() },
      icon = {
        Icon(
          Icons.Default.CheckCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(42.dp)
        )
      },
      title = {
        Text(
          "Fusion Terminée avec Succès !",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            report.message,
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Détail des opérations :",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("• Fournisseurs : +${report.suppliersAdded} ajoutés, ${report.suppliersUpdated} mis à jour", style = MaterialTheme.typography.bodySmall)
              Text("• Chantiers : +${report.projectsAdded} ajoutés, ${report.projectsUpdated} mis à jour", style = MaterialTheme.typography.bodySmall)
              Text("• Articles : +${report.productsAdded} ajoutés, ${report.productsUpdated} mis à jour", style = MaterialTheme.typography.bodySmall)
              Text("• Demandes (DA) : +${report.daAdded} ajoutées, ${report.daUpdated} mises à jour", style = MaterialTheme.typography.bodySmall)
              Text("• Commandes (BC) : +${report.bcAdded} ajoutées, ${report.bcUpdated} mises à jour", style = MaterialTheme.typography.bodySmall)
              Text("• Réceptions & Factures : +${report.receptionsAdded + report.invoicesAdded} traitées", style = MaterialTheme.typography.bodySmall)
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "✓ Enregistré automatiquement dans le Journal d'Audit Légal.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.dismissReport()
            Toast.makeText(context, "Données fusionnées dans l'application", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.testTag("btn_close_merge_report")
        ) {
          Text("Terminer")
        }
      }
    )
  }

  // Error Alert
  uiState.errorMessage?.let { error ->
    AlertDialog(
      onDismissRequest = { viewModel.clearError() },
      icon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
      title = { Text("Information") },
      text = { Text(error) },
      confirmButton = {
        TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
      }
    )
  }
}

@Composable
private fun MiniStat(
  icon: ImageVector,
  label: String,
  value: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
    Spacer(modifier = Modifier.height(2.dp))
    Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
  }
}
