package com.example.core.ai

data class ExtractedReceiptItem(
  val designation: String,
  val quantity: Double,
  val unit: String = "U",
  val unitPriceHt: Double = 0.0,
  val totalPriceHt: Double = 0.0
)

data class ReceiptExtractionData(
  val supplierName: String = "",
  val nif: String = "",
  val nis: String = "",
  val rcNumber: String = "",
  val articleImposition: String = "",
  val supplierAddress: String = "",
  val supplierPhone: String = "",
  val documentType: String = "FACTURE", // FACTURE, BON_RECEPTION_BL, RECU_CAISSE, BON_COMMANDE
  val documentNumber: String = "",
  val documentDate: String = "",
  val totalHtDzd: Double = 0.0,
  val tvaRate: Double = 19.0,
  val totalTvaDzd: Double = 0.0,
  val timbreFiscalDzd: Double = 0.0,
  val totalTtcDzd: Double = 0.0,
  val paymentMethod: String = "ESPECES", // ESPECES, VIREMENT, CHEQUE, TRAITE
  val items: List<ExtractedReceiptItem> = emptyList(),
  val summaryNotes: String = ""
)
