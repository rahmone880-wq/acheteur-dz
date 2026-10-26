package com.example.core.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "ReceiptScannerService"

class ReceiptScannerService {

  private val receiptSchema = Schema.obj(
    properties = mapOf(
      "supplierName" to Schema.string(description = "Raison sociale du fournisseur émetteur"),
      "nif" to Schema.string(description = "NIF fiscal algérien à 15 chiffres si présent"),
      "nis" to Schema.string(description = "NIS à 15 chiffres si présent"),
      "rcNumber" to Schema.string(description = "Numéro du registre de commerce (RC)"),
      "articleImposition" to Schema.string(description = "Article d'imposition (AI)"),
      "supplierAddress" to Schema.string(description = "Adresse ou wilaya du fournisseur"),
      "supplierPhone" to Schema.string(description = "Téléphone du fournisseur"),
      "documentType" to Schema.string(description = "Type de document: FACTURE, BON_RECEPTION_BL, RECU_CAISSE, ou BON_COMMANDE"),
      "documentNumber" to Schema.string(description = "Numéro de facture ou reçu"),
      "documentDate" to Schema.string(description = "Date du document (JJ/MM/AAAA)"),
      "totalHtDzd" to Schema.double(description = "Montant Total Hors Taxe en Dinar Algérien"),
      "tvaRate" to Schema.double(description = "Taux de TVA en pourcentage (19, 9 ou 0)"),
      "totalTvaDzd" to Schema.double(description = "Montant TVA en Dinar Algérien"),
      "timbreFiscalDzd" to Schema.double(description = "Montant du droit de timbre fiscal"),
      "totalTtcDzd" to Schema.double(description = "Montant Total TTC net à payer en Dinar Algérien"),
      "paymentMethod" to Schema.string(description = "Mode de paiement: ESPECES, VIREMENT, CHEQUE, ou TRAITE"),
      "items" to Schema.array(
        items = Schema.obj(
          properties = mapOf(
            "designation" to Schema.string(description = "Désignation de la marchandise ou prestation"),
            "quantity" to Schema.double(description = "Quantité"),
            "unit" to Schema.string(description = "Unité (U, Sac, Tonne, Mètre, Litre, etc.)"),
            "unitPriceHt" to Schema.double(description = "Prix unitaire Hors Taxe"),
            "totalPriceHt" to Schema.double(description = "Total ligne Hors Taxe")
          ),
          optionalProperties = listOf("unit", "unitPriceHt", "totalPriceHt")
        ),
        description = "Articles ou lignes figurant sur le reçu ou la facture"
      ),
      "summaryNotes" to Schema.string(description = "Remarques ou observations générales")
    ),
    optionalProperties = listOf(
      "nif", "nis", "rcNumber", "articleImposition", "supplierAddress",
      "supplierPhone", "timbreFiscalDzd", "summaryNotes"
    )
  )

  private val config = generationConfig {
    responseMimeType = "application/json"
    responseSchema = receiptSchema
  }

