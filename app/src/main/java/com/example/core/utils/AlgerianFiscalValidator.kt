package com.example.core.utils

object AlgerianFiscalValidator {
  
  /**
   * NIF (Numéro d'Identification Fiscale): 15 numeric digits in Algeria.
   */
  fun validateNif(nif: String?): Boolean {
    if (nif.isNullOrBlank()) return false
    val clean = nif.replace(" ", "").replace("-", "")
    return clean.length == 15 && clean.all { it.isDigit() }
  }

  /**
   * NIS (Numéro d'Identification Statistique): 15 numeric digits in Algeria.
   */
  fun validateNis(nis: String?): Boolean {
    if (nis.isNullOrBlank()) return false
    val clean = nis.replace(" ", "").replace("-", "")
    return clean.length == 15 && clean.all { it.isDigit() }
  }

  /**
   * RIB (Relevé d'Identité Bancaire): exactly 20 digits in Algeria
   * Structure: Code Banque (3) + Code Guichet/Agence (5) + Numéro de Compte (10) + Clé RIB (2)
   */
  fun validateRib(rib: String?): Boolean {
    if (rib.isNullOrBlank()) return false
    val clean = rib.replace(" ", "").replace("-", "")
    if (clean.length != 20 || !clean.all { it.isDigit() }) return false
    return true
  }

  /**
   * Format RIB with standard spacing: 000 00000 0000000000 00
   */
  fun formatRib(rib: String): String {
    val clean = rib.replace(" ", "").replace("-", "")
    if (clean.length != 20) return rib
    return "${clean.substring(0, 3)} ${clean.substring(3, 8)} ${clean.substring(8, 18)} ${clean.substring(18, 20)}"
  }

  /**
   * Algerian phone validator: supports mobile (05/06/07) and fixed (02x/03x/04x) or +213 format
   */
  fun validatePhone(phone: String?): Boolean {
    if (phone.isNullOrBlank()) return false
    val clean = phone.replace(" ", "").replace("-", "").replace(".", "")
    // Match +213 / 00213 followed by 8-9 digits, or 0 followed by 8-9 digits
    val regex = Regex("^(\\+213|00213|0)[1-9][0-9]{7,8}$")
    return regex.matches(clean)
  }

  /**
   * RC (Registre de Commerce): typical format e.g. 16/00-1234567B20 or similar
   */
  fun validateRc(rc: String?): Boolean {
    if (rc.isNullOrBlank()) return false
    return rc.trim().length >= 4
  }

  /**
   * Article d'imposition (AI): usually 11 numeric digits
   */
  fun validateArticleImposition(ai: String?): Boolean {
    if (ai.isNullOrBlank()) return false
    val clean = ai.replace(" ", "").replace("/", "").replace("-", "")
    return clean.length in 8..15 && clean.all { it.isDigit() }
  }
}
