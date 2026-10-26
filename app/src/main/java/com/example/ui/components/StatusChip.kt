package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias StatusType = com.example.core.designsystem.components.StatusType

/**
 * Re-export of [com.example.core.designsystem.components.StatusChip] in the ui.components namespace.
 */
@Composable
fun StatusChip(
  text: String,
  type: StatusType,
  modifier: Modifier = Modifier,
  showDot: Boolean = true,
  onClick: (() -> Unit)? = null,
  testTag: String = "status_chip"
) {
  com.example.core.designsystem.components.StatusChip(
    text = text,
    type = type,
    modifier = modifier,
    showDot = showDot,
    onClick = onClick,
    testTag = testTag
  )
}

@Composable
fun StatusChip(
  status: String,
  modifier: Modifier = Modifier,
  showDot: Boolean = true,
  onClick: (() -> Unit)? = null,
  testTag: String = "status_chip"
) {
  com.example.core.designsystem.components.StatusChip(
    status = status,
    modifier = modifier,
    showDot = showDot,
    onClick = onClick,
    testTag = testTag
  )
}
