package com.example.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldAccent

@Composable
fun RatingScoreBar(
  rating: Double,
  modifier: Modifier = Modifier,
  maxStars: Int = 5,
  starSize: Dp = 18.dp,
  onRatingChanged: ((Double) -> Unit)? = null
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    for (i in 1..maxStars) {
      val isFilled = rating >= i
      val iconModifier = Modifier
        .size(starSize)
        .then(
          if (onRatingChanged != null) {
            Modifier.clickable { onRatingChanged(i.toDouble()) }
          } else {
            Modifier
          }
        )

      if (isFilled) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "Étoile $i",
          tint = GoldAccent,
          modifier = iconModifier
        )
      } else {
        Icon(
          imageVector = Icons.Outlined.StarBorder,
          contentDescription = "Étoile vide $i",
          tint = MaterialTheme.colorScheme.outlineVariant,
          modifier = iconModifier
        )
      }
    }
  }
}
