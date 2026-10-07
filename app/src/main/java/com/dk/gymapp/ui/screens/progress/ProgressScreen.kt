package com.dk.gymapp.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dk.gymapp.data.model.BodyMeasurement
import com.dk.gymapp.data.model.PersonalRecord
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProgressScreen(
    history: List<WorkoutHistory>,
    measurements: List<BodyMeasurement>,
    personalRecords: List<PersonalRecord>,
    onLogMeasurement: (BodyMeasurement) -> Unit,
    onAddPersonalRecord: (PersonalRecord) -> Unit
) {
    var showLogMeasurementDialog by remember { mutableStateOf(false) }
    var showAddPrDialog by remember { mutableStateOf(false) }

    // Summary calculations
    val totalWorkouts = history.size
    val totalMinutes = history.sumOf { it.durationSeconds } / 60
    val totalCalories = history.sumOf { it.caloriesBurned }
    val latestWeight = measurements.firstOrNull()?.weightKg ?: 78.0

    // Dynamic camera cutout safe insets across various devices (punch hole, teardrop notch, pill)
    val safeCutoutInsets = WindowInsets.safeDrawing.asPaddingValues()
    val safeTop = safeCutoutInsets.calculateTopPadding()
    val safeBottom = safeCutoutInsets.calculateBottomPadding()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 16.dp + safeCutoutInsets.calculateStartPadding(LocalLayoutDirection.current),
            end = 16.dp + safeCutoutInsets.calculateEndPadding(LocalLayoutDirection.current),
            top = safeTop + 10.dp,
            bottom = 100.dp + safeBottom
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // HEADER
        item {
            Column {
                Text(
                    text = "ANALYTICS & BODY METRICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Progress & Stats",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your workout consistency, body composition, and heavy lifting PRs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // OVERALL STATS 4-CARD GRID
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Workouts",
                        value = "$totalWorkouts",
                        subtitle = "Total Completed",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = GymGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Minutes",
                        value = "$totalMinutes",
                        subtitle = "Time Invested",
                        icon = Icons.Default.Timer,
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Calories",
                        value = "$totalCalories",
                        subtitle = "Burned Total",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Streak",
                        value = "7 Days",
                        subtitle = "Best: 14 Days",
                        icon = Icons.Default.Whatshot,
                        accentColor = AccentPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // WEEKLY WORKOUT VOLUME CHART (CANVAS)
        item {
            GymCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WORKOUT MINUTES (LAST 7 DAYS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Weekly Activity Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GymGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "AVG: 42 MIN",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = GymGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bar Chart
                val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val dayValues = listOf(45, 40, 0, 50, 42, 30, 0) // Minutes per day
                val maxMinutes = 60f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dayLabels.forEachIndexed { i, day ->
                        val value = dayValues[i]
                        val fraction = (value / maxMinutes).coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (value > 0) {
                                Text(
                                    text = "${value}m",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .fillMaxHeight(fraction.coerceAtLeast(0.04f))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (value > 0) GymGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (value > 0) GymGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (value > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // MUSCLE SPLIT TRAINING DISTRIBUTION
        item {
            GymCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "MUSCLE GROUP FOCUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val muscleStats = listOf(
                    Triple("Chest & Triceps", 0.28f, GymGreen),
                    Triple("Back & Biceps", 0.24f, AccentCyan),
                    Triple("Legs & Glutes", 0.26f, AccentOrange),
                    Triple("Shoulders & Core", 0.22f, AccentPurple)
                )

                // Segmented Progress Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    muscleStats.forEach { stat ->
                        Box(
                            modifier = Modifier
                                .weight(stat.second)
                                .fillMaxHeight()
                                .background(stat.third)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    muscleStats.forEach { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(stat.third)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stat.first,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = "${(stat.second * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = stat.third
                            )
                        }
                    }
                }
            }
        }

        // WEIGHT TREND CHART & MEASUREMENTS
        item {
            Column {
                SectionHeader(
                    title = "Body Measurements",
                    subtitle = "Current: $latestWeight kg",
                    actionText = "+ Log Today",
                    onActionClick = { showLogMeasurementDialog = true }
                )

                GymCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "BODYWEIGHT TREND",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Weight Line Graph Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        val weights = measurements.reversed().map { it.weightKg.toFloat() }
                        if (weights.size >= 2) {
                            val minW = (weights.minOrNull() ?: 70f) - 1f
                            val maxW = (weights.maxOrNull() ?: 85f) + 1f
                            val range = (maxW - minW).coerceAtLeast(1f)

                            val stepX = size.width / (weights.size - 1)
                            val points = weights.mapIndexed { index, w ->
                                val x = index * stepX
                                val y = size.height - ((w - minW) / range) * size.height
                                Offset(x, y)
                            }

                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }

                            drawPath(
                                path = path,
                                color = GymGreen,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            points.forEach { pt ->
                                drawCircle(
                                    color = GymGreen,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // LATEST MEASUREMENT BREAKDOWN TABLE
                    val latestM = measurements.firstOrNull()
                    if (latestM != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Chest", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${latestM.chestCm} cm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Waist", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${latestM.waistCm} cm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Arms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${latestM.armsCm} cm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Legs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${latestM.legsCm} cm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            }
        }

        // PERSONAL RECORDS (PRs) SECTION
        item {
            Column {
                SectionHeader(
                    title = "Personal Records (PRs)",
                    subtitle = "All-time maximum lifts",
                    actionText = "+ Add PR",
                    onActionClick = { showAddPrDialog = true }
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    personalRecords.forEach { pr ->
                        val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(pr.dateMillis))
                        GymCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = AccentOrange.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = pr.exerciseName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Recorded: $dateFormatted",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = AccentOrange,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${pr.recordValue} ${pr.unit}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = GymGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // LOG MEASUREMENT DIALOG
    if (showLogMeasurementDialog) {
        var weightInput by remember { mutableStateOf("$latestWeight") }
        var chestInput by remember { mutableStateOf("104.0") }
        var waistInput by remember { mutableStateOf("81.0") }
        var armsInput by remember { mutableStateOf("38.5") }
        var legsInput by remember { mutableStateOf("58.0") }

        AlertDialog(
            onDismissRequest = { showLogMeasurementDialog = false },
            title = { Text("Log Body Measurements", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = chestInput,
                        onValueChange = { chestInput = it },
                        label = { Text("Chest (cm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = waistInput,
                        onValueChange = { waistInput = it },
                        label = { Text("Waist (cm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = armsInput,
                        onValueChange = { armsInput = it },
                        label = { Text("Arms (cm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = legsInput,
                        onValueChange = { legsInput = it },
                        label = { Text("Legs (cm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val m = BodyMeasurement(
                            dateMillis = System.currentTimeMillis(),
                            weightKg = weightInput.toDoubleOrNull() ?: 78.0,
                            chestCm = chestInput.toDoubleOrNull() ?: 100.0,
                            waistCm = waistInput.toDoubleOrNull() ?: 80.0,
                            armsCm = armsInput.toDoubleOrNull() ?: 38.0,
                            legsCm = legsInput.toDoubleOrNull() ?: 58.0
                        )
                        onLogMeasurement(m)
                        showLogMeasurementDialog = false
                    }
                ) {
                    Text("Save", color = GymGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogMeasurementDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // ADD PR DIALOG
    if (showAddPrDialog) {
        var exerciseNameInput by remember { mutableStateOf("") }
        var recordValueInput by remember { mutableStateOf("") }
        var unitInput by remember { mutableStateOf("kg") }

        AlertDialog(
            onDismissRequest = { showAddPrDialog = false },
            title = { Text("Add Personal Record", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = exerciseNameInput,
                        onValueChange = { exerciseNameInput = it },
                        label = { Text("Exercise Name") },
                        placeholder = { Text("e.g. Incline Bench Press") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = recordValueInput,
                        onValueChange = { recordValueInput = it },
                        label = { Text("Record Value") },
                        placeholder = { Text("e.g. 95") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = unitInput,
                        onValueChange = { unitInput = it },
                        label = { Text("Unit (kg, reps, sec)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (exerciseNameInput.isNotBlank() && recordValueInput.toDoubleOrNull() != null) {
                            val pr = PersonalRecord(
                                exerciseName = exerciseNameInput.trim(),
                                recordValue = recordValueInput.toDoubleOrNull() ?: 0.0,
                                unit = unitInput.trim().ifBlank { "kg" },
                                dateMillis = System.currentTimeMillis()
                            )
                            onAddPersonalRecord(pr)
                            showAddPrDialog = false
                        }
                    }
                ) {
                    Text("Add PR", color = GymGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPrDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}
