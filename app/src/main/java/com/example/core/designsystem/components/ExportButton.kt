package com.example.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

enum class ExportFormat {
  CSV,
  EXCEL,
  PDF
}

@Composable
fun ExportButton(
  onExport: (ExportFormat) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "Exporter",
  testTag: String = "export_button"
) {
  var menuExpanded by remember { mutableStateOf(false) }

  FilledTonalButton(
    onClick = { menuExpanded = true },
    modifier = modifier.testTag(testTag)
  ) {
    Icon(
      imageVector = Icons.Default.Share,
      contentDescription = label
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(label)

    DropdownMenu(
      expanded = menuExpanded,
      onDismissRequest = { menuExpanded = false }
    ) {
      DropdownMenuItem(
        text = { Text("Exporter en CSV (Excel)") },
        onClick = {
          menuExpanded = false
          onExport(ExportFormat.CSV)
        }
      )
      DropdownMenuItem(
        text = { Text("Exporter en XLSX") },
        onClick = {
          menuExpanded = false
          onExport(ExportFormat.EXCEL)
        }
      )
      DropdownMenuItem(
        text = { Text("Générer document PDF") },
        onClick = {
          menuExpanded = false
          onExport(ExportFormat.PDF)
        }
      )
    }
  }
}
