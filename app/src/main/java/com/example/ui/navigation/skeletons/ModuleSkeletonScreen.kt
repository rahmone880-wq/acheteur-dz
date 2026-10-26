package com.example.ui.navigation.skeletons

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Reusable animated placeholder box that renders a pulsating skeleton line/rect.
 */
@Composable
fun SkeletonPlaceholder(
  modifier: Modifier = Modifier,
  shape: RoundedCornerShape = RoundedCornerShape(6.dp)
) {
  val infiniteTransition = rememberInfiniteTransition(label = "skeleton_pulse")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.70f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "skeleton_alpha"
  )

  Box(
    modifier = modifier
      .clip(shape)
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
  )
}

/**
 * Empty screen skeleton for the Dashboard module.
 */
@Composable
fun DashboardSkeleton(
  modifier: Modifier = Modifier,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("skeleton_dashboard"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // KPI Cards Skeleton Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SkeletonPlaceholder(modifier = Modifier.size(24.dp).clip(CircleShape))
          Spacer(modifier = Modifier.height(10.dp))
          SkeletonPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f))
          Spacer(modifier = Modifier.height(6.dp))
          SkeletonPlaceholder(modifier = Modifier.height(20.dp).fillMaxWidth(0.9f))
        }
      }
      Card(
        modifier = Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          SkeletonPlaceholder(modifier = Modifier.size(24.dp).clip(CircleShape))
          Spacer(modifier = Modifier.height(10.dp))
          SkeletonPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f))
          Spacer(modifier = Modifier.height(6.dp))
          SkeletonPlaceholder(modifier = Modifier.height(20.dp).fillMaxWidth(0.9f))
        }
      }
    }

    // Chart / Timeline Banner Skeleton
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        SkeletonPlaceholder(modifier = Modifier.height(18.dp).fillMaxWidth(0.45f))
        Spacer(modifier = Modifier.height(12.dp))
        SkeletonPlaceholder(modifier = Modifier.height(90.dp).fillMaxWidth())
      }
    }

    // Empty state callout
    EmptyStateNotice(
      icon = Icons.Default.Dashboard,
      title = "Tableau de Bord en Attente de Données",
      description = "Dès la création de vos premiers chantiers, fournisseurs et commandes, les indicateurs d'achats et graphiques de dépenses DZD s'actualiseront automatiquement.",
      actionLabel = "Initialiser les données d'exemple",
      onActionClick = onActionClick,
      testTag = "dashboard_skeleton_empty_notice"
    )
  }
}

/**
 * Empty screen skeleton for the Achats (Procurement) module.
 */
@Composable
fun AchatsSkeleton(
  modifier: Modifier = Modifier,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("skeleton_achats"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Stage chips skeleton
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      repeat(3) {
        SkeletonPlaceholder(
          modifier = Modifier
            .weight(1f)
            .height(34.dp),
          shape = RoundedCornerShape(18.dp)
        )
      }
    }

    // Card Skeletons (DA / BC / Facture items)
    repeat(2) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            SkeletonPlaceholder(modifier = Modifier.height(16.dp).width(110.dp))
            SkeletonPlaceholder(
              modifier = Modifier.height(20.dp).width(80.dp),
              shape = RoundedCornerShape(10.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          SkeletonPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.7f))
          Spacer(modifier = Modifier.height(6.dp))
          SkeletonPlaceholder(modifier = Modifier.height(12.dp).fillMaxWidth(0.5f))
        }
      }
    }

    // Notice
    EmptyStateNotice(
      icon = Icons.Default.ShoppingCart,
      title = "Aucun Achat en Cours",
      description = "Gérez le cycle complet Procure-to-Pay conforme à la réglementation algérienne : Demande d'Achat (DA), Bon de Commande (BC), Réception sur Chantier et Facture.",
      actionLabel = "Créer une Demande d'Achat",
      onActionClick = onActionClick,
      testTag = "achats_skeleton_empty_notice"
    )
  }
}

/**
 * Empty screen skeleton for the Fournisseurs (Suppliers) module.
 */
@Composable
fun FournisseursSkeleton(
  modifier: Modifier = Modifier,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("skeleton_fournisseurs"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Search bar skeleton
    SkeletonPlaceholder(
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(24.dp)
    )

    // Supplier Item Skeletons
    repeat(2) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SkeletonPlaceholder(
            modifier = Modifier.size(44.dp),
            shape = CircleShape
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            SkeletonPlaceholder(modifier = Modifier.height(16.dp).fillMaxWidth(0.6f))
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonPlaceholder(modifier = Modifier.height(12.dp).fillMaxWidth(0.85f))
          }
        }
      }
    }

    // Notice
    EmptyStateNotice(
      icon = Icons.Default.Business,
      title = "Répertoire Fournisseurs Vide",
      description = "Enregistrez vos fournisseurs locaux et sous-traitants BTP avec leurs mentions fiscales obligatoires (NIF à 15 chiffres, NIS, RC, Article d'Imposition).",
      actionLabel = "Ajouter un Fournisseur",
      onActionClick = onActionClick,
      testTag = "fournisseurs_skeleton_empty_notice"
    )
  }
}

/**
 * Empty screen skeleton for the Projets (Chantiers) module.
 */
