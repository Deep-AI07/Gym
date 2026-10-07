package com.dk.gymapp.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dk.gymapp.ui.theme.GymGreen

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Workouts : Screen("workouts", "Workouts")
    object Exercises : Screen("exercises", "Exercises")
    object Progress : Screen("progress", "Progress")
    object Profile : Screen("profile", "Profile")

    object WorkoutDetail : Screen("workout_detail/{workoutId}", "Workout Detail") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }

    object ActiveWorkout : Screen("active_workout/{workoutId}", "Active Workout") {
        fun createRoute(workoutId: String) = "active_workout/$workoutId"
    }

    object ExerciseDetail : Screen("exercise_detail/{exerciseId}", "Exercise Detail") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }

    object CustomWorkout : Screen("custom_workout", "Custom Workout")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("workouts", "Workouts", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    BottomNavItem("exercises", "Library", Icons.AutoMirrored.Filled.FormatListBulleted, Icons.AutoMirrored.Outlined.FormatListBulleted),
    BottomNavItem("progress", "Progress", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    BottomNavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun GymBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        // Premium transparent frosted-glass surface
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                val tint by animateColorAsState(
                    targetValue = if (selected) GymGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "tint"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNavigate(item.route)
                        }
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .then(
                                if (selected) {
                                    Modifier
                                        .background(GymGreen.copy(alpha = 0.18f))
                                        .border(1.dp, GymGreen.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                                } else {
                                    Modifier.background(Color.Transparent)
                                }
                            )
                            .padding(horizontal = if (selected) 10.dp else 6.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = tint,
                                modifier = Modifier.size(22.dp)
                            )
                            if (selected) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = item.title,
                                    color = GymGreen,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
