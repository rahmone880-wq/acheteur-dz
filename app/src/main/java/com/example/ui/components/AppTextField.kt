package com.example.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Re-export of [com.example.core.designsystem.components.AppTextField] in the ui.components namespace.
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
  com.example.core.designsystem.components.AppTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    modifier = modifier,
    placeholder = placeholder,
    helperText = helperText,
    errorMessage = errorMessage,
    isError = isError,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    showClearButton = showClearButton,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = singleLine,
    maxLines = maxLines,
    minLines = minLines,
    readOnly = readOnly,
    enabled = enabled,
    visualTransformation = visualTransformation,
    testTag = testTag
  )
}
