package com.example.ui.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.designsystem.components.AmountText
import com.example.core.designsystem.components.AppDropdown
import com.example.core.designsystem.components.StatusChip
import com.example.core.designsystem.components.StatusType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScannerScreen(
  viewModel: ReceiptScannerViewModel,
  onNavigateBack: () -> Unit,
  onInvoiceCreated: (String) -> Unit = {},
  onReceptionCreated: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  // Activity Result Launchers
  // 1. Direct camera preview capture (returns Bitmap directly)
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      viewModel.onImageCaptured(bitmap)
    }
  }

  // 2. Camera permission request
  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      cameraLauncher.launch(null)
    } else {
      Toast.makeText(context, "Permission caméra requise pour scanner un reçu", Toast.LENGTH_SHORT).show()
    }
  }

  // 3. Photo Picker (Android 13+ Photo Picker / standard media contract)
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val bitmap = loadBitmapFromUri(context, uri)
      if (bitmap != null) {
        viewModel.onImageCaptured(bitmap)
      } else {
        Toast.makeText(context, "Impossible de charger l'image sélectionnée", Toast.LENGTH_SHORT).show()
      }
    }
  }

  LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { error ->
      snackbarHostState.showSnackbar(error)
    }
  }

  LaunchedEffect(uiState.successMessage) {
    uiState.successMessage?.let { success ->
      snackbarHostState.showSnackbar(success)
      viewModel.clearSuccess()
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              "Scanner Reçu & Facture",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(Modifier.width(4.dp))
              Text(
                "Gemini Multimodal AI",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("scanner_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        actions = {
          if (uiState.imageBitmap != null) {
            IconButton(
              onClick = { viewModel.resetAll() },
              modifier = Modifier.testTag("scanner_reset_button")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "Réinitialiser")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Capture / Select Actions Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            "Capturer une facture papier ou reçu de caisse",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            "L'IA Gemini analyse automatiquement la photo et extrait le fournisseur, NIF, montants Hors Taxe, TVA 19%/9%, et total TTC en DA.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Button(
              onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                  cameraLauncher.launch(null)
                } else {
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("button_take_photo")
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Appareil photo")
            }

            OutlinedButton(
              onClick = {
                galleryLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              modifier = Modifier
                .weight(1f)
                .testTag("button_pick_gallery")
            ) {
              Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(Modifier.width(8.dp))
              Text("Galerie")
            }
          }
        }
      }

      // 2. Image Preview & Scanning Status
      if (uiState.imageBitmap != null) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.05f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                bitmap = uiState.imageBitmap!!.asImageBitmap(),
                contentDescription = "Aperçu du reçu scanné",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
              )

              if (uiState.isAnalyzing) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    CircularProgressIndicator(
                      color = MaterialTheme.colorScheme.primary,
                      strokeWidth = 3.dp,
                      modifier = Modifier.size(44.dp)
                    )
                    Text(
                      "Extraction IA Gemini en cours...",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = Color.White
                    )
                    Text(
                      "Lecture des montants DZD & NIF fiscal",
                      style = MaterialTheme.typography.bodySmall,
                      color = Color.White.copy(alpha = 0.8f)
                    )
                  }
                }
              }
            }

            if (!uiState.isAnalyzing && uiState.errorMessage != null) {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("scanner_error_banner")
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(Modifier.width(8.dp))
                  Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }

            if (!uiState.isAnalyzing && uiState.extractedData == null) {
              Button(
                onClick = { viewModel.analyzeReceipt() },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("button_retry_analysis")
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                  if (uiState.errorMessage != null) "Réessayer l'analyse avec Gemini" else "Lancer l'analyse avec Gemini 3.8 Flash"
                )
              }
            }
          }
        }
      }

      // 3. Extracted Results Display
      uiState.extractedData?.let { data ->
        // Supplier & Legal Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_extracted_supplier")
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "Fournisseur & Fiscalité",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              StatusChip(
                text = data.documentType,
                type = StatusType.ACTIVE
              )
            }

            Text(
              text = data.supplierName.ifBlank { "Fournisseur non spécifié" },
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )

            if (uiState.matchedSupplier != null) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                  "Correspond à : ${uiState.matchedSupplier!!.raisonSociale}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.weight(1f)) {
                Text("NIF :", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(data.nif.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
              }
              Column(modifier = Modifier.weight(1f)) {
                Text("RC :", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(data.rcNumber.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
              }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Article d'Imp. (AI) :", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(data.articleImposition.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text("Date document :", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(data.documentDate.ifBlank { "-" }, style = MaterialTheme.typography.bodyMedium)
              }
            }

            if (data.supplierAddress.isNotBlank()) {
              Text(
                "Adresse: ${data.supplierAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Financial Amounts Card (DZD)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("card_extracted_amounts")
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              "Montants Extraits (DZD)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Total Hors Taxe (HT) :", style = MaterialTheme.typography.bodyMedium)
              AmountText(amount = data.totalHtDzd, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("TVA (${data.tvaRate.toInt()}%) :", style = MaterialTheme.typography.bodyMedium)
              AmountText(amount = data.totalTvaDzd, style = MaterialTheme.typography.bodyMedium)
            }

            if (data.timbreFiscalDzd > 0) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Droit de Timbre :", style = MaterialTheme.typography.bodyMedium)
                AmountText(amount = data.timbreFiscalDzd, style = MaterialTheme.typography.bodyMedium)
              }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "Total TTC net à payer :",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              AmountText(
                amount = if (data.totalTtcDzd > 0) data.totalTtcDzd else (data.totalHtDzd + data.totalTvaDzd),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Text(
              "Mode de règlement : ${data.paymentMethod}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Extracted Items Card
        if (data.items.isNotEmpty()) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                "Lignes & Marchandises (${data.items.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              data.items.forEachIndexed { index, item ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      item.designation,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                      "Qté : ${item.quantity} ${item.unit} x ${item.unitPriceHt} DA",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  if (item.totalPriceHt > 0) {
                    AmountText(amount = item.totalPriceHt, style = MaterialTheme.typography.bodyMedium)
                  }
                }
                if (index < data.items.size - 1) {
                  HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                }
              }
            }
          }
        }

        // Project Assignment Card (PROJECT-DIRECTED PURCHASING)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(Modifier.width(8.dp))
              Text(
                "Chantier / Projet cible",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            Text(
              "Toute dépense est imputée directement à un chantier selon les règles de gestion du startup.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (uiState.projects.isNotEmpty()) {
              AppDropdown(
                label = "Sélectionner le chantier",
                options = uiState.projects,
                selectedOption = uiState.projects.firstOrNull { it.id == uiState.selectedProjectId }
                  ?: uiState.projects.firstOrNull(),
                onOptionSelected = { project -> viewModel.selectProject(project.id) },
                optionToString = { "${it.code} - ${it.name}" },
                testTag = "dropdown_project_target"
              )
            }
          }
        }

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              viewModel.saveAsSupplierInvoice { invoiceId ->
                onInvoiceCreated(invoiceId)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("button_save_invoice")
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Enregistrer comme Facture Fournisseur", fontWeight = FontWeight.Bold)
          }

          FilledTonalButton(
            onClick = {
              viewModel.saveAsSiteReception { receptionId ->
                onReceptionCreated(receptionId)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("button_save_reception")
          ) {
            Icon(Icons.Default.Description, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Créer Bon de Réception Chantier (BL)", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Safely decodes a Uri into a Bitmap compatible with Gemini multimodal processing,
 * with downsampling to avoid out-of-memory errors and excessive network payloads.
 */
private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
  return try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val source = ImageDecoder.createSource(context.contentResolver, uri)
      ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        decoder.isMutableRequired = true
        val maxDim = 1280
        val width = info.size.width
        val height = info.size.height
        if (width > maxDim || height > maxDim) {
          val ratio = width.toFloat() / height.toFloat()
          if (width > height) {
            decoder.setTargetSize(maxDim, (maxDim / ratio).toInt().coerceAtLeast(1))
          } else {
            decoder.setTargetSize((maxDim * ratio).toInt().coerceAtLeast(1), maxDim)
          }
        }
      }
    } else {
      @Suppress("DEPRECATION")
      val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
      if (bitmap != null && (bitmap.width > 1280 || bitmap.height > 1280)) {
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val tw = if (bitmap.width > bitmap.height) 1280 else (1280 * ratio).toInt().coerceAtLeast(1)
        val th = if (bitmap.width > bitmap.height) (1280 / ratio).toInt().coerceAtLeast(1) else 1280
        Bitmap.createScaledBitmap(bitmap, tw, th, true)
      } else {
        bitmap
      }
    }
  } catch (e: Exception) {
    null
  }
}
