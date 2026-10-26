package com.example.ui.products

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.designsystem.components.ExportFormat
import com.example.core.utils.ExportService
import com.example.data.local.entities.ProductEntity
import com.example.data.repository.ProductRepository
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductListUiState(
  val products: List<ProductEntity> = emptyList(),
  val filteredProducts: List<ProductEntity> = emptyList(),
  val searchQuery: String = "",
  val categoryFilter: String = "TOUTES",
  val isLoading: Boolean = false
)

class ProductViewModel(
  private val productRepository: ProductRepository,
  private val supplierRepository: SupplierRepository
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _categoryFilter = MutableStateFlow("TOUTES")
  private val _products = productRepository.getActiveProducts()

  val suppliers = supplierRepository.getActiveSuppliers()

  val uiState: StateFlow<ProductListUiState> = combine(
    _products,
    _searchQuery,
    _categoryFilter
  ) { list, query, category ->
    val filtered = list.filter {
      val matchesQuery = query.isBlank() ||
          it.designationFr.contains(query, ignoreCase = true) ||
          it.designationAr.contains(query) ||
          it.reference.contains(query, ignoreCase = true) ||
          it.defaultSupplierName.contains(query, ignoreCase = true)

      val matchesCategory = category == "TOUTES" || it.category == category
      matchesQuery && matchesCategory
    }

    ProductListUiState(
      products = list,
      filteredProducts = filtered,
      searchQuery = query,
      categoryFilter = category
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ProductListUiState(isLoading = true)
  )

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun onCategoryFilterChange(category: String) {
    _categoryFilter.value = category
  }

  fun saveProduct(product: ProductEntity, onSaved: () -> Unit) {
    viewModelScope.launch {
      productRepository.saveProduct(product)
      onSaved()
    }
  }

  fun exportProducts(context: Context, format: ExportFormat) {
    val items = uiState.value.filteredProducts
    val headers = listOf("Référence", "Désignation FR", "Désignation AR", "Catégorie", "Unité", "TVA (%)", "Fournisseur par Défaut", "Dernier Prix (DA)")
    val csv = ExportService.generateCsv(headers, items) {
      listOf(it.reference, it.designationFr, it.designationAr, it.category, it.unit, it.tvaRate.toString(), it.defaultSupplierName, it.lastPriceDzd.toString())
    }
    ExportService.shareExport(context, "Catalogue_Produits_AcheteurDZ.csv", csv, "text/csv")
  }
}
