package com.dk.gymapp.ui.screens.workouts

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import com.dk.gymapp.data.model.WorkoutExercise
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.data.model.WorkoutPlan
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    workout: WorkoutPlan,
    allExercises: List<Exercise>,
    onFinishWorkout: (WorkoutHistory) -> Unit,
    onCancelWorkout: () -> Unit
) {
    val context = LocalContext.current

    // Session State
    var isPaused by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var currentExerciseIndex by remember { mutableIntStateOf(0) }
    var isResting by remember { mutableStateOf(false) }
    var restTimeRemaining by remember { mutableIntStateOf(60) }
    var initialRestSeconds by remember { mutableIntStateOf(60) }
    var isWorkoutComplete by remember { mutableStateOf(false) }

    // User completion notes & rating
    var workoutNotes by remember { mutableStateOf("") }
    var workoutRating by remember { mutableIntStateOf(5) }
    var showQuitConfirmation by remember { mutableStateOf(false) }

    // Track sets status per exercise: exerciseIndex -> list of (weight, reps, completed)
    val exerciseSetsState = remember(workout) {
        val list = mutableStateListOf<MutableList<SetRecord>>()
        workout.exercises.forEach { ex ->
            val setsForEx = mutableListOf<SetRecord>()
            val baseReps = ex.targetReps.split("-").firstOrNull()?.trim()?.toIntOrNull() ?: 10
            for (i in 1..ex.targetSets) {
                setsForEx.add(SetRecord(i, baseReps, ex.targetWeightKg, false))
            }
            list.add(setsForEx)
        }
        list
    }

    // ELAPSED WORKOUT TIMER
    LaunchedEffect(isPaused, isWorkoutComplete) {
        while (!isPaused && !isWorkoutComplete) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    // REST COUNTDOWN TIMER
    LaunchedEffect(isResting, restTimeRemaining) {
        if (isResting && restTimeRemaining > 0) {
            delay(1000L)
            restTimeRemaining--
            if (restTimeRemaining == 0) {
                isResting = false
                vibrateDevice(context)
            }
        }
    }

    val currentWorkoutExercise = workout.exercises.getOrNull(currentExerciseIndex)
    val currentExerciseDetail = remember(currentWorkoutExercise) {
        allExercises.find { it.id == currentWorkoutExercise?.exerciseId }
    }

    // EXOPLAYER SETUP FOR ASSET VIDEOS
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    LaunchedEffect(currentWorkoutExercise) {
        val videoName = currentWorkoutExercise?.videoFileName ?: ""
        if (videoName.isNotBlank()) {
            val assetUri = "asset:///$videoName"
            exoPlayer.setMediaItem(MediaItem.fromUri(assetUri))
            exoPlayer.prepare()
            exoPlayer.play()
        } else {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // COMPLETION SCREEN OVERLAY
    if (isWorkoutComplete) {
        val totalCompletedSets = exerciseSetsState.sumOf { sets -> sets.count { it.completed } }
        val completedExercisesCount = exerciseSetsState.count { sets -> sets.any { it.completed } }
        val caloriesBurned = (elapsedSeconds * 0.14).toInt().coerceAtLeast(50)

        WorkoutCompletionDialog(
            workoutTitle = workout.title,
            durationSeconds = elapsedSeconds,
            totalSets = totalCompletedSets,
            completedExercises = completedExercisesCount,
            caloriesBurned = caloriesBurned,
            notes = workoutNotes,
            onNotesChange = { workoutNotes = it },
            rating = workoutRating,
            onRatingChange = { workoutRating = it },
            onSaveAndClose = {
                val history = WorkoutHistory(
                    workoutId = workout.id,
                    workoutTitle = workout.title,
                    dateMillis = System.currentTimeMillis(),
                    durationSeconds = elapsedSeconds,
                    caloriesBurned = caloriesBurned,
                    exercisesCompleted = completedExercisesCount,
                    totalSets = totalCompletedSets,
                    notes = workoutNotes,
                    rating = workoutRating
                )
                onFinishWorkout(history)
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = workout.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        val mins = elapsedSeconds / 60
                        val secs = elapsedSeconds % 60
                        val formattedTime = String.format("%02d:%02d", mins, secs)
                        Text(
                            text = "Elapsed: $formattedTime ${if (isPaused) "• PAUSED" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPaused) AccentOrange else GymGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showQuitConfirmation = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit Workout")
                    }
                },
                actions = {
                    IconButton(onClick = { isPaused = !isPaused }) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = if (isPaused) GymGreen else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    TextButton(
                        onClick = { isWorkoutComplete = true }
                    ) {
                        Text(
                            text = "FINISH",
                            color = GymGreen,
                            fontWeight = FontWeight.Black
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // WORKOUT PROGRESS BAR
            val progress = (currentExerciseIndex + 1).toFloat() / workout.exercises.size.coerceAtLeast(1)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = GymGreen,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // EXERCISE VIDEO / HEADER BANNER
                item {
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GymGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "EXERCISE ${currentExerciseIndex + 1} OF ${workout.exercises.size}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GymGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (currentExerciseDetail != null) {
                                MuscleBadge(muscle = currentExerciseDetail.muscleGroup, isPrimary = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentWorkoutExercise?.exerciseName ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // VIDEO PLAYER OR PLACEHOLDER
                        if (currentWorkoutExercise?.videoFileName?.isNotBlank() == true) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                AndroidView(
                                    modifier = Modifier.fillMaxSize(),
                                    factory = { ctx ->
                                        PlayerView(ctx).apply {
                                            player = exoPlayer
                                            useController = false
                                        }
                                    }
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
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
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${currentWorkoutExercise?.targetSets} Sets • ${currentWorkoutExercise?.targetReps} Reps • ${currentWorkoutExercise?.restSeconds}s Rest",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // REST TIMER CARD (VISIBLE WHEN RESTING)
                item {
                    AnimatedVisibility(
                        visible = isResting,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        GymCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = AccentCyan.copy(alpha = 0.5f),
                            backgroundColor = DarkSurfaceCard
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "REST INTERVAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val restFraction = if (initialRestSeconds > 0) restTimeRemaining.toFloat() / initialRestSeconds else 0f
                                MetricProgressRing(
                                    progress = restFraction,
                                    size = 110.dp,
                                    strokeWidth = 10.dp,
                                    activeColor = AccentCyan
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$restTimeRemaining",
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "SECONDS",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { restTimeRemaining += 30 },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("+30s", color = AccentCyan)
                                    }

                                    OutlinedButton(
                                        onClick = { restTimeRemaining = (restTimeRemaining - 15).coerceAtLeast(0) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("-15s", color = AccentCyan)
                                    }

                                    Button(
                                        onClick = { isResting = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Skip Rest", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // SET LOGGING TABLE
                item {
                    Text(
                        text = "LOG SETS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                val currentSets = exerciseSetsState.getOrNull(currentExerciseIndex) ?: mutableListOf()
                itemsIndexed(currentSets) { setIndex, setRecord ->
                    GymCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (setRecord.completed) GymGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        backgroundColor = if (setRecord.completed) GymGreen.copy(alpha = 0.08f) else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (setRecord.completed) GymGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${setIndex + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (setRecord.completed) Color.Black else MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "${setRecord.reps} Reps  •  ${setRecord.weightKg} kg",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (setRecord.completed) "Completed" else "Target: ${currentWorkoutExercise?.targetReps} reps",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (setRecord.completed) GymGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Increment/decrement reps quick buttons
                                IconButton(
                                    onClick = {
                                        if (setRecord.reps > 1) {
                                            currentSets[setIndex] = setRecord.copy(reps = setRecord.reps - 1)
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                IconButton(
                                    onClick = {
                                        currentSets[setIndex] = setRecord.copy(reps = setRecord.reps + 1)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                // Checkmark / Complete Set Button
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (setRecord.completed) GymGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                        .clickable {
                                            val newState = !setRecord.completed
                                            currentSets[setIndex] = setRecord.copy(completed = newState)
                                            if (newState) {
                                                // Trigger Rest Timer
                                                val restSeconds = currentWorkoutExercise?.restSeconds ?: 60
                                                initialRestSeconds = restSeconds
                                                restTimeRemaining = restSeconds
                                                isResting = true
                                                vibrateDevice(context)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete Set",
                                        tint = if (setRecord.completed) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTTOM NAVIGATION ROW (PREV / NEXT EXERCISE)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.90f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentExerciseIndex > 0) {
                                currentExerciseIndex--
                                isResting = false
                            }
                        },
                        enabled = currentExerciseIndex > 0,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    if (currentExerciseIndex < workout.exercises.size - 1) {
                        Button(
                            onClick = {
                                currentExerciseIndex++
                                isResting = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                        ) {
                            Text("Next Exercise", color = Color.Black, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { isWorkoutComplete = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                        ) {
                            Text("Finish Workout", color = Color.Black, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    // QUIT CONFIRMATION DIALOG
    if (showQuitConfirmation) {
        AlertDialog(
            onDismissRequest = { showQuitConfirmation = false },
            title = { Text("Quit Workout?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to stop this workout? Your current progress will not be saved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showQuitConfirmation = false
                        onCancelWorkout()
                    }
                ) {
                    Text("Quit", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitConfirmation = false }) {
                    Text("Resume", color = GymGreen)
                }
            }
        )
    }
}

data class SetRecord(
    val setNumber: Int,
    var reps: Int,
    var weightKg: Double,
    var completed: Boolean
)

@Composable
fun WorkoutCompletionDialog(
    workoutTitle: String,
    durationSeconds: Int,
    totalSets: Int,
    completedExercises: Int,
    caloriesBurned: Int,
    notes: String,
    onNotesChange: (String) -> Unit,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    onSaveAndClose: () -> Unit
) {
    val mins = durationSeconds / 60
    val secs = durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", mins, secs)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, GymGreen.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GymGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = GymGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "WORKOUT CRUSHED!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = workoutTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GymGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // STATS SUMMARY ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedDuration,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = AccentCyan
                        )
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$caloriesBurned",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = AccentOrange
                        )
                        Text(
                            text = "Calories",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalSets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = GymGreen
                        )
                        Text(
                            text = "Sets Done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // STAR RATING
                Text(
                    text = "Rate Session Intensity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Default.StarOutline,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) AccentOrange else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onRatingChange(i) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NOTES INPUT
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add workout notes (e.g. new PR, felt great!)", fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSaveAndClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                ) {
                    Text(
                        text = "SAVE TO HISTORY",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

private fun vibrateDevice(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(
                VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    } catch (_: Exception) { }
}
