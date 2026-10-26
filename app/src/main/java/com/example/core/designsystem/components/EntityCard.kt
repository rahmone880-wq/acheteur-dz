package com.example.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Base Material 3 card container providing consistent borders, elevation, surface colors,
 * ripple feedback, and test tags across the application.
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
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag)
      .clickable(
        enabled = enabled,
        onClick = onClick
      ),
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      content = content
    )
  }
}

/**
 * Structured overload for [EntityCard] providing an organized layout with header,
 * status badge, metadata key-values, and action bar.
 */
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
  EntityCard(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    testTag = testTag
  ) {
    // Header Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f, fill = false),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (leadingIcon != null) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            leadingIcon()
          }
        }

        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (!subtitle.isNullOrBlank()) {
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      if (statusChip != null) {
        Box(modifier = Modifier.padding(start = 8.dp)) {
          statusChip()
        }
      }
    }

    // Metadata entries
    if (metadata.isNotEmpty()) {
      Spacer(modifier = Modifier.height(12.dp))
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        metadata.forEach { (label, value) ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = label,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = value,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // Custom footer content if any
    if (footer != null) {
      Spacer(modifier = Modifier.height(12.dp))
      footer()
    }

    // Action items
    if (actions != null) {
      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        thickness = 1.dp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        content = actions
      )
    }
  }
}
