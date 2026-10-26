package com.example.ui.suppliers

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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AppDatePicker
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.core.utils.AlgerianData
import com.example.core.utils.AlgerianFiscalValidator
import com.example.data.local.entities.SupplierEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierEditScreen(
  supplierId: String?,
  viewModel: SupplierViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val existingSupplier by if (supplierId != null) {
    viewModel.getSupplier(supplierId).collectAsStateWithLifecycle(initialValue = null)
  } else {
    remember { mutableStateOf(null) }
  }

  var raisonSociale by remember { mutableStateOf("") }
  var formeJuridique by remember { mutableStateOf("SARL") }
  var activite by remember { mutableStateOf("") }
  var categorie by remember { mutableStateOf(AlgerianData.CATEGORIES.first()) }
  var adresse by remember { mutableStateOf("") }
  var selectedWilaya by remember { mutableStateOf(AlgerianData.WILAYAS.find { it.code == "16" }!!) }
  var commune by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var website by remember { mutableStateOf("") }
  var contactPerson by remember { mutableStateOf("") }

  // Legal & Fiscal
  var rcNumber by remember { mutableStateOf("") }
  var rcDate by remember { mutableStateOf("") }
  var nif by remember { mutableStateOf("") }
  var nis by remember { mutableStateOf("") }
  var articleImposition by remember { mutableStateOf("") }
  var carteArtisanNin by remember { mutableStateOf("") }

  // Terms
  var paymentTermsDays by remember { mutableIntStateOf(30) }
  var leadTimeDays by remember { mutableIntStateOf(7) }
  var deliveryZones by remember { mutableStateOf("Toutes wilayas") }

  // Validation Errors
  var nameError by remember { mutableStateOf<String?>(null) }
  var nifError by remember { mutableStateOf<String?>(null) }
  var phoneError by remember { mutableStateOf<String?>(null) }

  // Load existing supplier values when available
  LaunchedEffect(existingSupplier) {
    existingSupplier?.let { s ->
      raisonSociale = s.raisonSociale
      formeJuridique = s.formeJuridique
      activite = s.activite
      adresse = s.adresse
      AlgerianData.WILAYAS.find { it.code == s.wilayaCode }?.let { selectedWilaya = it }
      commune = s.commune
      phone = s.phone
      email = s.email
      website = s.website
      contactPerson = s.contactPerson
      rcNumber = s.rcNumber
      rcDate = s.rcDate
      nif = s.nif
      nis = s.nis
      articleImposition = s.articleImposition
      carteArtisanNin = s.carteArtisanNin
      paymentTermsDays = s.paymentTermsDays
      leadTimeDays = s.leadTimeDays
      deliveryZones = s.deliveryZones
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            if (supplierId == null) "Nouveau Fournisseur" else "Modifier le Fournisseur",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          Button(
            onClick = {
              // Validations
              var valid = true
              if (raisonSociale.isBlank()) {
                nameError = "La raison sociale est obligatoire"
                valid = false
              }
              if (nif.isNotBlank() && !AlgerianFiscalValidator.validateNif(nif)) {
                nifError = "Le NIF algérien doit contenir exactement 15 chiffres"
                valid = false
              }
              if (phone.isNotBlank() && !AlgerianFiscalValidator.validatePhone(phone)) {
                phoneError = "Format téléphone invalide (Ex: 023..., 05..., 06..., 07...)"
                valid = false
              }

              if (valid) {
                val supplierToSave = (existingSupplier ?: SupplierEntity(raisonSociale = raisonSociale)).copy(
                  raisonSociale = raisonSociale.trim(),
                  formeJuridique = formeJuridique,
                  activite = activite.trim(),
                  categoriesTags = categorie,
                  adresse = adresse.trim(),
                  wilayaCode = selectedWilaya.code,
                  wilayaName = selectedWilaya.nameFr,
                  commune = commune.trim(),
                  phone = phone.trim(),
                  email = email.trim(),
                  website = website.trim(),
                  contactPerson = contactPerson.trim(),
                  rcNumber = rcNumber.trim(),
                  rcDate = rcDate.trim(),
                  nif = nif.replace(" ", "").trim(),
                  nis = nis.replace(" ", "").trim(),
                  articleImposition = articleImposition.replace(" ", "").trim(),
                  carteArtisanNin = carteArtisanNin.trim(),
                  paymentTermsDays = paymentTermsDays,
                  leadTimeDays = leadTimeDays,
                  deliveryZones = deliveryZones.trim()
                )

                viewModel.saveSupplier(supplierToSave) {
                  Toast.makeText(context, "Fournisseur enregistré avec versioning et audit log", Toast.LENGTH_SHORT).show()
                  onNavigateBack()
                }
              }
            },
            modifier = Modifier.testTag("save_supplier_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Enregistrer")
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
      // 1. Raison Sociale & Forme Juridique
      FormSectionCard("1. Informations de l'Entreprise") {
        AppTextField(
          value = raisonSociale,
          onValueChange = {
            raisonSociale = it
            nameError = null
          },
          label = "Raison Sociale / Nom commercial *",
          placeholder = "Ex : SARL Algerian Industrial Supply",
          errorMessage = nameError,
          testTag = "input_supplier_name"
        )

        AppDropdown(
          label = "Forme Juridique (Algérie)",
          options = AlgerianData.LEGAL_FORMS.map { it.first },
          selectedOption = formeJuridique,
          onOptionSelected = { formeJuridique = it },
          optionToString = { code ->
            val desc = AlgerianData.LEGAL_FORMS.find { it.first == code }?.second ?: code
            "$code - $desc"
          }
        )

        AppTextField(
          value = activite,
          onValueChange = { activite = it },
          label = "Activité principale / Objet commercial",
          placeholder = "Ex : Fournitures industrielles, BTP, outillage..."
        )

        AppDropdown(
          label = "Catégorie principale",
          options = AlgerianData.CATEGORIES,
          selectedOption = categorie,
          onOptionSelected = { categorie = it }
        )
      }

      // 2. Fiscalité & Registres (Algérie)
      FormSectionCard("2. Identifiants Fiscaux & Réglementaires") {
        AppTextField(
          value = nif,
          onValueChange = {
            nif = it
            nifError = null
          },
          label = "NIF (Numéro d'Identification Fiscale - 15 chiffres)",
          placeholder = "Ex : 001916000987654",
          helperText = "Exactement 15 chiffres numériques",
          errorMessage = nifError,
          testTag = "input_supplier_nif"
        )

        AppTextField(
          value = nis,
          onValueChange = { nis = it },
          label = "NIS (Numéro d'Identification Statistique - 15 chiffres)",
          placeholder = "Ex : 001916000987654"
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          AppTextField(
            value = rcNumber,
            onValueChange = { rcNumber = it },
            label = "N° Registre de Commerce (RC)",
            placeholder = "Ex : 16/00-0987654B19",
            modifier = Modifier.weight(1.2f)
          )

          AppDatePicker(
            value = rcDate,
            onDateSelected = { rcDate = it },
            label = "Date délivrance",
            modifier = Modifier.weight(1f)
          )
        }

        AppTextField(
          value = articleImposition,
          onValueChange = { articleImposition = it },
          label = "Article d'imposition (AI)",
          placeholder = "Ex : 16024892150"
        )

        if (formeJuridique == "Auto-entrepreneur" || formeJuridique == "Personne physique") {
          AppTextField(
            value = carteArtisanNin,
            onValueChange = { carteArtisanNin = it },
            label = "NIN / Numéro Carte Artisan",
            placeholder = "Ex : NIN-2023-88741"
          )
        }
      }

      // 3. Localisation & Contact
      FormSectionCard("3. Localisation & Contact") {
        AppDropdown(
          label = "Wilaya (58 wilayas)",
          options = AlgerianData.WILAYAS,
          selectedOption = selectedWilaya,
          onOptionSelected = {
            selectedWilaya = it
            commune = it.communes.firstOrNull() ?: ""
          },
          optionToString = { "${it.code} - ${it.nameFr} (${it.nameAr})" }
        )

        AppTextField(
          value = commune,
          onValueChange = { commune = it },
          label = "Commune",
          placeholder = "Ex : Oued Smar, Chéraga, Bir El Djir..."
        )

        AppTextField(
          value = adresse,
          onValueChange = { adresse = it },
          label = "Adresse complète",
          placeholder = "Zone industrielle, lotissement, rue..."
        )

        AppTextField(
          value = contactPerson,
          onValueChange = { contactPerson = it },
          label = "Personne de contact / Interlocuteur",
          placeholder = "Ex : Karim Mansouri (Directeur Commercial)"
        )

        AppTextField(
          value = phone,
          onValueChange = {
            phone = it
            phoneError = null
          },
          label = "Téléphone",
          placeholder = "Ex : 023924510 ou 0550123456",
          errorMessage = phoneError,
          testTag = "input_supplier_phone"
        )

        AppTextField(
          value = email,
          onValueChange = { email = it },
          label = "Email professionnel",
          placeholder = "contact@entreprise.dz"
        )

        AppTextField(
          value = website,
          onValueChange = { website = it },
          label = "Site Web",
          placeholder = "www.entreprise.dz"
        )
      }

      // 4. Conditions Commerciales
      FormSectionCard("4. Conditions Commerciales") {
        AppDropdown(
          label = "Modalités de paiement habituelles",
          options = listOf(0, 15, 30, 45, 60, 90),
          selectedOption = paymentTermsDays,
          onOptionSelected = { paymentTermsDays = it },
          optionToString = { if (it == 0) "Paiement Comptant (Chèque/Virement)" else "$it jours net" }
        )

        AppDropdown(
          label = "Délai moyen d'approvisionnement",
          options = listOf(1, 2, 3, 5, 7, 10, 14, 21, 30),
          selectedOption = leadTimeDays,
          onOptionSelected = { leadTimeDays = it },
          optionToString = { "$it jours" }
        )

        AppTextField(
          value = deliveryZones,
          onValueChange = { deliveryZones = it },
          label = "Zones & wilayas de livraison",
          placeholder = "Ex : Alger, Blida, Centre, National..."
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun FormSectionCard(title: String, content: @Composable () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      content()
    }
  }
}
