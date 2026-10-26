package com.example.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.core.utils.CurrencyFormatter

@Composable
fun AmountText(
  amount: Double,
  modifier: Modifier = Modifier,
  style: TextStyle = MaterialTheme.typography.titleMedium,
  fontWeight: FontWeight = FontWeight.Bold,
  color: Color = MaterialTheme.colorScheme.primary,
  includeDecimals: Boolean = true
) {
  val formatted = if (includeDecimals) {
    CurrencyFormatter.formatDzd(amount)
  } else {
    CurrencyFormatter.formatDzdNoDecimals(amount)
  }

  Text(
    text = formatted,
    style = style.copy(fontWeight = fontWeight),
    color = color,
    modifier = modifier
  )
}
