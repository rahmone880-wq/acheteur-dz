package com.example.core.designsystem.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AppDatePicker(
  value: String,
  onDateSelected: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String = "JJ/MM/AAAA",
  errorMessage: String? = null
) {
  val context = LocalContext.current
  val calendar = Calendar.getInstance()

  val openDatePicker = {
    val datePicker = DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val selected = Calendar.getInstance().apply {
          set(year, month, dayOfMonth)
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        onDateSelected(sdf.format(selected.time))
      },
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePicker.show()
  }

  Box(modifier = modifier) {
    AppTextField(
      value = value,
      onValueChange = onDateSelected,
      label = label,
      placeholder = placeholder,
      errorMessage = errorMessage,
      readOnly = true,
      trailingIcon = {
        IconButton(onClick = openDatePicker) {
          Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Sélectionner la date")
        }
      },
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = openDatePicker)
    )
  }
}
