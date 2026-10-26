package com.example.ui.products

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.AppTextField
import com.example.core.utils.AlgerianData
import com.example.data.local.entities.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
  viewModel: ProductViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val suppliers by viewModel.suppliers.collectAsStateWithLifecycle(initialValue = emptyList())

  var reference by remember { mutableStateOf("") }
  var designationFr by remember { mutableStateOf("") }
  var designationAr by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(AlgerianData.CATEGORIES.first()) }
  var unit by remember { mutableStateOf("U") }
  var tvaRate by remember { mutableDoubleStateOf(19.0) }
  var priceStr by remember { mutableStateOf("") }
  var selectedSupplierId by remember { mutableStateOf<String?>(null) }

  var refError by remember { mutableStateOf<String?>(null) }
  var desigError by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Nouvel Article / Marchandise", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          Button(onClick = {
            var valid = true
            if (reference.isBlank()) {
              refError = "La référence est requise"
              valid = false
            }
            if (designationFr.isBlank()) {
              desigError = "La désignation est obligatoire"
              valid = false
            }

            if (valid) {
              val supplierObj = suppliers.find { it.id == selectedSupplierId }
              val newProduct = ProductEntity(
                reference = reference.trim(),
                designationFr = designationFr.trim(),
                designationAr = designationAr.trim(),
                category = category,
                unit = unit,
                tvaRate = tvaRate,
                defaultSupplierId = selectedSupplierId,
                defaultSupplierName = supplierObj?.raisonSociale ?: "",
                lastPriceDzd = priceStr.toDoubleOrNull() ?: 0.0,
                avgPriceDzd = priceStr.toDoubleOrNull() ?: 0.0
              )
              viewModel.saveProduct(newProduct) {
                Toast.makeText(context, "Article ajouté au catalogue", Toast.LENGTH_SHORT).show()
                onNavigateBack()
              }
            }
          }) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Enregistrer")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
          Text("Description de l'Article", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

          AppTextField(
            value = reference,
            onValueChange = {
              reference = it
              refError = null
            },
            label = "Référence Article / Code interne *",
            placeholder = "Ex : MAT-CIM-001, OUT-MEUL-230",
            errorMessage = refError
          )

          AppTextField(
            value = designationFr,
            onValueChange = {
              designationFr = it
              desigError = null
            },
            label = "Désignation en Français *",
            placeholder = "Ex : Ciment Portland CEM II 42.5 (Sac 50kg)",
            errorMessage = desigError
          )

          AppTextField(
            value = designationAr,
            onValueChange = { designationAr = it },
            label = "Désignation en Arabe (التسمية بالعربية)",
            placeholder = "Ex : إسمنت بورتلاند 42.5 كيس 50 كغ"
          )

          AppDropdown(
            label = "Catégorie",
            options = AlgerianData.CATEGORIES,
            selectedOption = category,
            onOptionSelected = { category = it }
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AppDropdown(
              label = "Unité",
              options = listOf("U", "Sac", "Tonne", "Mètre", "M²", "M³", "Litre", "Kg", "Forfait"),
              selectedOption = unit,
              onOptionSelected = { unit = it },
              modifier = Modifier.weight(1f)
            )

            AppDropdown(
              label = "Taux TVA Algérie",
              options = listOf(19.0, 9.0, 0.0),
              selectedOption = tvaRate,
              onOptionSelected = { tvaRate = it },
              optionToString = { "${it.toInt()}%" },
              modifier = Modifier.weight(1f)
            )
          }

          AppTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = "Prix Unitaire indicatif / Dernier prix (DA)",
            placeholder = "Ex : 650"
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
          Text("Fournisseur Attitré / Par Défaut", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

          AppDropdown(
            label = "Fournisseur sélectionné",
            options = suppliers,
            selectedOption = suppliers.find { it.id == selectedSupplierId },
            onOptionSelected = { selectedSupplierId = it.id },
            optionToString = { "${it.raisonSociale} (${it.wilayaName})" }
          )
        }
      }
    }
  }
}
