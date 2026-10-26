package com.example.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Reusable Material 3 text field adhering to the application design system.
 *
 * Provides consistent 12.dp rounded corners, accessible touch targets,
 * error state feedback, clear button action, and helper typography.
 */
@Composable
fun AppTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String? = null,
  helperText: String? = null,
  errorMessage: String? = null,
  isError: Boolean = errorMessage != null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  showClearButton: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  singleLine: Boolean = true,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
  readOnly: Boolean = false,
  enabled: Boolean = true,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  testTag: String = "app_text_field"
) {
  val effectiveTrailingIcon: @Composable (() -> Unit)? = when {
    trailingIcon != null -> trailingIcon
    showClearButton && value.isNotEmpty() && !readOnly && enabled -> {
      {
        IconButton(
          onClick = { onValueChange("") },
          modifier = Modifier
            .size(48.dp)
            .testTag("${testTag}_clear_button")
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Effacer",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
    else -> null
  }

  Column(modifier = modifier) {
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = { Text(label) },
      placeholder = placeholder?.let { { Text(it) } },
      leadingIcon = leadingIcon,
      trailingIcon = effectiveTrailingIcon,
      isError = isError,
      singleLine = singleLine,
      maxLines = maxLines,
      minLines = minLines,
      readOnly = readOnly,
      enabled = enabled,
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      shape = MaterialTheme.shapes.medium,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag(testTag)
    )

    if (isError && errorMessage != null) {
      Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
          .padding(start = 12.dp, top = 4.dp)
          .testTag("${testTag}_error")
      )
    } else if (helperText != null) {
      Text(
        text = helperText,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
          .padding(start = 12.dp, top = 4.dp)
          .testTag("${testTag}_helper")
      )
    }
  }
}
