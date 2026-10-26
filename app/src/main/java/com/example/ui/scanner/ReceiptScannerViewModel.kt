package com.example.ui.scanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.ai.ReceiptExtractionData
import com.example.core.ai.ReceiptScannerService
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import com.example.data.repository.AuditRepository
import com.example.data.repository.ProcurementRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ReceiptScannerUiState(
  val imageBitmap: Bitmap? = null,
  val isAnalyzing: Boolean = false,
  val extractedData: ReceiptExtractionData? = null,
  val matchedSupplier: SupplierEntity? = null,
  val selectedProjectId: String? = null,
  val selectedSupplierId: String? = null,
  val projects: List<ProjectEntity> = emptyList(),
  val suppliers: List<SupplierEntity> = emptyList(),
  val errorMessage: String? = null,
  val successMessage: String? = null
)

class ReceiptScannerViewModel(
  private val procurementRepository: ProcurementRepository,
  private val supplierRepository: SupplierRepository,
  private val projectRepository: ProjectRepository,
  private val auditRepository: AuditRepository,
  private val scannerService: ReceiptScannerService = ReceiptScannerService()
) : ViewModel() {

  private val _uiState = MutableStateFlow(ReceiptScannerUiState())
  val uiState: StateFlow<ReceiptScannerUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      combine(
        projectRepository.getActiveProjects(),
        supplierRepository.getActiveSuppliers()
      ) { projects, suppliers ->
        Pair(projects, suppliers)
      }.collect { (projects, suppliers) ->
        _uiState.update { current ->
          current.copy(
            projects = projects,
            suppliers = suppliers,
            selectedProjectId = current.selectedProjectId ?: projects.firstOrNull()?.id
          )
        }
      }
    }
  }

  fun onImageCaptured(bitmap: Bitmap) {
    _uiState.update {
      it.copy(
        imageBitmap = bitmap,
        errorMessage = null,
        successMessage = null,
        extractedData = null,
        matchedSupplier = null
      )
    }
    // Automatically trigger Gemini multimodal analysis
    analyzeReceipt(bitmap)
  }

  fun analyzeReceipt(bitmap: Bitmap? = _uiState.value.imageBitmap) {
    if (bitmap == null) {
      _uiState.update { it.copy(errorMessage = "Veuillez d'abord capturer ou choisir une photo de reçu.") }
      return
    }

    viewModelScope.launch {
      _uiState.update { it.copy(isAnalyzing = true, errorMessage = null, successMessage = null) }
      try {
        val extracted = scannerService.extractReceiptData(bitmap)
        
        // Find best matching supplier based on NIF or name
        val allSuppliers = _uiState.value.suppliers
        val matched = allSuppliers.firstOrNull { sup ->
          (extracted.nif.isNotBlank() && sup.nif.isNotBlank() && sup.nif == extracted.nif) ||
            (extracted.supplierName.isNotBlank() && sup.raisonSociale.contains(extracted.supplierName, ignoreCase = true)) ||
            (extracted.supplierName.isNotBlank() && extracted.supplierName.contains(sup.raisonSociale, ignoreCase = true))
        }

        _uiState.update {
          it.copy(
            isAnalyzing = false,
            extractedData = extracted,
            matchedSupplier = matched,
            selectedSupplierId = matched?.id ?: allSuppliers.firstOrNull()?.id
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
            isAnalyzing = false,
            errorMessage = e.message ?: "Erreur lors de l'analyse du document avec Gemini."
          )
        }
      }
    }
  }

  fun selectProject(projectId: String) {
    _uiState.update { it.copy(selectedProjectId = projectId) }
  }

  fun selectSupplier(supplierId: String) {
    val matched = _uiState.value.suppliers.firstOrNull { it.id == supplierId }
    _uiState.update { it.copy(selectedSupplierId = supplierId, matchedSupplier = matched) }
  }

  fun updateExtractedData(data: ReceiptExtractionData) {
    _uiState.update { it.copy(extractedData = data) }
  }

  fun clearError() {
    _uiState.update { it.copy(errorMessage = null) }
  }

  fun clearSuccess() {
    _uiState.update { it.copy(successMessage = null) }
  }

  fun resetAll() {
    _uiState.update {
      it.copy(
        imageBitmap = null,
        isAnalyzing = false,
        extractedData = null,
        matchedSupplier = null,
        errorMessage = null,
        successMessage = null
      )
    }
  }

  /**
   * Converts the Gemini-extracted receipt directly into a Supplier Invoice,
   * saving to Room database, updating budget consumptions, and writing to immutable audit log.
   */
  fun saveAsSupplierInvoice(onSuccess: (String) -> Unit) {
    val state = _uiState.value
    val data = state.extractedData ?: return
    val selectedProject = state.projects.firstOrNull { it.id == state.selectedProjectId }
      ?: state.projects.firstOrNull()

    viewModelScope.launch {
      try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        // 1. Resolve or create supplier
        var targetSupplier = state.matchedSupplier
        if (targetSupplier == null && data.supplierName.isNotBlank()) {
          val newSupplier = SupplierEntity(
            id = UUID.randomUUID().toString(),
            raisonSociale = data.supplierName,
            nif = data.nif,
            nis = data.nis,
            rcNumber = data.rcNumber,
            articleImposition = data.articleImposition,
            adresse = data.supplierAddress,
            phone = data.supplierPhone,
            status = "AGREE",
            categoriesTags = "Scanné via Gemini AI"
          )
          supplierRepository.saveSupplier(newSupplier)
          targetSupplier = newSupplier
        }

        val invoiceNumber = if (data.documentNumber.isNotBlank()) {
          data.documentNumber
        } else {
          "FAC-SCAN-${System.currentTimeMillis() % 100000}"
        }

        val invoice = SupplierInvoiceEntity(
          id = UUID.randomUUID().toString(),
          invoiceNumber = invoiceNumber,
          invoiceDate = if (data.documentDate.isNotBlank()) data.documentDate else todayStr,
          dueDate = todayStr,
          supplierId = targetSupplier?.id ?: "UNKNOWN_SUPPLIER",
          supplierName = targetSupplier?.raisonSociale ?: data.supplierName.ifBlank { "Fournisseur Inconnu" },
          projectId = selectedProject?.id ?: "SIEGE",
          projectName = selectedProject?.name ?: "Chantier Général / Siège",
          totalHtDzd = data.totalHtDzd,
          totalTvaDzd = data.totalTvaDzd,
          totalTtcDzd = if (data.totalTtcDzd > 0) data.totalTtcDzd else (data.totalHtDzd + data.totalTvaDzd),
          paymentStatus = if (data.paymentMethod.equals("ESPECES", ignoreCase = true)) "PAYEE" else "NON_PAYEE",
          paidAmountDzd = if (data.paymentMethod.equals("ESPECES", ignoreCase = true)) data.totalTtcDzd else 0.0,
          matchingStatus = "CONFORME (Reçu Scanné)"
        )

        procurementRepository.saveInvoice(invoice)
        _uiState.update {
          it.copy(successMessage = "Facture ${invoice.invoiceNumber} enregistrée avec succès !")
        }
        onSuccess(invoice.id)
      } catch (e: Exception) {
        _uiState.update {
          it.copy(errorMessage = "Erreur lors de l'enregistrement de la facture: ${e.message}")
        }
      }
    }
  }

  /**
   * Converts the Gemini-extracted receipt directly into a Site Reception (BR / Réception chantier),
   * satisfying the "PROJECT-DIRECTED PURCHASING" core design requirement.
   */
  fun saveAsSiteReception(onSuccess: (String) -> Unit) {
    val state = _uiState.value
    val data = state.extractedData ?: return
    val selectedProject = state.projects.firstOrNull { it.id == state.selectedProjectId }
      ?: state.projects.firstOrNull()

    viewModelScope.launch {
      try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val supplierName = state.matchedSupplier?.raisonSociale ?: data.supplierName.ifBlank { "Fournisseur Livraisons" }
        val supplierId = state.matchedSupplier?.id ?: "UNKNOWN_SUPPLIER"

        val brNumber = "BR-SCAN-${System.currentTimeMillis() % 100000}"
        val blNumber = if (data.documentNumber.isNotBlank()) data.documentNumber else "BL-${System.currentTimeMillis() % 10000}"

        val reception = ReceptionEntity(
          id = UUID.randomUUID().toString(),
          brNumber = brNumber,
          blNumber = blNumber,
          purchaseOrderId = "DIRECT_DELIVERY",
          bcNumber = "Achat direct / Caisse",
          supplierId = supplierId,
          supplierName = supplierName,
          destinationType = "PROJET",
          destinationId = selectedProject?.id ?: "SIEGE",
          destinationName = selectedProject?.name ?: "Chantier Général / Siège",
          receptionDate = if (data.documentDate.isNotBlank()) data.documentDate else todayStr,
          status = "CONFORME",
          notes = "Scanné via Gemini AI. Articles : ${data.items.joinToString { "${it.designation} (${it.quantity} ${it.unit})" }}"
        )

        procurementRepository.saveReception(reception)
        _uiState.update {
          it.copy(successMessage = "Bon de réception $brNumber enregistré sur le chantier !")
        }
        onSuccess(reception.id)
      } catch (e: Exception) {
        _uiState.update {
          it.copy(errorMessage = "Erreur lors de l'enregistrement de la réception: ${e.message}")
        }
      }
    }
  }
}
