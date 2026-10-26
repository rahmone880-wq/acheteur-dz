package com.example.core.transfer

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.PurchaseOrderEntity
import com.example.data.local.entities.PurchaseOrderLineEntity
import com.example.data.local.entities.PurchaseRequestEntity
import com.example.data.local.entities.ReceptionEntity
import com.example.data.local.entities.SupplierBankAccountEntity
import com.example.data.local.entities.SupplierDocumentEntity
import com.example.data.local.entities.SupplierEntity
import com.example.data.local.entities.SupplierInvoiceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataTransferManager {

  private const val APP_IDENTIFIER = "Acheteur DZ"

  /**
   * Builds a TransferPackage from the current local database based on scope.
   */
  suspend fun buildExportPackage(
    database: AppDatabase,
    scope: TransferScope,
    deviceName: String = android.os.Build.MODEL ?: "Téléphone Acheteur DZ"
  ): TransferPackage = withContext(Dispatchers.IO) {
    val suppliers = if (scope == TransferScope.FULL || scope == TransferScope.SUPPLIERS) {
      database.supplierDao().getAllSuppliersDirect()
    } else emptyList()

    val bankAccounts = if (scope == TransferScope.FULL || scope == TransferScope.SUPPLIERS) {
      database.supplierBankAccountDao().getAllAccountsDirect()
    } else emptyList()

    val documents = if (scope == TransferScope.FULL || scope == TransferScope.SUPPLIERS) {
      database.supplierDocumentDao().getAllDocumentsDirect()
    } else emptyList()

    val projects = if (scope == TransferScope.FULL || scope == TransferScope.PROJECTS) {
      database.projectDao().getAllProjectsDirect()
    } else emptyList()

    val products = if (scope == TransferScope.FULL || scope == TransferScope.PRODUCTS) {
      database.productDao().getAllProductsDirect()
    } else emptyList()

    val purchaseRequests = if (scope == TransferScope.FULL || scope == TransferScope.PROCUREMENT) {
      database.procurementDao().getAllPurchaseRequestsDirect()
    } else emptyList()

    val purchaseOrders = if (scope == TransferScope.FULL || scope == TransferScope.PROCUREMENT) {
      database.procurementDao().getAllPurchaseOrdersDirect()
    } else emptyList()

    val purchaseOrderLines = if (scope == TransferScope.FULL || scope == TransferScope.PROCUREMENT) {
      database.procurementDao().getAllPurchaseOrderLinesDirect()
    } else emptyList()

    val receptions = if (scope == TransferScope.FULL || scope == TransferScope.PROCUREMENT) {
      database.procurementDao().getAllReceptionsDirect()
    } else emptyList()

    val invoices = if (scope == TransferScope.FULL || scope == TransferScope.PROCUREMENT) {
      database.procurementDao().getAllInvoicesDirect()
    } else emptyList()

    TransferPackage(
      appName = APP_IDENTIFIER,
      formatVersion = 1,
      timestamp = System.currentTimeMillis(),
      senderDevice = deviceName,
      scope = scope.name,
      suppliers = suppliers,
      bankAccounts = bankAccounts,
      documents = documents,
      projects = projects,
      products = products,
      purchaseRequests = purchaseRequests,
      purchaseOrders = purchaseOrders,
      purchaseOrderLines = purchaseOrderLines,
      receptions = receptions,
      invoices = invoices
    )
  }

  /**
   * Serializes a TransferPackage into a clean JSON string.
   */
  fun packageToJson(pkg: TransferPackage): String {
    val root = JSONObject().apply {
      put("appName", pkg.appName)
      put("formatVersion", pkg.formatVersion)
      put("timestamp", pkg.timestamp)
      put("senderDevice", pkg.senderDevice)
      put("scope", pkg.scope)

      // Suppliers
      val suppliersArray = JSONArray()
      pkg.suppliers.forEach { s ->
        suppliersArray.put(JSONObject().apply {
          put("id", s.id)
          put("raisonSociale", s.raisonSociale)
          put("formeJuridique", s.formeJuridique)
          put("activite", s.activite)
          put("adresse", s.adresse)
          put("wilayaCode", s.wilayaCode)
          put("wilayaName", s.wilayaName)
          put("commune", s.commune)
          put("phone", s.phone)
          put("email", s.email)
          put("contactPerson", s.contactPerson)
          put("rcNumber", s.rcNumber)
          put("rcDate", s.rcDate)
          put("nif", s.nif)
          put("nis", s.nis)
          put("articleImposition", s.articleImposition)
          put("status", s.status)
          put("qualityScore", s.qualityScore)
          put("priceScore", s.priceScore)
          put("deliveryScore", s.deliveryScore)
          put("afterSalesScore", s.afterSalesScore)
          put("reliabilityScore", s.reliabilityScore)
          put("overallRating", s.overallRating)
          put("paymentTermsDays", s.paymentTermsDays)
          put("deliveryZones", s.deliveryZones)
          put("categoriesTags", s.categoriesTags)
          put("version", s.version)
        })
      }
      put("suppliers", suppliersArray)

      // Bank Accounts
      val bankAccountsArray = JSONArray()
      pkg.bankAccounts.forEach { b ->
        bankAccountsArray.put(JSONObject().apply {
          put("id", b.id)
          put("supplierId", b.supplierId)
          put("bankName", b.bankName)
          put("agency", b.agency)
          put("rib", b.rib)
          put("holderName", b.holderName)
          put("isPrimary", b.isPrimary)
        })
      }
      put("bankAccounts", bankAccountsArray)

      // Documents
      val documentsArray = JSONArray()
      pkg.documents.forEach { d ->
        documentsArray.put(JSONObject().apply {
          put("id", d.id)
          put("supplierId", d.supplierId)
          put("docType", d.docType)
          put("title", d.title)
          put("referenceNumber", d.referenceNumber)
          put("issueDate", d.issueDate)
          put("expiryDate", d.expiryDate)
        })
      }
      put("documents", documentsArray)

      // Projects
      val projectsArray = JSONArray()
      pkg.projects.forEach { p ->
        projectsArray.put(JSONObject().apply {
          put("id", p.id)
          put("code", p.code)
          put("name", p.name)
          put("client", p.client)
          put("locationWilaya", p.locationWilaya)
          put("locationCommune", p.locationCommune)
          put("costCenter", p.costCenter)
          put("manager", p.manager)
          put("status", p.status)
          put("totalBudgetDzd", p.totalBudgetDzd)
          put("committedBudgetDzd", p.committedBudgetDzd)
          put("invoicedBudgetDzd", p.invoicedBudgetDzd)
          put("paidBudgetDzd", p.paidBudgetDzd)
          put("version", p.version)
        })
      }
      put("projects", projectsArray)

      // Products
      val productsArray = JSONArray()
      pkg.products.forEach { pr ->
        productsArray.put(JSONObject().apply {
          put("id", pr.id)
          put("reference", pr.reference)
          put("designationFr", pr.designationFr)
          put("designationAr", pr.designationAr)
          put("category", pr.category)
          put("unit", pr.unit)
          put("tvaRate", pr.tvaRate)
          put("defaultSupplierId", pr.defaultSupplierId ?: "")
          put("defaultSupplierName", pr.defaultSupplierName)
          put("lastPriceDzd", pr.lastPriceDzd)
          put("minPriceDzd", pr.minPriceDzd)
          put("maxPriceDzd", pr.maxPriceDzd)
          put("avgPriceDzd", pr.avgPriceDzd)
          put("isStockable", pr.isStockable)
          put("version", pr.version)
        })
      }
      put("products", productsArray)

      // Purchase Requests (DA)
      val daArray = JSONArray()
      pkg.purchaseRequests.forEach { da ->
        daArray.put(JSONObject().apply {
          put("id", da.id)
          put("daNumber", da.daNumber)
          put("requesterName", da.requesterName)
          put("department", da.department)
          put("projectId", da.projectId)
          put("projectName", da.projectName)
          put("priority", da.priority)
          put("requiredDate", da.requiredDate)
          put("status", da.status)
          put("justification", da.justification)
          put("totalEstimatedDzd", da.totalEstimatedDzd)
          put("version", da.version)
        })
      }
      put("purchaseRequests", daArray)

      // Purchase Orders (BC)
      val bcArray = JSONArray()
      pkg.purchaseOrders.forEach { bc ->
        bcArray.put(JSONObject().apply {
          put("id", bc.id)
          put("bcNumber", bc.bcNumber)
          put("purchaseRequestId", bc.purchaseRequestId ?: "")
          put("daNumber", bc.daNumber)
          put("supplierId", bc.supplierId)
          put("supplierName", bc.supplierName)
          put("projectId", bc.projectId)
          put("projectName", bc.projectName)
          put("status", bc.status)
          put("orderDate", bc.orderDate)
          put("expectedDeliveryDate", bc.expectedDeliveryDate)
          put("deliverySiteAddress", bc.deliverySiteAddress)
          put("totalHtDzd", bc.totalHtDzd)
          put("totalTvaDzd", bc.totalTvaDzd)
          put("timbreFiscalDzd", bc.timbreFiscalDzd)
          put("totalTtcDzd", bc.totalTtcDzd)
          put("paymentTerms", bc.paymentTerms)
          put("version", bc.version)
        })
      }
      put("purchaseOrders", bcArray)

      // Lines
      val linesArray = JSONArray()
      pkg.purchaseOrderLines.forEach { l ->
        linesArray.put(JSONObject().apply {
          put("id", l.id)
          put("purchaseOrderId", l.purchaseOrderId)
          put("productId", l.productId ?: "")
          put("productReference", l.productReference)
          put("designation", l.designation)
          put("unit", l.unit)
          put("quantity", l.quantity)
          put("unitPriceHt", l.unitPriceHt)
          put("discountPercent", l.discountPercent)
          put("tvaRate", l.tvaRate)
          put("totalHt", l.totalHt)
          put("totalTtc", l.totalTtc)
          put("receivedQuantity", l.receivedQuantity)
        })
      }
      put("purchaseOrderLines", linesArray)

      // Receptions
      val recArray = JSONArray()
      pkg.receptions.forEach { r ->
        recArray.put(JSONObject().apply {
          put("id", r.id)
          put("blNumber", r.blNumber)
          put("brNumber", r.brNumber)
          put("purchaseOrderId", r.purchaseOrderId)
          put("bcNumber", r.bcNumber)
          put("supplierId", r.supplierId)
          put("supplierName", r.supplierName)
          put("destinationType", r.destinationType)
          put("destinationId", r.destinationId)
          put("destinationName", r.destinationName)
          put("receptionDate", r.receptionDate)
          put("receivedBy", r.receivedBy)
          put("status", r.status)
          put("notes", r.notes)
        })
      }
      put("receptions", recArray)

      // Invoices
      val invArray = JSONArray()
      pkg.invoices.forEach { inv ->
        invArray.put(JSONObject().apply {
          put("id", inv.id)
          put("invoiceNumber", inv.invoiceNumber)
          put("invoiceDate", inv.invoiceDate)
          put("dueDate", inv.dueDate)
          put("supplierId", inv.supplierId)
          put("supplierName", inv.supplierName)
          put("purchaseOrderId", inv.purchaseOrderId)
          put("bcNumber", inv.bcNumber)
          put("projectId", inv.projectId)
          put("projectName", inv.projectName)
          put("totalHtDzd", inv.totalHtDzd)
          put("totalTvaDzd", inv.totalTvaDzd)
          put("totalTtcDzd", inv.totalTtcDzd)
          put("paidAmountDzd", inv.paidAmountDzd)
          put("matchingStatus", inv.matchingStatus)
          put("paymentStatus", inv.paymentStatus)
        })
      }
      put("invoices", invArray)
    }

    return root.toString(2)
  }

  /**
   * Parses JSON text into a TransferPackage.
   */
  fun jsonToPackage(jsonString: String): TransferPackage {
    val root = JSONObject(jsonString)
    val appName = root.optString("appName", "")
    if (!appName.contains("Acheteur DZ", ignoreCase = true)) {
      throw IllegalArgumentException("Fichier invalide : ce package ne provient pas de l'application Acheteur DZ.")
    }

    val formatVersion = root.optInt("formatVersion", 1)
    val timestamp = root.optLong("timestamp", System.currentTimeMillis())
    val senderDevice = root.optString("senderDevice", "Appareil distant")
    val scope = root.optString("scope", TransferScope.FULL.name)

    // Suppliers
    val suppliersList = mutableListOf<SupplierEntity>()
    val suppliersArr = root.optJSONArray("suppliers") ?: JSONArray()
    for (i in 0 until suppliersArr.length()) {
      val o = suppliersArr.getJSONObject(i)
      suppliersList.add(
        SupplierEntity(
          id = o.optString("id"),
          raisonSociale = o.optString("raisonSociale"),
          formeJuridique = o.optString("formeJuridique", "SARL"),
          activite = o.optString("activite"),
          adresse = o.optString("adresse"),
          wilayaCode = o.optString("wilayaCode", "16"),
          wilayaName = o.optString("wilayaName", "Alger"),
          commune = o.optString("commune"),
          phone = o.optString("phone"),
          email = o.optString("email"),
          contactPerson = o.optString("contactPerson"),
          rcNumber = o.optString("rcNumber"),
          rcDate = o.optString("rcDate"),
          nif = o.optString("nif"),
          nis = o.optString("nis"),
          articleImposition = o.optString("articleImposition"),
          status = o.optString("status", "AGREE"),
          qualityScore = o.optDouble("qualityScore", 4.0),
          priceScore = o.optDouble("priceScore", 4.0),
          deliveryScore = o.optDouble("deliveryScore", 4.0),
          afterSalesScore = o.optDouble("afterSalesScore", 4.0),
          reliabilityScore = o.optDouble("reliabilityScore", 4.0),
          overallRating = o.optDouble("overallRating", 4.0),
          paymentTermsDays = o.optInt("paymentTermsDays", 30),
          deliveryZones = o.optString("deliveryZones"),
          categoriesTags = o.optString("categoriesTags"),
          version = o.optInt("version", 1)
        )
      )
    }

    // Bank Accounts
    val bankList = mutableListOf<SupplierBankAccountEntity>()
    val bankArr = root.optJSONArray("bankAccounts") ?: JSONArray()
    for (i in 0 until bankArr.length()) {
      val o = bankArr.getJSONObject(i)
      bankList.add(
        SupplierBankAccountEntity(
          id = o.optString("id"),
          supplierId = o.optString("supplierId"),
          bankName = o.optString("bankName", "BNA"),
          agency = o.optString("agency"),
          rib = o.optString("rib"),
          holderName = o.optString("holderName"),
          isPrimary = o.optBoolean("isPrimary", true)
        )
      )
    }

    // Documents
    val docList = mutableListOf<SupplierDocumentEntity>()
    val docArr = root.optJSONArray("documents") ?: JSONArray()
    for (i in 0 until docArr.length()) {
      val o = docArr.getJSONObject(i)
      docList.add(
        SupplierDocumentEntity(
          id = o.optString("id"),
          supplierId = o.optString("supplierId"),
          docType = o.optString("docType", "RC"),
          title = o.optString("title"),
          referenceNumber = o.optString("referenceNumber"),
          issueDate = o.optString("issueDate"),
          expiryDate = o.optString("expiryDate")
        )
      )
    }

    // Projects
    val projectList = mutableListOf<ProjectEntity>()
    val projectArr = root.optJSONArray("projects") ?: JSONArray()
    for (i in 0 until projectArr.length()) {
      val o = projectArr.getJSONObject(i)
      projectList.add(
        ProjectEntity(
          id = o.optString("id"),
          code = o.optString("code"),
          name = o.optString("name"),
          client = o.optString("client"),
          locationWilaya = o.optString("locationWilaya", "16"),
          locationCommune = o.optString("locationCommune"),
          costCenter = o.optString("costCenter"),
          manager = o.optString("manager"),
          status = o.optString("status", "ACTIF"),
          totalBudgetDzd = o.optDouble("totalBudgetDzd", 0.0),
          committedBudgetDzd = o.optDouble("committedBudgetDzd", 0.0),
          invoicedBudgetDzd = o.optDouble("invoicedBudgetDzd", 0.0),
          paidBudgetDzd = o.optDouble("paidBudgetDzd", 0.0),
          version = o.optInt("version", 1)
        )
      )
    }

    // Products
    val productList = mutableListOf<ProductEntity>()
    val productArr = root.optJSONArray("products") ?: JSONArray()
    for (i in 0 until productArr.length()) {
      val o = productArr.getJSONObject(i)
      productList.add(
        ProductEntity(
          id = o.optString("id"),
          reference = o.optString("reference"),
          designationFr = o.optString("designationFr"),
          designationAr = o.optString("designationAr"),
          category = o.optString("category"),
          unit = o.optString("unit", "U"),
          tvaRate = o.optDouble("tvaRate", 19.0),
          defaultSupplierId = o.optString("defaultSupplierId").ifBlank { null },
          defaultSupplierName = o.optString("defaultSupplierName"),
          lastPriceDzd = o.optDouble("lastPriceDzd", 0.0),
          minPriceDzd = o.optDouble("minPriceDzd", 0.0),
          maxPriceDzd = o.optDouble("maxPriceDzd", 0.0),
          avgPriceDzd = o.optDouble("avgPriceDzd", 0.0),
          isStockable = o.optBoolean("isStockable", false),
          version = o.optInt("version", 1)
        )
      )
    }

    // Purchase Requests (DA)
    val daList = mutableListOf<PurchaseRequestEntity>()
    val daArr = root.optJSONArray("purchaseRequests") ?: JSONArray()
    for (i in 0 until daArr.length()) {
      val o = daArr.getJSONObject(i)
      daList.add(
        PurchaseRequestEntity(
          id = o.optString("id"),
          daNumber = o.optString("daNumber"),
          requesterName = o.optString("requesterName"),
          department = o.optString("department"),
          projectId = o.optString("projectId"),
          projectName = o.optString("projectName"),
          priority = o.optString("priority", "NORMALE"),
          requiredDate = o.optString("requiredDate"),
          status = o.optString("status", "VALIDEE"),
          justification = o.optString("justification"),
          totalEstimatedDzd = o.optDouble("totalEstimatedDzd", 0.0),
          version = o.optInt("version", 1)
        )
      )
    }

    // Purchase Orders (BC)
    val bcList = mutableListOf<PurchaseOrderEntity>()
    val bcArr = root.optJSONArray("purchaseOrders") ?: JSONArray()
    for (i in 0 until bcArr.length()) {
      val o = bcArr.getJSONObject(i)
      bcList.add(
        PurchaseOrderEntity(
          id = o.optString("id"),
          bcNumber = o.optString("bcNumber"),
          purchaseRequestId = o.optString("purchaseRequestId").ifBlank { null },
          daNumber = o.optString("daNumber"),
          supplierId = o.optString("supplierId"),
          supplierName = o.optString("supplierName"),
          projectId = o.optString("projectId"),
          projectName = o.optString("projectName"),
          status = o.optString("status", "CONFIRME"),
          orderDate = o.optString("orderDate"),
          expectedDeliveryDate = o.optString("expectedDeliveryDate"),
          deliverySiteAddress = o.optString("deliverySiteAddress"),
          totalHtDzd = o.optDouble("totalHtDzd", 0.0),
          totalTvaDzd = o.optDouble("totalTvaDzd", 0.0),
          timbreFiscalDzd = o.optDouble("timbreFiscalDzd", 0.0),
          totalTtcDzd = o.optDouble("totalTtcDzd", 0.0),
          paymentTerms = o.optString("paymentTerms", "30 jours"),
          version = o.optInt("version", 1)
        )
      )
    }

    // Lines
    val linesList = mutableListOf<PurchaseOrderLineEntity>()
    val linesArr = root.optJSONArray("purchaseOrderLines") ?: JSONArray()
    for (i in 0 until linesArr.length()) {
      val o = linesArr.getJSONObject(i)
      linesList.add(
        PurchaseOrderLineEntity(
          id = o.optString("id"),
          purchaseOrderId = o.optString("purchaseOrderId"),
          productId = o.optString("productId").ifBlank { null },
          productReference = o.optString("productReference"),
          designation = o.optString("designation"),
          unit = o.optString("unit", "U"),
          quantity = o.optDouble("quantity", 1.0),
          unitPriceHt = o.optDouble("unitPriceHt", 0.0),
          discountPercent = o.optDouble("discountPercent", 0.0),
          tvaRate = o.optDouble("tvaRate", 19.0),
          totalHt = o.optDouble("totalHt", 0.0),
          totalTtc = o.optDouble("totalTtc", 0.0),
          receivedQuantity = o.optDouble("receivedQuantity", 0.0)
        )
      )
    }

    // Receptions
    val recList = mutableListOf<ReceptionEntity>()
    val recArr = root.optJSONArray("receptions") ?: JSONArray()
    for (i in 0 until recArr.length()) {
      val o = recArr.getJSONObject(i)
      recList.add(
        ReceptionEntity(
          id = o.optString("id"),
          blNumber = o.optString("blNumber"),
          brNumber = o.optString("brNumber"),
          purchaseOrderId = o.optString("purchaseOrderId"),
          bcNumber = o.optString("bcNumber"),
          supplierId = o.optString("supplierId"),
          supplierName = o.optString("supplierName"),
          destinationType = o.optString("destinationType", "PROJET"),
          destinationId = o.optString("destinationId"),
          destinationName = o.optString("destinationName"),
          receptionDate = o.optString("receptionDate"),
          receivedBy = o.optString("receivedBy"),
          status = o.optString("status", "CONFORME"),
          notes = o.optString("notes")
        )
      )
    }

    // Invoices
    val invList = mutableListOf<SupplierInvoiceEntity>()
    val invArr = root.optJSONArray("invoices") ?: JSONArray()
    for (i in 0 until invArr.length()) {
      val o = invArr.getJSONObject(i)
      val invoiceDateStr = o.optString("invoiceDate", "")
      val dueDateStr = o.optString("dueDate", invoiceDateStr)
      invList.add(
        SupplierInvoiceEntity(
          id = if (o.has("id") && o.getString("id").isNotBlank()) o.getString("id") else java.util.UUID.randomUUID().toString(),
          invoiceNumber = o.optString("invoiceNumber", "FAC-IMP"),
          invoiceDate = invoiceDateStr,
          dueDate = dueDateStr,
          supplierId = o.optString("supplierId", ""),
          supplierName = o.optString("supplierName", ""),
          purchaseOrderId = o.optString("purchaseOrderId").takeIf { it.isNotBlank() },
          bcNumber = o.optString("bcNumber", ""),
          projectId = o.optString("projectId", ""),
          projectName = o.optString("projectName", ""),
          totalHtDzd = o.optDouble("totalHtDzd", o.optDouble("amountHtDzd", 0.0)),
          totalTvaDzd = o.optDouble("totalTvaDzd", o.optDouble("amountTvaDzd", 0.0)),
          totalTtcDzd = o.optDouble("totalTtcDzd", o.optDouble("amountTtcDzd", 0.0)),
          paidAmountDzd = o.optDouble("paidAmountDzd", 0.0),
          matchingStatus = o.optString("matchingStatus", "CONFORME"),
          paymentStatus = o.optString("paymentStatus", "NON_PAYEE")
        )
      )
    }

    return TransferPackage(
      appName = appName,
      formatVersion = formatVersion,
      timestamp = timestamp,
      senderDevice = senderDevice,
      scope = scope,
      suppliers = suppliersList,
      bankAccounts = bankList,
      documents = docList,
      projects = projectList,
      products = productList,
      purchaseRequests = daList,
      purchaseOrders = bcList,
      purchaseOrderLines = linesList,
      receptions = recList,
      invoices = invList
    )
  }

  /**
   * Writes the TransferPackage to cache and initiates sharing via Android Sharesheet
   * (Compatible with Quick Share, Nearby Share, Bluetooth, WhatsApp, Email, etc.).
   */
  fun sharePackage(context: Context, pkg: TransferPackage) {
    val json = packageToJson(pkg)
    val dir = File(context.cacheDir, "transfers").apply { mkdirs() }
    val timeTag = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date(pkg.timestamp))
    val file = File(dir, "acheteur_dz_${pkg.scope.lowercase()}_$timeTag.adz")
    FileOutputStream(file).use { it.write(json.toByteArray(Charsets.UTF_8)) }

    val uri: Uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "application/octet-stream"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_SUBJECT, "Données Acheteur DZ ($timeTag)")
      putExtra(
        Intent.EXTRA_TEXT,
        "Package de données Acheteur DZ (${pkg.totalItemsCount} éléments). Ouvrez ce fichier dans l'application Acheteur DZ pour fusionner."
      )
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Envoyer vers un autre téléphone via :")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }

  /**
   * Smartly merges a TransferPackage into the receiver's local Room database:
   * - Deduplicates existing entities
   * - Updates changed fields while preserving relationships
   * - Recalculates project budgets
   * - Records a traceable audit log entry
   */
  suspend fun mergePackage(
    database: AppDatabase,
    pkg: TransferPackage,
    currentUser: String = "Acheteur (Import)"
  ): MergeReport = withContext(Dispatchers.IO) {
    var supAdded = 0
    var supUpdated = 0
    var prjAdded = 0
    var prjUpdated = 0
    var prodAdded = 0
    var prodUpdated = 0
    var daAdded = 0
    var daUpdated = 0
    var bcAdded = 0
    var bcUpdated = 0
    var recAdded = 0
    var invAdded = 0

    val now = System.currentTimeMillis()

    // 1. Merge Suppliers
    for (s in pkg.suppliers) {
      val existing = if (s.nif.isNotBlank()) {
        database.supplierDao().getSupplierByNifDirect(s.nif)
          ?: database.supplierDao().getSupplierByIdDirect(s.id)
      } else {
        database.supplierDao().getSupplierByIdDirect(s.id)
      }

      if (existing != null) {
        val merged = existing.copy(
          raisonSociale = s.raisonSociale.ifBlank { existing.raisonSociale },
          phone = s.phone.ifBlank { existing.phone },
          email = s.email.ifBlank { existing.email },
          rcNumber = s.rcNumber.ifBlank { existing.rcNumber },
          articleImposition = s.articleImposition.ifBlank { existing.articleImposition },
          deliveryZones = s.deliveryZones.ifBlank { existing.deliveryZones },
          updatedAt = now,
          updatedBy = currentUser,
          version = existing.version + 1
        )
        database.supplierDao().update(merged)
        supUpdated++
      } else {
        database.supplierDao().insert(s.copy(updatedAt = now, updatedBy = currentUser))
        supAdded++
      }
    }

    // 2. Bank Accounts & Documents
    for (b in pkg.bankAccounts) {
      database.supplierBankAccountDao().insert(b)
    }
    for (d in pkg.documents) {
      database.supplierDocumentDao().insert(d)
    }

    // 3. Merge Projects
    for (p in pkg.projects) {
      val existing = if (p.code.isNotBlank()) {
        database.projectDao().getProjectByCodeDirect(p.code)
          ?: database.projectDao().getProjectByIdDirect(p.id)
      } else {
        database.projectDao().getProjectByIdDirect(p.id)
      }

      if (existing != null) {
        val merged = existing.copy(
          name = p.name.ifBlank { existing.name },
          client = p.client.ifBlank { existing.client },
          manager = p.manager.ifBlank { existing.manager },
          totalBudgetDzd = if (p.totalBudgetDzd > 0) p.totalBudgetDzd else existing.totalBudgetDzd,
          committedBudgetDzd = maxOf(existing.committedBudgetDzd, p.committedBudgetDzd),
          updatedAt = now,
          updatedBy = currentUser,
          version = existing.version + 1
        )
        database.projectDao().update(merged)
        prjUpdated++
      } else {
        database.projectDao().insert(p.copy(updatedAt = now, updatedBy = currentUser))
        prjAdded++
      }
    }

    // 4. Merge Products
    for (pr in pkg.products) {
      val existing = if (pr.reference.isNotBlank()) {
        database.productDao().getProductByReferenceDirect(pr.reference)
          ?: database.productDao().getProductById(pr.id)
      } else null

      val directExisting = database.productDao().getAllProductsDirect().find {
        it.reference.equals(pr.reference, ignoreCase = true) || it.id == pr.id
      }

      if (directExisting != null) {
        val merged = directExisting.copy(
          designationFr = pr.designationFr.ifBlank { directExisting.designationFr },
          lastPriceDzd = if (pr.lastPriceDzd > 0) pr.lastPriceDzd else directExisting.lastPriceDzd,
          tvaRate = pr.tvaRate,
          updatedAt = now,
          updatedBy = currentUser,
          version = directExisting.version + 1
        )
        database.productDao().update(merged)
        prodUpdated++
      } else {
        database.productDao().insert(pr.copy(updatedAt = now, updatedBy = currentUser))
        prodAdded++
      }
    }

    // 5. Merge Purchase Requests (DA)
    for (da in pkg.purchaseRequests) {
      val existing = database.procurementDao().getPurchaseRequestByNumberDirect(da.daNumber)
      if (existing != null) {
        database.procurementDao().insertPurchaseRequest(
          da.copy(id = existing.id, updatedAt = now, updatedBy = currentUser, version = existing.version + 1)
        )
        daUpdated++
      } else {
        database.procurementDao().insertPurchaseRequest(da.copy(updatedAt = now, updatedBy = currentUser))
        daAdded++
      }
    }

    // 6. Merge Purchase Orders (BC) & Lines
    for (bc in pkg.purchaseOrders) {
      val existing = database.procurementDao().getPurchaseOrderByNumberDirect(bc.bcNumber)
      if (existing != null) {
        database.procurementDao().insertPurchaseOrder(
          bc.copy(id = existing.id, updatedAt = now, updatedBy = currentUser, version = existing.version + 1)
        )
        bcUpdated++
      } else {
        database.procurementDao().insertPurchaseOrder(bc.copy(updatedAt = now, updatedBy = currentUser))
        bcAdded++
      }
    }

    if (pkg.purchaseOrderLines.isNotEmpty()) {
      database.procurementDao().insertOrderLines(pkg.purchaseOrderLines)
    }

    // 7. Merge Receptions & Invoices
    for (r in pkg.receptions) {
      database.procurementDao().insertReception(r)
      recAdded++
    }
    for (inv in pkg.invoices) {
      database.procurementDao().insertInvoice(inv)
      invAdded++
    }

    // 8. Record in Legal Audit Log
    val summaryText = "Import et fusion réussis depuis ${pkg.senderDevice} ($supAdded fournisseurs aj., $bcAdded BC aj., $prjAdded projets aj.)"
    database.auditLogDao().insert(
      AuditLogEntity(
        entityType = "DATA_TRANSFER",
        entityId = "transfer-$now",
        action = "IMPORT_MERGE",
        who = currentUser,
        timestamp = now,
        description = summaryText
      )
    )

    MergeReport(
      success = true,
      suppliersAdded = supAdded,
      suppliersUpdated = supUpdated,
      projectsAdded = prjAdded,
      projectsUpdated = prjUpdated,
      productsAdded = prodAdded,
      productsUpdated = prodUpdated,
      daAdded = daAdded,
      daUpdated = daUpdated,
      bcAdded = bcAdded,
      bcUpdated = bcUpdated,
      receptionsAdded = recAdded,
      invoicesAdded = invAdded,
      message = summaryText
    )
  }
}
