package com.example.ui.transfer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.transfer.DataTransferManager
import com.example.core.transfer.MergeReport
import com.example.core.transfer.TransferPackage
import com.example.core.transfer.TransferScope
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

data class TransferUiState(
  val selectedScope: TransferScope = TransferScope.FULL,
  val isExporting: Boolean = false,
  val isImporting: Boolean = false,
  val previewPackage: TransferPackage? = null,
  val mergeReport: MergeReport? = null,
  val errorMessage: String? = null,
  val localSuppliersCount: Int = 0,
  val localProjectsCount: Int = 0,
  val localOrdersCount: Int = 0,
  val localProductsCount: Int = 0
)

class DataTransferViewModel(private val database: AppDatabase) : ViewModel() {

  private val _uiState = MutableStateFlow(TransferUiState())
  val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

  init {
    loadLocalCounts()
  }

  fun loadLocalCounts() {
    viewModelScope.launch {
      try {
        val suppliers = database.supplierDao().getAllSuppliersDirect().size
        val projects = database.projectDao().getAllProjectsDirect().size
        val orders = database.procurementDao().getAllPurchaseOrdersDirect().size
        val products = database.productDao().getAllProductsDirect().size

        _uiState.update {
          it.copy(
            localSuppliersCount = suppliers,
            localProjectsCount = projects,
            localOrdersCount = orders,
            localProductsCount = products
          )
        }
      } catch (_: Exception) {}
    }
  }

  fun setScope(scope: TransferScope) {
    _uiState.update { it.copy(selectedScope = scope) }
  }

  fun exportAndShare(context: Context) {
    viewModelScope.launch {
      _uiState.update { it.copy(isExporting = true, errorMessage = null) }
      try {
        val pkg = DataTransferManager.buildExportPackage(database, _uiState.value.selectedScope)
        DataTransferManager.sharePackage(context, pkg)
      } catch (e: Exception) {
        _uiState.update { it.copy(errorMessage = "Erreur lors de l'export : ${e.message}") }
      } finally {
        _uiState.update { it.copy(isExporting = false) }
      }
    }
  }

  fun parseFileUri(context: Context, uri: Uri) {
    viewModelScope.launch {
      _uiState.update { it.copy(isImporting = true, errorMessage = null) }
      try {
        val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
          BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
        } ?: throw IllegalArgumentException("Impossible de lire le fichier sélectionné.")

        val pkg = DataTransferManager.jsonToPackage(content)
        _uiState.update { it.copy(previewPackage = pkg) }
      } catch (e: Exception) {
        _uiState.update { it.copy(errorMessage = "Échec de lecture du fichier : ${e.localizedMessage}") }
      } finally {
        _uiState.update { it.copy(isImporting = false) }
      }
    }
  }

  fun parsePastedText(text: String) {
    if (text.isBlank()) return
    try {
      val pkg = DataTransferManager.jsonToPackage(text.trim())
      _uiState.update { it.copy(previewPackage = pkg, errorMessage = null) }
    } catch (e: Exception) {
      _uiState.update { it.copy(errorMessage = "Code de transfert invalide : ${e.localizedMessage}") }
    }
  }

  fun confirmMerge(currentUser: String = "Acheteur") {
    val pkg = _uiState.value.previewPackage ?: return
    viewModelScope.launch {
      _uiState.update { it.copy(isImporting = true, errorMessage = null) }
      try {
        val report = DataTransferManager.mergePackage(database, pkg, currentUser)
        _uiState.update {
          it.copy(
            mergeReport = report,
            previewPackage = null
          )
        }
        loadLocalCounts()
      } catch (e: Exception) {
        _uiState.update { it.copy(errorMessage = "Erreur lors de la fusion : ${e.message}") }
      } finally {
        _uiState.update { it.copy(isImporting = false) }
      }
    }
  }

  fun dismissPreview() {
    _uiState.update { it.copy(previewPackage = null) }
  }

  fun dismissReport() {
    _uiState.update { it.copy(mergeReport = null) }
  }

  fun clearError() {
    _uiState.update { it.copy(errorMessage = null) }
  }
}
