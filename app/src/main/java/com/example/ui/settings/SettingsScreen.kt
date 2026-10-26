package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType
import com.example.core.utils.ExportService
import com.example.data.repository.SettingsRepository
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  settingsRepository: SettingsRepository,
  supplierRepository: SupplierRepository,
  onNavigateBack: () -> Unit,
  onNavigateToAudit: () -> Unit,
  onNavigateToProfile: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val settings by settingsRepository.getSettings().collectAsStateWithLifecycle(
    initialValue = com.example.data.local.entities.SettingsEntity()
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Paramètres & Configuration",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
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
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Feature Flag: Module Magasin / Entrepôt
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
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
              Icon(
                Icons.Default.Warehouse,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  "Module Magasin / Stock",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  if (settings.isWarehouseModuleEnabled) "Activé (Entrepôt disponible)" else "Désactivé par défaut (Achats directs chantier)",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Switch(
              checked = settings.isWarehouseModuleEnabled,
              onCheckedChange = { enabled ->
                scope.launch {
                  settingsRepository.toggleWarehouseModule(enabled)
                  val msg = if (enabled) "Module Magasin activé." else "Module Magasin désactivé. Livraisons directes chantier."
                  Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
              }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          OutlinedCard(
            colors = CardDefaults.outlinedCardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
          ) {
            Text(
              text = if (settings.isWarehouseModuleEnabled) {
                "ℹ️ Le module Magasin est ACTIF : les réceptions peuvent être affectées soit directement à un Projet/Chantier, soit stockées en Magasin avec suivi CUMP."
              } else {
                "ℹ️ Principe n°1 appliqué : Achats dirigés directement vers les projets/chantiers. Aucun écran de stock n'est affiché. Les tables et données restent prêtes pour activation future sans migration."
              },
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }

      // 1b. Profil Acheteur & Compte Gmail
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Profil Acheteur & Compte Google / Gmail",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          Text(
            "Identité de l'acheteur actif : ${settings.currentUserName} (${settings.currentUserEmail})",
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            "Titre : ${settings.currentUserRole} • Tél : ${settings.currentUserPhone}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          FilledTonalButton(
            onClick = onNavigateToProfile,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Gérer le Profil Acheteur & Paramètres Gmail")
          }
        }
      }

      // 2. Profil Entreprise (Algérie)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Profil Entreprise & Facturation",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Text("Raison Sociale : ${settings.companyName}", style = MaterialTheme.typography.bodyMedium)
          Text("NIF (15 ch.) : ${settings.companyNif}", style = MaterialTheme.typography.bodyMedium)
          Text("NIS : ${settings.companyNis}", style = MaterialTheme.typography.bodyMedium)
          Text("RC : ${settings.companyRc}", style = MaterialTheme.typography.bodyMedium)
          Text("Article d'imposition : ${settings.companyAi}", style = MaterialTheme.typography.bodyMedium)
          Text("Banque & RIB : ${settings.companyBank} - ${settings.companyRib}", style = MaterialTheme.typography.bodyMedium)
          Text("Adresse : ${settings.companyAddress}", style = MaterialTheme.typography.bodyMedium)
        }
      }

      // 3. Immutabilité & Audit Log
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Intégrité des Données & Piste d'Audit",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          Text(
            "Toutes les créations, modifications, changements de statut et archivages sont consignés de manière immuable et horodatée.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = onNavigateToAudit,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Consulter le Journal d'Audit Immuable")
          }
        }
      }

      // 4. Export Global de Toutes les Données
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              "Export Global de Sauvegarde",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          Text(
            "Exportez l'intégralité du référentiel fournisseurs, coordonnées bancaires, documents et piste d'audit dans un fichier universel (CSV / Excel).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          FilledTonalButton(
            onClick = {
              scope.launch {
                val suppliers = supplierRepository.getAllSuppliersIncludingArchived().first()
                val headers = listOf("ID", "Raison Sociale", "Forme Juridique", "NIF", "NIS", "RC", "AI", "Statut", "Wilaya", "Téléphone", "Note")
                val csv = ExportService.generateCsv(headers, suppliers) {
                  listOf(it.id, it.raisonSociale, it.formeJuridique, it.nif, it.nis, it.rcNumber, it.articleImposition, it.status, it.wilayaName, it.phone, it.overallRating.toString())
                }
                ExportService.shareExport(context, "Export_Global_AcheteurDZ.csv", csv, "text/csv")
              }
            },
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exporter toutes les données (CSV / Excel)")
          }
        }
      }
    }
  }
}
