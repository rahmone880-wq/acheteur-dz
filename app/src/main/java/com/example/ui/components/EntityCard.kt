package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Re-export of [com.example.core.designsystem.components.EntityCard] in the ui.components namespace.
 */
@Composable
fun EntityCard(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  shape: Shape = MaterialTheme.shapes.large,
  colors: CardColors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surface
  ),
  elevation: CardElevation = CardDefaults.cardElevation(
    defaultElevation = 1.dp,
    pressedElevation = 3.dp,
    hoveredElevation = 2.dp
  ),
  border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
  testTag: String = "entity_card",
  content: @Composable ColumnScope.() -> Unit
) {
  com.example.core.designsystem.components.EntityCard(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    testTag = testTag,
    content = content
  )
}

@Composable
fun EntityCard(
  title: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  statusChip: (@Composable () -> Unit)? = null,
  leadingIcon: (@Composable () -> Unit)? = null,
  metadata: List<Pair<String, String>> = emptyList(),
  actions: (@Composable RowScope.() -> Unit)? = null,
  enabled: Boolean = true,
  testTag: String = "entity_card",
  footer: (@Composable ColumnScope.() -> Unit)? = null
) {
  com.example.core.designsystem.components.EntityCard(
    title = title,
    onClick = onClick,
    modifier = modifier,
    subtitle = subtitle,
    statusChip = statusChip,
    leadingIcon = leadingIcon,
    metadata = metadata,
    actions = actions,
    enabled = enabled,
    testTag = testTag,
    footer = footer
  )
}
