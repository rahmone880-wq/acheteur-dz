package com.example.core.utils

import android.content.Context
import android.content.Intent

object ExportService {

  /**
   * Generic CSV builder for any list of entities
   */
  fun <T> generateCsv(
    headers: List<String>,
    items: List<T>,
    rowMapper: (T) -> List<String>
  ): String {
    val sb = StringBuilder()
    // UTF-8 BOM for Excel compatibility with accents and Arabic characters
    sb.append('\uFEFF')
    sb.append(headers.joinToString(";") { escapeCsv(it) })
    sb.append("\n")

    for (item in items) {
      val row = rowMapper(item)
      sb.append(row.joinToString(";") { escapeCsv(it) })
      sb.append("\n")
    }
    return sb.toString()
  }

  private fun escapeCsv(value: String): String {
    var v = value.replace("\n", " ").replace("\r", " ")
    if (v.contains(";") || v.contains("\"")) {
      v = "\"" + v.replace("\"", "\"\"") + "\""
    }
    return v
  }

  /**
   * Share content (CSV, text report, or formatted sheet) via WhatsApp, Email, Drive, or file
   */
  fun shareExport(context: Context, title: String, content: String, mimeType: String = "text/plain") {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TITLE, title)
      putExtra(Intent.EXTRA_SUBJECT, title)
      putExtra(Intent.EXTRA_TEXT, content)
      type = mimeType
    }
    val chooser = Intent.createChooser(sendIntent, title)
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
  }
}
