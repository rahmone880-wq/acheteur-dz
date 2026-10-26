package com.example.ui.suppliers

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.designsystem.components.ExportFormat
import com.example.core.utils.ExportService
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInteractionEntity
import com.example.data.repository.SupplierAccountStatement
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SupplierListUiState(
  val suppliers: List<SupplierEntity> = emptyList(),
  val filteredSuppliers: List<SupplierEntity> = emptyList(),
  val searchQuery: String = "",
  val statusFilter: String = "TOUS",
  val totalCount: Int = 0,
  val approvedCount: Int = 0,
  val pendingCount: Int = 0,
  val expiringDocsCount: Int = 0,
  val isLoading: Boolean = false
)

class SupplierViewModel(private val repository: SupplierRepository) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _statusFilter = MutableStateFlow("TOUS")
  private val _allSuppliers = repository.getActiveSuppliers()
  private val _expiringDocs = repository.getAllExpiringDocuments()

  val listUiState: StateFlow<SupplierListUiState> = combine(
    _allSuppliers,
    _searchQuery,
    _statusFilter,
    _expiringDocs
  ) { suppliers, query, filter, docs ->
    val filtered = suppliers.filter { s ->
      val matchesQuery = query.isBlank() ||
          s.raisonSociale.contains(query, ignoreCase = true) ||
          s.nif.contains(query) ||
          s.rcNumber.contains(query, ignoreCase = true) ||
          s.wilayaName.contains(query, ignoreCase = true) ||
          s.categoriesTags.contains(query, ignoreCase = true)

      val matchesStatus = when (filter) {
        "TOUS" -> true
        "AGREE" -> s.status == "AGREE"
        "EN_COURS" -> s.status == "EN_COURS"
        "SUSPENDU" -> s.status == "SUSPENDU"
        "BLACKLISTE" -> s.status == "BLACKLISTE"
        else -> true
      }

      matchesQuery && matchesStatus
    }

    SupplierListUiState(
      suppliers = suppliers,
      filteredSuppliers = filtered,
      searchQuery = query,
      statusFilter = filter,
      totalCount = suppliers.size,
      approvedCount = suppliers.count { it.status == "AGREE" },
      pendingCount = suppliers.count { it.status == "EN_COURS" },
      expiringDocsCount = docs.size,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = SupplierListUiState(isLoading = true)
  )

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun onStatusFilterChange(filter: String) {
    _statusFilter.value = filter
  }

  fun getSupplier(id: String) = repository.getSupplierById(id)

  fun getBankAccounts(supplierId: String) = repository.getAccountsForSupplier(supplierId)

  fun getDocuments(supplierId: String) = repository.getDocumentsForSupplier(supplierId)

  fun getInteractions(supplierId: String) = repository.getInteractionsForSupplier(supplierId)

  fun getAccountStatement(supplierId: String): SupplierAccountStatement =
    repository.calculateAccountStatement(supplierId)

  fun saveSupplier(supplier: SupplierEntity, onSaved: () -> Unit) {
    viewModelScope.launch {
      repository.saveSupplier(supplier)
      onSaved()
    }
  }

  fun updateStatus(id: String, newStatus: String, reason: String) {
    viewModelScope.launch {
      repository.updateSupplierStatus(id, newStatus, reason)
    }
  }

  fun updateScores(
    id: String,
    quality: Double,
    price: Double,
    delivery: Double,
    afterSales: Double,
    reliability: Double
  ) {
    viewModelScope.launch {
      repository.updateSupplierScores(id, quality, price, delivery, afterSales, reliability)
    }
  }

  fun archiveSupplier(id: String, reason: String, onArchived: () -> Unit) {
    viewModelScope.launch {
      repository.softArchiveSupplier(id, reason)
      onArchived()
    }
  }

  fun addBankAccount(account: SupplierBankAccountEntity) {
    viewModelScope.launch {
      repository.addBankAccount(account)
    }
  }

  fun addDocument(document: SupplierDocumentEntity) {
    viewModelScope.launch {
      repository.addDocument(document)
    }
  }

  fun addInteraction(interaction: SupplierInteractionEntity) {
    viewModelScope.launch {
      repository.addInteraction(interaction)
    }
  }

  fun exportSuppliers(context: Context, format: ExportFormat) {
    val suppliers = listUiState.value.filteredSuppliers
    val headers = listOf(
      "Raison Sociale",
      "Forme Juridique",
      "Statut",
      "Wilaya",
      "Commune",
      "Téléphone",
      "Email",
      "NIF",
      "NIS",
      "RC",
      "Article Imposition",
      "Note Globale (/5)",
      "Conditions de Paiement"
    )

    val csv = ExportService.generateCsv(headers, suppliers) { s ->
      listOf(
        s.raisonSociale,
        s.formeJuridique,
        s.status,
        s.wilayaName,
        s.commune,
        s.phone,
        s.email,
        s.nif,
        s.nis,
        s.rcNumber,
        s.articleImposition,
        s.overallRating.toString(),
        "${s.paymentTermsDays} jours"
      )
    }

    val title = "Liste_Fournisseurs_AcheteurDZ.csv"
    ExportService.shareExport(context, title, csv, "text/csv")
  }

  fun exportSupplierSheet(context: Context, supplier: SupplierEntity, format: ExportFormat) {
    val statement = repository.calculateAccountStatement(supplier.id)
    val text = buildString {
      appendLine("====================================================")
      appendLine("           FICHE FOURNISSEUR AGRÉÉ - ACHETEUR DZ    ")
      appendLine("====================================================")
      appendLine("Raison sociale : ${supplier.raisonSociale} (${supplier.formeJuridique})")
      appendLine("Statut         : ${supplier.status}")
      appendLine("Activité       : ${supplier.activite}")
      appendLine("Adresse        : ${supplier.adresse}, ${supplier.commune}, ${supplier.wilayaName} (${supplier.wilayaCode})")
      appendLine("Contact        : ${supplier.contactPerson} - Tél: ${supplier.phone} - Email: ${supplier.email}")
      appendLine("----------------------------------------------------")
      appendLine("IDENTIFIANTS FISCAUX & COMMERCIAUX (ALGÉRIE) :")
      appendLine("NIF (15 ch.)   : ${supplier.nif}")
      appendLine("NIS (15 ch.)   : ${supplier.nis}")
      appendLine("RC             : ${supplier.rcNumber} (Date: ${supplier.rcDate})")
      appendLine("Article d'Imp. : ${supplier.articleImposition}")
      appendLine("----------------------------------------------------")
      appendLine("ÉVALUATION GLOBALE : ${supplier.overallRating} / 5.0")
      appendLine("Qualité: ${supplier.qualityScore}/5 | Prix: ${supplier.priceScore}/5 | Délais: ${supplier.deliveryScore}/5")
      appendLine("SAV: ${supplier.afterSalesScore}/5 | Fiabilité: ${supplier.reliabilityScore}/5")
      appendLine("----------------------------------------------------")
      appendLine("RELEVÉ DE COMPTE (DZD) :")
      appendLine("Total Commandé : ${statement.totalOrderedDzd} DA")
      appendLine("Total Reçu     : ${statement.totalReceivedDzd} DA")
      appendLine("Total Facturé  : ${statement.totalInvoicedDzd} DA")
      appendLine("Total Payé     : ${statement.totalPaidDzd} DA")
      appendLine("Solde en cours : ${statement.currentBalanceDzd} DA")
      appendLine("====================================================")
    }

    val title = "Fiche_${supplier.raisonSociale.replace(" ", "_")}.txt"
    ExportService.shareExport(context, title, text, "text/plain")
  }
}
