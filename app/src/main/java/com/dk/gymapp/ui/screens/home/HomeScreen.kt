package com.dk.gymapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.data.model.UserAccount
import com.dk.gymapp.data.model.UserProfile
import com.dk.gymapp.data.model.UserRole
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.data.model.WorkoutPlan
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    currentUser: UserAccount,
    userProfile: UserProfile,
    workouts: List<WorkoutPlan>,
    exercises: List<Exercise>,
    history: List<WorkoutHistory>,
    onStartWorkout: (String) -> Unit,
    onViewWorkoutDetail: (String) -> Unit,
    onViewExerciseDetail: (String) -> Unit,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ... 7 = Saturday

    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val todayDate = dateFormat.format(Date())

    // Weekly day mapping for Mon-Sat split + Sun rest
    // Index: 0 = Mon, 1 = Tue, 2 = Wed, 3 = Thu, 4 = Fri, 5 = Sat, 6 = Sun
    val todaySplitIndex = when (dayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        else -> 6 // Sunday
    }

    var selectedSplitDayIndex by remember { mutableIntStateOf(todaySplitIndex) }

    val splitWorkoutIds = listOf(
        "split_mon",
        "split_tue",
        "split_wed",
        "split_thu",
        "split_fri",
        "split_sat",
        "split_sun"
    )

    val currentSplitPlan = workouts.find { it.id == splitWorkoutIds[selectedSplitDayIndex] }
        ?: workouts.firstOrNull()

    // Weekly calculations
    val workoutsThisWeek = history.take(7).size.coerceAtMost(userProfile.preferredWorkoutDays)
    val totalCalories = history.take(7).sumOf { it.caloriesBurned }
    val totalMinutes = history.take(7).sumOf { it.durationSeconds } / 60

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
        // TOP HEADER: Role badge, Greeting & Profile Avatar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = todayDate.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        if (currentUser.role == UserRole.ADMIN) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentOrange,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$greeting,\n${currentUser.displayName.split(" ").firstOrNull() ?: "Athlete"}!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (currentUser.role == UserRole.ADMIN) listOf(AccentOrange, GymGreen)
                                else listOf(GymGreen, AccentCyan)
                            )
                        )
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.displayName.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // MOTIVATION QUOTE BANNER
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = GymGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "\"${userProfile.tagline}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // WEEKLY SPLIT SCHEDULE (MON-SAT + SUN REST)
        item {
            Column {
                SectionHeader(
                    title = "Weekly Workout Split",
                    subtitle = "Mon–Sat split with Sunday recovery",
                    actionText = "All Plans",
                    onActionClick = onNavigateToWorkouts
                )

                // 7-Day Day Selector Bar (Mon to Sun)
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val dayMuscleTitles = listOf("Chest & Bi", "Back & Tri", "Legs & Sh", "Chest & Bi", "Back & Tri", "Legs & Sh", "Rest Day")

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dayNames.size) { index ->
                        val isSelected = selectedSplitDayIndex == index
                        val isToday = todaySplitIndex == index

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSplitDayIndex = index },
                            color = if (isSelected) GymGreen else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isToday) GymGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dayNames[index],
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground
                                    )
                                    if (isToday) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.Black else GymGreen)
                                        )
                                    }
                                }
                                Text(
                                    text = dayMuscleTitles[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FEATURED SPLIT CARD FOR SELECTED DAY
                if (currentSplitPlan != null) {
                    GymCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = GymGreen.copy(alpha = 0.5f),
                        backgroundColor = DarkSurfaceCard
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DifficultyBadge(difficulty = currentSplitPlan.difficulty)
                                if (selectedSplitDayIndex == todaySplitIndex) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = GymGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "TODAY'S TARGET",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GymGreen,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${currentSplitPlan.durationMinutes} MINS",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentSplitPlan.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentSplitPlan.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Target muscles row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentSplitPlan.muscleGroups.take(4).forEach { muscle ->
                                MuscleBadge(muscle = muscle, isPrimary = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onStartWorkout(currentSplitPlan.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GymGreen)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedSplitDayIndex == 6) "Start Recovery" else "Start Workout",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onViewWorkoutDetail(currentSplitPlan.id) },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = "Details",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // QUICK STATS 2x2 GRID
        item {
            Column {
                SectionHeader(
                    title = "Weekly Performance",
                    subtitle = "Consistency and calorie output",
                    actionText = "Details",
                    onActionClick = onNavigateToProgress
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Streak",
                        value = "7 Days",
                        subtitle = "Personal Best: 14",
                        icon = Icons.Default.Whatshot,
                        accentColor = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Weekly Goal",
                        value = "$workoutsThisWeek/${userProfile.preferredWorkoutDays}",
                        subtitle = "Workouts Done",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = GymGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Calories",
                        value = "$totalCalories",
                        subtitle = "Burned this week",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = AccentPurple,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Active Time",
                        value = "${totalMinutes}m",
                        subtitle = "Total logged",
                        icon = Icons.Default.Timer,
                        accentColor = AccentCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // RECOMMENDED EXERCISES
        item {
            Column {
                SectionHeader(
                    title = "Exercise Library",
                    subtitle = "Technique, video guides & level tiers",
                    actionText = "Explore All",
                    onActionClick = onNavigateToExercises
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(exercises.take(6)) { ex ->
                        GymCard(
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { onViewExerciseDetail(ex.id) },
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MuscleBadge(muscle = ex.muscleGroup, isPrimary = false)
                                if (ex.videoFileName.isNotBlank() || ex.videoUrl.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentCyan.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "VIDEO",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AccentCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = ex.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${ex.setsRecommendation} sets × ${ex.repsRecommendation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            DifficultyBadge(difficulty = ex.difficulty)
                        }
                    }
                }
            }
        }

        // RECENT WORKOUT HISTORY
        item {
            Column {
                SectionHeader(
                    title = "Recent Workouts",
                    subtitle = "Saved user history",
                    actionText = "Full History",
                    onActionClick = onNavigateToProgress
                )

                if (history.isEmpty()) {
                    GymCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No workouts recorded yet. Start your first workout today!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        history.take(3).forEach { item ->
                            val itemDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(item.dateMillis))
                            GymCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.workoutTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$itemDate • ${item.durationSeconds / 60} mins • ${item.caloriesBurned} kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (item.notes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "\"${item.notes}\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = GymGreenLight,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        repeat(item.rating) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = AccentOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
