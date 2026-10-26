package com.example.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusBlacklisted
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSuspended

enum class StatusType {
  APPROVED,
  PENDING,
  SUSPENDED,
  BLACKLISTED,
  DRAFT,
  ACTIVE,
  WARNING,
  INFO,
  ERROR,
  SUCCESS,
  COMPLETED,
  CANCELLED
}

/**
 * Semantic status chip badge for displaying supplier, document, project, or audit workflow statuses.
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
  val (bgColor, textColor) = when (type) {
    StatusType.APPROVED, StatusType.ACTIVE, StatusType.SUCCESS, StatusType.COMPLETED -> {
      StatusApproved.copy(alpha = 0.15f) to StatusApproved
    }
    StatusType.PENDING, StatusType.WARNING -> {
      StatusPending.copy(alpha = 0.15f) to StatusPending
    }
    StatusType.SUSPENDED, StatusType.ERROR -> {
      StatusSuspended.copy(alpha = 0.15f) to StatusSuspended
    }
    StatusType.BLACKLISTED, StatusType.CANCELLED -> {
      StatusBlacklisted.copy(alpha = 0.15f) to StatusBlacklisted
    }
    StatusType.DRAFT -> {
      StatusDraft.copy(alpha = 0.15f) to StatusDraft
    }
    StatusType.INFO -> {
      StatusInfo.copy(alpha = 0.15f) to StatusInfo
    }
  }

  val baseModifier = modifier
    .testTag(testTag)
    .clip(RoundedCornerShape(8.dp))
    .background(bgColor)
    .then(
      if (onClick != null) {
        Modifier.clickable(onClick = onClick)
      } else {
        Modifier
      }
    )
    .padding(horizontal = 10.dp, vertical = 5.dp)

  Row(
    modifier = baseModifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    if (showDot) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
    }

    Text(
      text = text,
      color = textColor,
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
    )
  }
}

/**
 * Convenient overload accepting a status string directly from Algerian procurement models
 * (e.g. "AGREE", "EN_COURS", "SUSPENDU", "BLACKLISTE", "BROUILLON", "VALIDE", "PAYE").
 */
@Composable
fun StatusChip(
  status: String,
  modifier: Modifier = Modifier,
  showDot: Boolean = true,
  onClick: (() -> Unit)? = null,
  testTag: String = "status_chip"
) {
  val type = resolveStatusType(status)
  val displayText = formatStatusDisplay(status)
  StatusChip(
    text = displayText,
    type = type,
    modifier = modifier,
    showDot = showDot,
    onClick = onClick,
    testTag = testTag
  )
}

internal fun resolveStatusType(status: String): StatusType {
  val normalized = status.trim().uppercase()
  return when {
    normalized in listOf("AGREE", "AGRÉÉ", "VALIDE", "VALIDÉ", "APPROUVE", "APPROUVÉ", "PAYE", "PAYÉ", "ACTIF", "CONFORME", "LIVRE", "LIVRÉ", "SUCCESS", "APPROVED") -> StatusType.APPROVED
    normalized in listOf("EN_COURS", "EN_ATTENTE", "SOUMIS", "EN_ETUDE", "EN_ÉTUDE", "COMMANDE", "COMMANDÉ", "PENDING", "WARNING") -> StatusType.PENDING
    normalized in listOf("SUSPENDU", "LITIGE", "BLOQUE", "BLOQUÉ", "DANGER", "ERROR", "CRITIQUE") -> StatusType.SUSPENDED
    normalized in listOf("BLACKLISTE", "BLACKLISTÉ", "REJETE", "REJETÉ", "ANNULE", "ANNULÉ", "REFUSE", "REFUSÉ") -> StatusType.BLACKLISTED
    normalized in listOf("BROUILLON", "DRAFT", "INITIAL") -> StatusType.DRAFT
    else -> StatusType.INFO
  }
}

internal fun formatStatusDisplay(status: String): String {
  return when (status.trim().uppercase()) {
    "AGREE", "AGRÉÉ" -> "Agréé"
    "EN_COURS" -> "En cours"
    "SUSPENDU" -> "Suspendu"
    "BLACKLISTE", "BLACKLISTÉ" -> "Blacklisté"
    "BROUILLON" -> "Brouillon"
    "SOUMIS" -> "Soumis"
    "VALIDE", "VALIDÉ" -> "Validé"
    "APPROUVE", "APPROUVÉ" -> "Approuvé"
    "REJETE", "REJETÉ" -> "Rejeté"
    "ANNULE", "ANNULÉ" -> "Annulé"
    "COMMANDE", "COMMANDÉ" -> "Commandé"
    "LIVRE", "LIVRÉ" -> "Livré"
    "PAYE", "PAYÉ" -> "Payé"
    "EN_ATTENTE" -> "En attente"
    "EN_ETUDE", "EN_ÉTUDE" -> "En étude"
    "RECEPTIONNE", "RÉCEPTIONNÉ" -> "Réceptionné"
    "CLOTURE", "CLÔTURÉ" -> "Clôturé"
    "FAIBLE" -> "Faible"
    "MOYEN" -> "Moyen"
    "ELEVE", "ÉLEVÉ" -> "Élevé"
    "CRITIQUE" -> "Critique"
    else -> status.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
  }
}
