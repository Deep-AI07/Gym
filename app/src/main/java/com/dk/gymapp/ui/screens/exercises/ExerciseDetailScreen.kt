package com.dk.gymapp.ui.screens.exercises

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.screens.admin.AdminExerciseDialog
import com.dk.gymapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exercise: Exercise,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    isAdmin: Boolean = false,
    onUpdateExercise: ((Exercise) -> Unit)? = null,
    onDeleteExercise: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    // Interactive 3-Level Selector: Beginner (0), Intermediate (1), Advanced (2)
    var selectedLevelIndex by remember { mutableIntStateOf(1) } // Default to Intermediate
    val levels = listOf("Beginner", "Intermediate", "Advanced")

    var showAdminEditDialog by remember { mutableStateOf(false) }

    val exoPlayer = remember(exercise.videoFileName) {
        if (exercise.videoFileName.isNotBlank()) {
            ExoPlayer.Builder(context).build().apply {
                val assetUri = "asset:///${exercise.videoFileName}"
                setMediaItem(MediaItem.fromUri(assetUri))
                prepare()
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
                playWhenReady = false
            }
        } else {
            null
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
        }
    }

    // Dynamic metrics based on selected tier
    val dynamicSets = when (selectedLevelIndex) {
        0 -> 3
        1 -> exercise.setsRecommendation
        else -> 5
    }
    val dynamicReps = when (selectedLevelIndex) {
        0 -> "10-12"
        1 -> exercise.repsRecommendation
        else -> "3-6 (Heavy)"
    }
    val dynamicRest = when (selectedLevelIndex) {
        0 -> 60
        1 -> exercise.restTimeSeconds
        else -> 120
    }
    val currentLevelGuide = when (selectedLevelIndex) {
        0 -> exercise.beginnerGuide.ifBlank { "Focus on mastering strict bar path, controlled 3-second descent, and full range of motion with lighter loads." }
        1 -> exercise.intermediateGuide.ifBlank { "Implement progressive overload, pause briefly at contraction point, and train at 70-80% of your 1-rep maximum." }
        else -> exercise.advancedGuide.ifBlank { "Heavy compound intensity protocols: use 85-90% 1RM, explosive concentric drive, drop sets, and rest-pause intervals." }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(exercise.id) }) {
                        Icon(
                            imageVector = if (exercise.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (exercise.isFavorite) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isAdmin) {
                        IconButton(onClick = { showAdminEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Exercise", tint = GymGreen)
                        }
                    }
                },
                windowInsets = WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 32.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // VIDEO DEMONSTRATION / MEDIA BANNER
            item {
                if (exoPlayer != null) {
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "OFFLINE VIDEO DEMONSTRATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black)
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    PlayerView(ctx).apply {
                                        player = exoPlayer
                                        useController = true
                                    }
                                }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(DarkSurfaceBorder, DarkSurfaceCard)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = GymGreen,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${exercise.muscleGroup} • ${exercise.equipment}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // EXTERNAL VIDEO LINK ACTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (exercise.videoUrl.isNotBlank()) {
                        Button(
                            onClick = {
                                val trimmed = exercise.videoUrl.trim()
                                val videoId = when {
                                    trimmed.contains("v=") -> trimmed.substringAfter("v=").substringBefore("&")
                                    trimmed.contains("youtu.be/") -> trimmed.substringAfter("youtu.be/").substringBefore("?")
                                    else -> ""
                                }

                                var launched = false
                                if (videoId.isNotBlank()) {
                                    try {
                                        // Attempt to launch directly in YouTube app
                                        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(appIntent)
                                        launched = true
                                    } catch (_: Exception) {}
                                }

                                if (!launched && trimmed.isNotBlank()) {
                                    try {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmed)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(webIntent)
                                        launched = true
                                    } catch (_: Exception) {}
                                }

                                if (!launched) {
                                    try {
                                        val query = Uri.encode("${exercise.name} form tutorial")
                                        val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(searchIntent)
                                    } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WATCH HD VIDEO TUTORIAL",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val query = Uri.encode("${exercise.name} exercise form tutorial")
                                val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(searchIntent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SEARCH TUTORIALS ON YOUTUBE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 3-LEVEL DIFFICULTY TIER SELECTOR (BEGINNER, INTERMEDIATE, ADVANCED)
            item {
                GymCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = GymGreen.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "SELECT DIFFICULTY LEVEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = GymGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3-Tab Tier Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        levels.forEachIndexed { index, levelName ->
                            val isSelected = selectedLevelIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GymGreen else Color.Transparent)
                                    .clickable { selectedLevelIndex = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = levelName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ADAPTIVE METRICS (SETS / REPS / REST)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$dynamicSets",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = GymGreen
                            )
                            Text("Sets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dynamicReps,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = AccentCyan
                            )
                            Text("Reps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dynamicRest}s",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = AccentOrange
                            )
                            Text("Rest", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // LEVEL TRAINING DIRECTIVE
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentLevelGuide,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // TARGET MUSCLE & EQUIPMENT
            item {
                GymCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "PRIMARY TARGET: ${exercise.muscleGroup.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GymGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    if (exercise.secondaryMuscles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Secondary:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            exercise.secondaryMuscles.forEach { sec ->
                                MuscleBadge(muscle = sec, isPrimary = false)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Equipment Required: ${exercise.equipment}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // STEP-BY-STEP INSTRUCTIONS
            if (exercise.instructions.isNotEmpty()) {
                item {
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EXECUTION FORM & INSTRUCTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = GymGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        exercise.instructions.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(GymGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GymGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // SAFETY TIPS
            if (exercise.safetyTips.isNotEmpty()) {
                item {
                    GymCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = GymGreen.copy(alpha = 0.3f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GymGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SAFETY TIPS",
                                style = MaterialTheme.typography.labelSmall,
                                color = GymGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        exercise.safetyTips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // COMMON MISTAKES
            if (exercise.commonMistakes.isNotEmpty()) {
                item {
                    GymCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = AccentRed.copy(alpha = 0.3f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMMON MISTAKES TO AVOID",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        exercise.commonMistakes.forEach { mistake ->
                            Text(
                                text = "✗ $mistake",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ADMIN EDIT DIALOG
    if (showAdminEditDialog) {
        AdminExerciseDialog(
            exerciseToEdit = exercise,
            onDismiss = { showAdminEditDialog = false },
            onSave = { updated ->
                onUpdateExercise?.invoke(updated)
                showAdminEditDialog = false
            },
            onDelete = { id ->
                onDeleteExercise?.invoke(id)
                showAdminEditDialog = false
                onBack()
            }
        )
    }
}
