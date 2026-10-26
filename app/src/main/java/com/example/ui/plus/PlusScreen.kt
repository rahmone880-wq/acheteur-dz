package com.example.ui.plus

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.skeletons.AchatsSkeleton
import com.example.ui.navigation.skeletons.DashboardSkeleton
import com.example.ui.navigation.skeletons.FournisseursSkeleton
import com.example.ui.navigation.skeletons.PlusSkeleton
import com.example.ui.navigation.skeletons.ProjetsSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusScreen(
  onNavigateToProfile: () -> Unit,
  onNavigateToProducts: () -> Unit,
  onNavigateToScanner: () -> Unit,
  onNavigateToAudit: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onNavigateToTransfer: () -> Unit,
  onNavigateToSkeleton: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showSkeletonPreview by remember { mutableStateOf(false) }
  var selectedSkeletonTab by remember { mutableStateOf("dashboard") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Plus d'Outils & Services",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "Catalogue, IA, audit et réglementation",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("plus_screen_list"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 0. Buyer Profile & Google Account Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("plus_buyer_profile_card")
            .clickable(onClick = onNavigateToProfile),
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "DZ",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimary
                )
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Profil Acheteur & Compte Gmail",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
              Text(
                text = "abdobigboss30@gmail.com • Actif & Vérifié",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Gérer coordonnées, signature et envois de commandes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Ouvrir le profil",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      // 1. Navigation Hub Cards
      item {
        Text(
          "MODULES COMPLÉMENTAIRES",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
      }

      item {
        PlusNavigationItem(
          title = "Catalogue Articles & Matériaux",
          description = "Gestion des articles BTP, références internes, unités et prix indicatifs HT en Dinars (DZD).",
          icon = Icons.Default.Category,
          badge = "Articles",
          testTag = "plus_menu_products",
          onClick = onNavigateToProducts
        )
      }

      item {
        PlusNavigationItem(
          title = "Scanner Reçus & Factures IA",
          description = "Capture photo ou import de facture avec extraction automatique par Google Gemini 2.5 Flash.",
          icon = Icons.Default.CameraAlt,
          badge = "Gemini IA",
          testTag = "plus_menu_scanner",
          onClick = onNavigateToScanner
        )
      }

      item {
        PlusNavigationItem(
          title = "Journal d'Audit Légal (Piste d'Audit)",
          description = "Historique immuable de toutes les actions, approbations de DA/BC et créations de tiers.",
          icon = Icons.Default.History,
          badge = "Conformité",
          testTag = "plus_menu_audit",
          onClick = onNavigateToAudit
        )
      }

      item {
        PlusNavigationItem(
          title = "Transfert & Fusion Mobile (P2P)",
          description = "Partager ou recevoir des données entre téléphones sans internet (Quick Share, WhatsApp, Fichier) avec fusion intelligente.",
          icon = Icons.Default.PhoneAndroid,
          badge = "P2P / Hors-ligne",
          testTag = "plus_menu_transfer",
          onClick = onNavigateToTransfer
        )
      }

      item {
        PlusNavigationItem(
          title = "Paramètres de l'Entreprise & Fiscalité",
          description = "Configuration des identifiants légaux (NIF, NIS, RC, AI), seuil de paiement et taux TVA (19%).",
          icon = Icons.Default.Settings,
          badge = "Société",
          testTag = "plus_menu_settings",
          onClick = onNavigateToSettings
        )
      }

      // 2. Interactive Skeletons Section
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          "SQUELETTES D'ÉCRANS (MODULE SKELETONS)",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("plus_skeleton_toggle_card"),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ),
          shape = MaterialTheme.shapes.medium
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Layers,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    "Inspecter les Squelettes Vides",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                  )
                  Text(
                    "Aperçu des écrans vides pour les 5 onglets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chips to select skeleton module
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(
                "dashboard" to "Dashboard",
                "achats" to "Achats",
                "fournisseurs" to "Fournisseurs",
                "projets" to "Projets",
                "plus" to "Plus"
              ).forEach { (key, label) ->
                FilterChip(
                  selected = showSkeletonPreview && selectedSkeletonTab == key,
                  onClick = {
                    if (showSkeletonPreview && selectedSkeletonTab == key) {
                      showSkeletonPreview = false
                    } else {
                      selectedSkeletonTab = key
                      showSkeletonPreview = true
                    }
                  },
                  label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                  modifier = Modifier.testTag("skeleton_chip_$key")
                )
              }
            }
          }
        }
      }

      if (showSkeletonPreview) {
        item {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("embedded_skeleton_preview_card"),
            shape = MaterialTheme.shapes.large
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                "Aperçu du Squelette : ${selectedSkeletonTab.uppercase()}",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
              )

              when (selectedSkeletonTab) {
                "dashboard" -> DashboardSkeleton()
                "achats" -> AchatsSkeleton()
                "fournisseurs" -> FournisseursSkeleton()
                "projets" -> ProjetsSkeleton()
                "plus" -> PlusSkeleton()
                else -> DashboardSkeleton()
              }
            }
          }
        }
      }

      // 3. Algerian Procurement Regulatory Reminder
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          "CADRE LÉGAL & FISCAL ALGÉRIEN",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("plus_legal_compliance_card"),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
          ),
          shape = MaterialTheme.shapes.large
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                "Règles Clés des Achats en Algérie",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LegalBulletPoint(
              title = "Paiement en espèces plafonné à 500 000 DZD",
              description = "Tout achat supérieur ou égal à 500 000 DZD TTC doit obligatoirement être réglé par chèque, virement ou effet bancaire."
            )
            Spacer(modifier = Modifier.height(8.dp))
            LegalBulletPoint(
              title = "Identifiants obligatoires sur factures",
              description = "Chaque fournisseur doit mentionner NIF (15 chiffres), NIS, Registre de Commerce et Article d'Imposition."
            )
            Spacer(modifier = Modifier.height(8.dp))
            LegalBulletPoint(
              title = "Droit de timbre fiscal (1%)",
              description = "Applicable aux règlements d'espèces entre 2 500 DZD et 100 000 DZD (minimum 50 DZD, plafonné à 2 500 DZD)."
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun PlusNavigationItem(
  title: String,
  description: String,
  icon: ImageVector,
  badge: String,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag)
      .clickable(onClick = onClick),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Ouvrir",
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun LegalBulletPoint(
  title: String,
  description: String
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(
      "•",
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(end = 8.dp)
    )
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
