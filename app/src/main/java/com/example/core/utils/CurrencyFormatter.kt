package com.example.core.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

  /**
   * Format currency in Algerian Dinars: 1 234 567,89 DA
   */
  fun formatDzd(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.FRENCH).apply {
      groupingSeparator = ' '
      decimalSeparator = ','
    }
    val formatter = DecimalFormat("#,##0.00", symbols)
    return "${formatter.format(amount)} DA"
  }

  fun formatDzdNoDecimals(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.FRENCH).apply {
      groupingSeparator = ' '
    }
    val formatter = DecimalFormat("#,##0", symbols)
    return "${formatter.format(amount)} DA"
  }

  /**
   * Convert amount in Algerian Dinars to words (French) for official procurement documents
   */
  fun numberToWordsDzdFr(amount: Double): String {
    val longPart = amount.toLong()
    val cents = Math.round((amount - longPart) * 100).toInt()

    val text = convertNumberFr(longPart)
    return if (cents > 0) {
      "$text Dinars Algériens et ${convertNumberFr(cents.toLong())} Centimes"
    } else {
      "$text Dinars Algériens"
    }
  }

  private val UNITS_FR = arrayOf(
    "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix",
    "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"
  )

  private val TENS_FR = arrayOf(
    "", "dix", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingts", "quatre-vingt-dix"
  )

  private fun convertNumberFr(n: Long): String {
    if (n == 0L) return "zéro"
    if (n < 0) return "moins " + convertNumberFr(-n)

    var num = n
    var result = ""

    if (num >= 1_000_000_000) {
      val billions = num / 1_000_000_000
      result += (if (billions == 1L) "un milliard" else "${convertNumberFr(billions)} milliards") + " "
      num %= 1_000_000_000
    }

    if (num >= 1_000_000) {
      val millions = num / 1_000_000
      result += (if (millions == 1L) "un million" else "${convertNumberFr(millions)} millions") + " "
      num %= 1_000_000
    }

    if (num >= 1_000) {
      val thousands = num / 1_000
      result += (if (thousands == 1L) "mille" else "${convertNumberFr(thousands)} mille") + " "
      num %= 1_000
    }

    if (num >= 100) {
      val hundreds = num / 100
      result += (if (hundreds == 1L) "cent" else "${UNITS_FR[hundreds.toInt()]} cents") + " "
      num %= 100
    }

    if (num > 0) {
      if (num < 20) {
        result += UNITS_FR[num.toInt()]
      } else {
        val ten = (num / 10).toInt()
        val unit = (num % 10).toInt()
        result += if (ten == 7 || ten == 9) {
          "${TENS_FR[ten - 1]}-${UNITS_FR[unit + 10]}"
        } else if (unit == 1 && ten != 8) {
          "${TENS_FR[ten]} et un"
        } else if (unit > 0) {
          "${TENS_FR[ten]}-${UNITS_FR[unit]}"
        } else {
          TENS_FR[ten]
        }
      }
    }

    return result.trim().replaceFirstChar { it.uppercase() }
  }
}
