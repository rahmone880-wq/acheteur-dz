package com.example.ui.projects

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.core.utils.AlgerianData
import com.example.data.local.entities.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(
  viewModel: ProjectViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  var code by remember { mutableStateOf("") }
  var name by remember { mutableStateOf("") }
  var client by remember { mutableStateOf("") }
  var selectedWilaya by remember { mutableStateOf(AlgerianData.WILAYAS.find { it.code == "16" }!!) }
  var commune by remember { mutableStateOf("") }
  var manager by remember { mutableStateOf("") }
  var costCenter by remember { mutableStateOf("") }
  var budgetStr by remember { mutableStateOf("") }

  var nameError by remember { mutableStateOf<String?>(null) }
  var codeError by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Nouveau Projet / Chantier Cible",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          Button(onClick = {
            var valid = true
            if (name.isBlank()) {
              nameError = "Le nom du projet est obligatoire"
              valid = false
            }
            if (code.isBlank()) {
              codeError = "Le code projet est requis (Ex: PRJ-2026-001)"
              valid = false
            }

            if (valid) {
              val newProject = ProjectEntity(
                code = code.trim(),
                name = name.trim(),
                client = client.trim(),
                locationWilaya = selectedWilaya.code,
                locationCommune = commune.trim(),
                costCenter = costCenter.trim(),
                manager = manager.trim(),
                totalBudgetDzd = budgetStr.toDoubleOrNull() ?: 0.0
              )
              viewModel.saveProject(newProject) {
                Toast.makeText(context, "Chantier enregistré avec succès", Toast.LENGTH_SHORT).show()
                onNavigateBack()
              }
            }
          }) {
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
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Identification du Projet", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

          AppTextField(
            value = code,
            onValueChange = {
              code = it
              codeError = null
            },
            label = "Code Projet / Référence *",
            placeholder = "Ex : PRJ-2026-ALG01",
            errorMessage = codeError
          )

          AppTextField(
            value = name,
            onValueChange = {
              name = it
              nameError = null
            },
            label = "Nom du Projet / Intitulé du Chantier *",
            placeholder = "Ex : Chantier Tour Résidentielle Rouïba",
            errorMessage = nameError
          )

          AppTextField(
            value = client,
            onValueChange = { client = it },
            label = "Maître d'ouvrage / Client",
            placeholder = "Ex : Entreprise / Direction / Promotion"
          )

          AppTextField(
            value = budgetStr,
            onValueChange = { budgetStr = it },
            label = "Budget Total Alloué aux Achats (DA)",
            placeholder = "Ex : 25000000"
          )
        }
      }

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Localisation & Affectation Chantier", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

          AppDropdown(
            label = "Wilaya d'implantation",
            options = AlgerianData.WILAYAS,
            selectedOption = selectedWilaya,
            onOptionSelected = {
              selectedWilaya = it
              commune = it.communes.firstOrNull() ?: ""
            },
            optionToString = { "${it.code} - ${it.nameFr}" }
          )

          AppTextField(
            value = commune,
            onValueChange = { commune = it },
            label = "Commune / Site de livraison",
            placeholder = "Ex : Rouïba, Bir El Djir, Hassi Messaoud..."
          )

          AppTextField(
            value = manager,
            onValueChange = { manager = it },
            label = "Responsable de Chantier / Réceptionnaire",
            placeholder = "Ex : Ingénieur Chef de Projet"
          )

          AppTextField(
            value = costCenter,
            onValueChange = { costCenter = it },
            label = "Code Centre de Coûts (Imputation)",
            placeholder = "Ex : CC-ALG-BTP-01"
          )
        }
      }
    }
  }
}
