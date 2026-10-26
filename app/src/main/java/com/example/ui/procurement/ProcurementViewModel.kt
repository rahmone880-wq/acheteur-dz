package com.example.ui.procurement

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.designsystem.components.ExportFormat
import com.example.core.utils.ExportService
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import com.example.data.local.entities.AuditLogEntity
import com.example.data.repository.AuditRepository
import com.example.data.repository.ProcurementRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProcurementViewModel(
  private val procurementRepository: ProcurementRepository,
  private val supplierRepository: SupplierRepository,
  private val projectRepository: ProjectRepository,
  private val productRepository: ProductRepository,
  private val auditRepository: AuditRepository? = null
) : ViewModel() {

  val purchaseRequests = procurementRepository.getActivePurchaseRequests()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val purchaseOrders = procurementRepository.getActivePurchaseOrders()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val receptions = procurementRepository.getActiveReceptions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val invoices = procurementRepository.getActiveInvoices()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val suppliers = supplierRepository.getActiveSuppliers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val projects = projectRepository.getActiveProjects()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val products = productRepository.getActiveProducts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun createPurchaseRequest(da: PurchaseRequestEntity, onCreated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.savePurchaseRequest(da)
      onCreated()
    }
  }

  fun createPurchaseOrder(
    bc: PurchaseOrderEntity,
    lines: List<PurchaseOrderLineEntity>,
    onCreated: () -> Unit
  ) {
    viewModelScope.launch {
      procurementRepository.savePurchaseOrder(bc, lines)
      onCreated()
    }
  }

  fun recordReception(reception: ReceptionEntity, onCreated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.saveReception(reception)
      onCreated()
    }
  }

  fun recordInvoice(invoice: SupplierInvoiceEntity, onCreated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.saveInvoice(invoice)
      onCreated()
    }
  }

  fun updatePurchaseRequest(da: PurchaseRequestEntity, onUpdated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.savePurchaseRequest(da)
      onUpdated()
    }
  }

  fun updatePurchaseOrder(bc: PurchaseOrderEntity, onUpdated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.savePurchaseOrder(bc)
      onUpdated()
    }
  }

  fun updateReception(reception: ReceptionEntity, onUpdated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.saveReception(reception)
      onUpdated()
    }
  }

  fun updateInvoice(invoice: SupplierInvoiceEntity, onUpdated: () -> Unit) {
    viewModelScope.launch {
      procurementRepository.saveInvoice(invoice)
      onUpdated()
    }
  }

  fun getEntityAuditLogs(entityType: String, entityId: String): Flow<List<AuditLogEntity>> {
    return auditRepository?.getLogsForEntity(entityType, entityId) ?: flowOf(emptyList())
  }

  fun exportOrders(context: Context, format: ExportFormat) {
    val orders = purchaseOrders.value
    val headers = listOf("N° BC", "Fournisseur", "Chantier / Projet", "Date", "Total HT (DA)", "TVA (DA)", "Total TTC (DA)", "Statut")
    val csv = ExportService.generateCsv(headers, orders) {
      listOf(it.bcNumber, it.supplierName, it.projectName, it.orderDate, it.totalHtDzd.toString(), it.totalTvaDzd.toString(), it.totalTtcDzd.toString(), it.status)
    }
    ExportService.shareExport(context, "Bons_de_Commande_AcheteurDZ.csv", csv, "text/csv")
  }

  fun emailPurchaseOrder(
    context: Context,
    bc: PurchaseOrderEntity,
    buyerEmail: String = "abdobigboss30@gmail.com",
    buyerName: String = "Responsable Achats",
    signature: String = "Acheteur DZ"
  ) {
    val subject = "[Bon de Commande ${bc.bcNumber}] Commande pour chantier ${bc.projectName}"
    val body = """
      Bonjour,

      Veuillez trouver ci-dessous les détails de notre bon de commande officiel :

      • Numéro de Commande : ${bc.bcNumber}
      • Fournisseur : ${bc.supplierName}
      • Date d'émission : ${bc.orderDate}
      • Chantier / Destination : ${bc.projectName}
      • Lieu de livraison : ${bc.deliverySiteAddress.ifBlank { "Chantier direct" }}
      • Date souhaitée : ${bc.expectedDeliveryDate.ifBlank { "Selon planning" }}

      Montants :
      • Total HT : ${bc.totalHtDzd} DZD
      • TVA (19%) : ${bc.totalTvaDzd} DZD
      • Total TTC : ${bc.totalTtcDzd} DZD
      • Modalités de règlement : ${bc.paymentTerms}

      Demandeur / Acheteur responsable :
      • $buyerName
      • Gmail : $buyerEmail

      $signature
    """.trimIndent()

    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
      data = android.net.Uri.parse("mailto:")
      putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
      putExtra(android.content.Intent.EXTRA_TEXT, body)
    }

    try {
      context.startActivity(android.content.Intent.createChooser(intent, "Transmettre le BC par Gmail / Email"))
    } catch (e: Exception) {
      android.widget.Toast.makeText(context, "Application de messagerie introuvable.", android.widget.Toast.LENGTH_SHORT).show()
    }
  }
}
