package com.example.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet4Bar
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.SettingsEntity
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
  settingsRepository: SettingsRepository,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val settings by settingsRepository.getSettings().collectAsStateWithLifecycle(
    initialValue = SettingsEntity()
  )

  var userName by remember { mutableStateOf(settings.currentUserName) }
  var userRole by remember { mutableStateOf(settings.currentUserRole) }
  var userEmail by remember { mutableStateOf(settings.currentUserEmail) }
  var userPhone by remember { mutableStateOf(settings.currentUserPhone) }
  var companyName by remember { mutableStateOf(settings.companyName) }
  var signature by remember { mutableStateOf(settings.gmailSignature) }

  var showGoogleSwitchDialog by remember { mutableStateOf(false) }

  // Sync state whenever settings change
  LaunchedEffect(settings) {
    userName = settings.currentUserName
    userRole = settings.currentUserRole
    userEmail = settings.currentUserEmail
    userPhone = settings.currentUserPhone
    companyName = settings.companyName
    signature = settings.gmailSignature
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Mon Compte & Profil Acheteur",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("user_profile_back_button")) {
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

      // 1. Google Account & Buyer Header Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_profile_google_card"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Avatar badge with Google G styling
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userName.take(2).uppercase().ifEmpty { "DZ" },
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.onPrimary
                )
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = userName.ifBlank { "Acheteur Principal" },
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Compte Vérifié",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = userEmail.ifBlank { "abdobigboss30@gmail.com" },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = userRole.ifBlank { "Responsable des Achats & Approvisionnements" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(12.dp))

          // Status & Capability Tags
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            SuggestionChip(
              onClick = {},
              label = { Text("Gmail Prêt", style = MaterialTheme.typography.labelSmall) },
              icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp)) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surface
              )
            )
            SuggestionChip(
              onClick = {},
              label = { Text("Gemini IA Actif", style = MaterialTheme.typography.labelSmall) },
              icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(14.dp)) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surface
              )
            )
            SuggestionChip(
              onClick = {},
              label = { Text("100% Hors-ligne", style = MaterialTheme.typography.labelSmall) },
              icon = { Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(14.dp)) },
              colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.surface
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { showGoogleSwitchDialog = true },
              modifier = Modifier
                .weight(1f)
                .testTag("user_profile_switch_google_button"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Changer de Gmail", fontSize = 13.sp)
            }

            FilledTonalButton(
              onClick = {
                sendTestPurchaseOrderEmail(context, userEmail, userName, companyName, signature)
              },
              modifier = Modifier
                .weight(1.3f)
                .testTag("user_profile_test_email_button"),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Tester l'envoi Gmail", fontSize = 13.sp)
            }
          }
        }
      }

      // 2. Offline Resilience & Algerian Construction Site Guarantee
      OutlinedCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_profile_offline_resilience_card"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Résilience Chantier & Données Locales",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Votre profil acheteur et compte Gmail sont enregistrés localement. Vous pouvez créer, valider et modifier vos commandes (DA, BC, Factures) même dans les zones isolées sans réseau 3G/4G.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 3. Buyer Profile Form
      Text(
        text = "COORDONNÉES DE L'ACHETEUR",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
      )

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_profile_form_card"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Nom et Prénom de l'acheteur") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_name_input")
          )

          OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Adresse Gmail professionnelle") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_email_input")
          )

          OutlinedTextField(
            value = userRole,
            onValueChange = { userRole = it },
            label = { Text("Intitulé du Poste / Rôle") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_role_input")
          )

          OutlinedTextField(
            value = userPhone,
            onValueChange = { userPhone = it },
            label = { Text("Numéro de Téléphone direct") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_phone_input")
          )

          OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text("Société / Direction de rattachement") },
            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_company_input")
          )

          OutlinedTextField(
            value = signature,
            onValueChange = { signature = it },
            label = { Text("Signature automatique des Bons de Commande") },
            maxLines = 3,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_signature_input")
          )

          Spacer(modifier = Modifier.height(4.dp))

          Button(
            onClick = {
              scope.launch {
                settingsRepository.updateBuyerProfile(
                  userName = userName.trim(),
                  userRole = userRole.trim(),
                  userEmail = userEmail.trim(),
                  userPhone = userPhone.trim(),
                  gmailSignature = signature.trim(),
                  companyName = companyName.trim()
                )
                Toast.makeText(context, "Profil acheteur mis à jour avec succès !", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("user_profile_save_button"),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enregistrer les Coordonnées")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Switch Gmail Account Dialog
  if (showGoogleSwitchDialog) {
    var dialogEmail by remember { mutableStateOf(userEmail) }
    var dialogName by remember { mutableStateOf(userName) }

    AlertDialog(
      onDismissRequest = { showGoogleSwitchDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Connecter un compte Gmail")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            "Indiquez l'adresse Gmail à associer à vos bons de commande et demandes d'achats :",
            style = MaterialTheme.typography.bodyMedium
          )
          OutlinedTextField(
            value = dialogEmail,
            onValueChange = { dialogEmail = it },
            label = { Text("Compte Gmail") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = dialogName,
            onValueChange = { dialogName = it },
            label = { Text("Nom d'affichage") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            "ℹ️ Toutes les fonctions d'export d'ordres d'achat et le scanner Gemini 2.5 Flash utiliseront ce compte.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            scope.launch {
              settingsRepository.setGoogleLinked(
                isLinked = true,
                email = dialogEmail.trim(),
                displayName = dialogName.trim()
              )
              showGoogleSwitchDialog = false
              Toast.makeText(context, "Compte Gmail associé : $dialogEmail", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("Valider la Connexion")
        }
      },
      dismissButton = {
        TextButton(onClick = { showGoogleSwitchDialog = false }) {
          Text("Annuler")
        }
      }
    )
  }
}

/**
 * Helper to dispatch an email via Android native Gmail client / ACTION_SENDTO.
 */
private fun sendTestPurchaseOrderEmail(
  context: Context,
  buyerEmail: String,
  buyerName: String,
  companyName: String,
  signature: String
) {
  val subject = "[Acheteur DZ] Test d'envoi Bon de Commande par Gmail"
  val body = """
    Bonjour,

    Ceci est un test de transmission d'un bon de commande depuis l'application Acheteur DZ (Procure-to-Pay BTP Algérie).

    Détails de l'Acheteur :
    • Responsable : $buyerName
    • Email Gmail : $buyerEmail
    • Entreprise : $companyName

    Les services Google (Gemini IA 2.5 Flash, messagerie Gmail, rapports d'approvisionnement) sont configurés et opérationnels en mode hors-ligne résilient.

    Cordialement,
    $signature
  """.trimIndent()

  val intent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(buyerEmail))
    putExtra(Intent.EXTRA_SUBJECT, subject)
    putExtra(Intent.EXTRA_TEXT, body)
  }

  try {
    context.startActivity(Intent.createChooser(intent, "Envoyer via Gmail / Email"))
  } catch (e: Exception) {
    Toast.makeText(context, "Aucune application de messagerie trouvée : ${e.message}", Toast.LENGTH_LONG).show()
  }
}
