package com.example

import com.example.core.utils.AlgerianFiscalValidator
import com.example.core.utils.CurrencyFormatter
import com.example.core.utils.ExportService
import com.example.data.local.entities.SettingsEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProcureToPayUnitTest {

  @Test
  fun testAlgerianNifValidation() {
    // Valid 15 digits
    assertTrue(AlgerianFiscalValidator.validateNif("001916000987654"))
    // With spaces
    assertTrue(AlgerianFiscalValidator.validateNif("0019 1600 0987 654"))
    // Invalid length (< 15)
    assertFalse(AlgerianFiscalValidator.validateNif("001916000987"))
    // Invalid characters
    assertFalse(AlgerianFiscalValidator.validateNif("00191600098765A"))
    // Empty
    assertFalse(AlgerianFiscalValidator.validateNif(""))
  }

  @Test
  fun testAlgerianRibValidationAndFormatting() {
    val rib = "00100612012345678912" // 20 digits
    assertTrue(AlgerianFiscalValidator.validateRib(rib))
    assertFalse(AlgerianFiscalValidator.validateRib("0010061201234567891")) // 19 digits

    val formatted = AlgerianFiscalValidator.formatRib(rib)
    assertEquals("001 00612 0123456789 12", formatted)
  }

  @Test
  fun testAlgerianPhoneValidation() {
    assertTrue(AlgerianFiscalValidator.validatePhone("023924510")) // Fixed Alger
    assertTrue(AlgerianFiscalValidator.validatePhone("0550123456")) // Mobilis
    assertTrue(AlgerianFiscalValidator.validatePhone("0661123456")) // Djezzy
    assertTrue(AlgerianFiscalValidator.validatePhone("0770123456")) // Ooredoo
    assertTrue(AlgerianFiscalValidator.validatePhone("+21323924510")) // International
    assertFalse(AlgerianFiscalValidator.validatePhone("123456")) // Too short
  }

  @Test
  fun testCurrencyFormatterDzd() {
    val formatted = CurrencyFormatter.formatDzd(1250000.50)
    assertTrue(formatted.contains("1 250 000,50") || formatted.contains("1250000,50"))
    assertTrue(formatted.endsWith("DA"))
  }

  @Test
  fun testNumberToWordsDzd() {
    val words = CurrencyFormatter.numberToWordsDzdFr(1000.0)
    assertTrue(words.contains("mille Dinars Algériens", ignoreCase = true))

    val wordsWithCents = CurrencyFormatter.numberToWordsDzdFr(500.50)
    assertTrue(wordsWithCents.contains("Dinars Algériens"))
    assertTrue(wordsWithCents.contains("Centimes"))
  }

  @Test
  fun testFeatureFlagDefaultWarehouseDisabled() {
    val defaultSettings = SettingsEntity()
    // Project-directed purchasing principle: Warehouse disabled at launch
    assertFalse(defaultSettings.isWarehouseModuleEnabled)
  }

  @Test
  fun testExportServiceCsvGeneration() {
    val headers = listOf("Nom", "Wilaya", "Montant")
    val data = listOf(
      Triple("SARL BTP", "Alger", "1500000"),
      Triple("EURL Deco; Batiment", "Oran", "350000")
    )
    val csv = ExportService.generateCsv(headers, data) {
      listOf(it.first, it.second, it.third)
    }

    assertTrue(csv.startsWith("\uFEFF")) // BOM for Excel
    assertTrue(csv.contains("SARL BTP;Alger;1500000"))
    assertTrue(csv.contains("\"EURL Deco; Batiment\";Oran;350000")) // Semi-colon properly escaped in quotes
  }

  @Test
  fun testPurchaseOrderDaLinkage() {
    val da = com.example.data.local.entities.PurchaseRequestEntity(
      id = "da-test-123",
      daNumber = "DA-2026-0088",
      requesterName = "Chef de Chantier",
      projectId = "prj-01",
      projectName = "Chantier Alger",
      totalEstimatedDzd = 1500000.0
    )

    val bc = com.example.data.local.entities.PurchaseOrderEntity(
      id = "bc-test-456",
      bcNumber = "BC-2026-0044",
      purchaseRequestId = da.id,
      daNumber = da.daNumber,
      supplierId = "sup-01",
      supplierName = "Fournisseur BTP",
      projectId = da.projectId,
      projectName = da.projectName,
      orderDate = "25/10/2026",
      totalTtcDzd = 1500000.0
    )

    assertEquals("da-test-123", bc.purchaseRequestId)
    assertEquals("DA-2026-0088", bc.daNumber)
    assertEquals(da.projectId, bc.projectId)
  }
}