@Composable
fun ProjetsSkeleton(
  modifier: Modifier = Modifier,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("skeleton_projets"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Project KPI skeleton
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        SkeletonPlaceholder(modifier = Modifier.height(16.dp).fillMaxWidth(0.5f))
        Spacer(modifier = Modifier.height(10.dp))
        SkeletonPlaceholder(modifier = Modifier.height(24.dp).fillMaxWidth(0.8f))
      }
    }

    // Project card skeleton
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        SkeletonPlaceholder(modifier = Modifier.height(18.dp).fillMaxWidth(0.65f))
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonPlaceholder(modifier = Modifier.height(8.dp).fillMaxWidth(), shape = RoundedCornerShape(4.dp))
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonPlaceholder(modifier = Modifier.height(14.dp).fillMaxWidth(0.4f))
      }
    }

    // Notice
    EmptyStateNotice(
      icon = Icons.Default.Architecture,
      title = "Aucun Projet ou Chantier Actif",
      description = "Créez vos projets de construction ou d'exploitation pour affecter précisément chaque bon de commande et contrôler les dépassements budgétaires.",
      actionLabel = "Créer un Nouveau Projet",
      onActionClick = onActionClick,
      testTag = "projets_skeleton_empty_notice"
    )
  }
}

/**
 * Empty screen skeleton for the Plus module.
 */
@Composable
fun PlusSkeleton(
  modifier: Modifier = Modifier,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("skeleton_plus"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    repeat(3) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          SkeletonPlaceholder(modifier = Modifier.size(36.dp), shape = CircleShape)
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            SkeletonPlaceholder(modifier = Modifier.height(16.dp).fillMaxWidth(0.5f))
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonPlaceholder(modifier = Modifier.height(12.dp).fillMaxWidth(0.75f))
          }
        }
      }
    }

    EmptyStateNotice(
      icon = Icons.Default.MoreHoriz,
      title = "Services & Outils Complémentaires",
      description = "Accédez au catalogue d'articles, au scanner IA de reçus et factures, au journal d'audit légal et aux paramètres de fiscalité d'entreprise.",
      actionLabel = "Explorer les modules",
      onActionClick = onActionClick,
      testTag = "plus_skeleton_empty_notice"
    )
  }
}

/**
 * Helper empty state callout composable with structured visual hierarchy.
 */
@Composable
fun EmptyStateNotice(
  icon: ImageVector,
  title: String,
  description: String,
  actionLabel: String? = null,
  onActionClick: (() -> Unit)? = null,
  testTag: String = "empty_state_notice"
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(30.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
      )

      if (actionLabel != null && onActionClick != null) {
        Spacer(modifier = Modifier.height(18.dp))
        Button(
          onClick = onActionClick,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.testTag("${testTag}_button")
        ) {
          Text(actionLabel)
        }
      }
    }
  }
}

/**
 * Full-screen wrapper for previewing or inspecting any module's empty skeleton.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleSkeletonScreen(
  moduleKey: String,
  onNavigateBack: () -> Unit,
  onAction: () -> Unit = {}
) {
  val title = when (moduleKey.lowercase()) {
    "dashboard" -> "Dashboard (Squelette)"
    "achats", "procurement" -> "Achats (Squelette)"
    "fournisseurs", "suppliers" -> "Fournisseurs (Squelette)"
    "projets", "projects" -> "Projets (Squelette)"
    "plus" -> "Plus (Squelette)"
    else -> "Module (Squelette)"
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      when (moduleKey.lowercase()) {
        "dashboard" -> DashboardSkeleton(onActionClick = onAction)
        "achats", "procurement" -> AchatsSkeleton(onActionClick = onAction)
        "fournisseurs", "suppliers" -> FournisseursSkeleton(onActionClick = onAction)
        "projets", "projects" -> ProjetsSkeleton(onActionClick = onAction)
        "plus" -> PlusSkeleton(onActionClick = onAction)
        else -> DashboardSkeleton(onActionClick = onAction)
      }
    }
  }
}

/**
 * Standalone initial empty screen for Dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardEmptyScreen(
  onNavigateBack: (() -> Unit)? = null,
  onActionClick: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Dashboard (Initial)", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    DashboardSkeleton(
      modifier = Modifier.padding(padding),
      onActionClick = onActionClick
    )
  }
}

/**
 * Standalone initial empty screen for Achats (Procurement).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchatsEmptyScreen(
  onNavigateBack: (() -> Unit)? = null,
  onActionClick: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Achats (Initial)", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    AchatsSkeleton(
      modifier = Modifier.padding(padding),
      onActionClick = onActionClick
    )
  }
}

/**
 * Standalone initial empty screen for Fournisseurs (Suppliers).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FournisseursEmptyScreen(
  onNavigateBack: (() -> Unit)? = null,
  onActionClick: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Fournisseurs (Initial)", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    FournisseursSkeleton(
      modifier = Modifier.padding(padding),
      onActionClick = onActionClick
    )
  }
}

/**
 * Standalone initial empty screen for Projets (Projects).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetsEmptyScreen(
  onNavigateBack: (() -> Unit)? = null,
  onActionClick: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Projets (Initial)", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    ProjetsSkeleton(
      modifier = Modifier.padding(padding),
      onActionClick = onActionClick
    )
  }
}

/**
 * Standalone initial empty screen for Plus.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusEmptyScreen(
  onNavigateBack: (() -> Unit)? = null,
  onActionClick: () -> Unit = {}
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Plus (Initial)", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    PlusSkeleton(
      modifier = Modifier.padding(padding),
      onActionClick = onActionClick
    )
  }
}