  // Model initialized lazily to avoid premature initialization before App Check
  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = config
    )
  }

  /**
   * Scans a receipt/invoice photo bitmap using Gemini 3.8 Flash multimodal capabilities
   * and extracts key fiscal, commercial, and line item details in an Algerian context.
   */
  suspend fun extractReceiptData(bitmap: Bitmap): ReceiptExtractionData {
    val optimizedBitmap = scaleBitmapDown(bitmap, maxDimension = 1024)
    return try {
      val promptText = """
        Tu es un expert comptable et acheteur principal en Algérie.
        Analyse cette photo de reçu, facture papier, ticket de caisse ou bon de livraison (BL).
        Extrais avec précision les informations suivantes :
        1. Le nom ou raison sociale du fournisseur / vendeur.
        2. Les identifiants fiscaux algériens s'ils sont lisibles : NIF (15 chiffres), NIS, RC, Article d'imposition (AI).
        3. Le numéro du document et la date (JJ/MM/AAAA).
        4. Les montants financiers en Dinars Algériens (DA / DZD) : Total HT, Taux TVA (19% ou 9%), Montant TVA, Timbre fiscal (si espèces), Total TTC.
        5. La liste des articles/marchandises avec désignation, quantité et prix.
        6. Le mode de règlement (Espèces, Virement, Chèque).
      """.trimIndent()

      // Multimodal inference with 90s timeout and downscaled bitmap to prevent network timeout
      val response = withTimeout(90_000L) {
        model.generateContent(
          content {
            image(optimizedBitmap)
            text(promptText)
          }
        )
      }

      val jsonStr = response.text ?: throw IllegalStateException("Gemini a renvoyé une réponse vide.")
      Log.d(TAG, "Gemini Receipt JSON: $jsonStr")

      parseReceiptJson(jsonStr)
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Délai d'analyse Gemini dépassé", e)
      throw IllegalStateException("Le délai d'analyse du reçu par Gemini a expiré. Veuillez vérifier la connexion ou réessayer avec une photo bien cadrée.")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Échec de l'analyse du reçu par Gemini", e)
      throw e
    }
  }

  /**
   * Scales down large camera or gallery bitmaps to max dimension (e.g. 1024px),
   * ensuring clear text legibility for OCR while reducing upload payload size by up to 90%.
   */
  private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= maxDimension && height <= maxDimension) {
      return bitmap
    }
    val ratio = width.toFloat() / height.toFloat()
    val targetWidth: Int
    val targetHeight: Int
    if (width > height) {
      targetWidth = maxDimension
      targetHeight = (maxDimension / ratio).toInt().coerceAtLeast(1)
    } else {
      targetHeight = maxDimension
      targetWidth = (maxDimension * ratio).toInt().coerceAtLeast(1)
    }
    Log.d(TAG, "Optimisation bitmap pour Gemini AI: ${width}x${height} -> ${targetWidth}x${targetHeight}")
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
  }

  private fun parseReceiptJson(jsonStr: String): ReceiptExtractionData {
    val json = JSONObject(jsonStr)

    val itemsList = mutableListOf<ExtractedReceiptItem>()
    val itemsArray = json.optJSONArray("items")
    if (itemsArray != null) {
      for (i in 0 until itemsArray.length()) {
        val itemObj = itemsArray.getJSONObject(i)
        itemsList.add(
          ExtractedReceiptItem(
            designation = itemObj.optString("designation", "Article $i"),
            quantity = itemObj.optDouble("quantity", 1.0),
            unit = itemObj.optString("unit", "U"),
            unitPriceHt = itemObj.optDouble("unitPriceHt", 0.0),
            totalPriceHt = itemObj.optDouble("totalPriceHt", 0.0)
          )
        )
      }
    }

    return ReceiptExtractionData(
      supplierName = json.optString("supplierName", ""),
      nif = json.optString("nif", ""),
      nis = json.optString("nis", ""),
      rcNumber = json.optString("rcNumber", ""),
      articleImposition = json.optString("articleImposition", ""),
      supplierAddress = json.optString("supplierAddress", ""),
      supplierPhone = json.optString("supplierPhone", ""),
      documentType = json.optString("documentType", "FACTURE"),
      documentNumber = json.optString("documentNumber", ""),
      documentDate = json.optString("documentDate", ""),
      totalHtDzd = json.optDouble("totalHtDzd", 0.0),
      tvaRate = json.optDouble("tvaRate", 19.0),
      totalTvaDzd = json.optDouble("totalTvaDzd", 0.0),
      timbreFiscalDzd = json.optDouble("timbreFiscalDzd", 0.0),
      totalTtcDzd = json.optDouble("totalTtcDzd", 0.0),
      paymentMethod = json.optString("paymentMethod", "ESPECES"),
      items = itemsList,
      summaryNotes = json.optString("summaryNotes", "")
    )
  }
}
