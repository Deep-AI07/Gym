package com.dk.gymapp

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dk.gymapp.data.model.UserRole
import com.dk.gymapp.data.repository.GymRepository
import com.dk.gymapp.ui.navigation.GymBottomNavBar
import com.dk.gymapp.ui.navigation.Screen
import com.dk.gymapp.ui.navigation.bottomNavItems
import com.dk.gymapp.ui.screens.admin.AdminExerciseDialog
import com.dk.gymapp.ui.screens.auth.LoginDialog
import com.dk.gymapp.ui.screens.home.HomeScreen
import com.dk.gymapp.ui.screens.profile.ProfileScreen
import com.dk.gymapp.ui.screens.progress.ProgressScreen
import com.dk.gymapp.ui.screens.workouts.ActiveWorkoutScreen
import com.dk.gymapp.ui.screens.workouts.CustomWorkoutScreen
import com.dk.gymapp.ui.screens.workouts.WorkoutDetailScreen
import com.dk.gymapp.ui.screens.workouts.WorkoutsScreen
import com.dk.gymapp.ui.theme.GymTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Allow window to seamlessly draw behind cutout / camera punch hole on all modern Android devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
        }

        // Disable contrast enforcement so Android doesn't paint a dark/gray scrim over transparent bars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        val repository = GymRepository(applicationContext)

        setContent {
            val currentUser by repository.currentUser.collectAsState()
            val userProfile by repository.userProfile.collectAsState()
            val workouts by repository.workouts.collectAsState()
            val exercises by repository.exercises.collectAsState()
            val history by repository.history.collectAsState()
            val measurements by repository.measurements.collectAsState()
            val personalRecords by repository.personalRecords.collectAsState()

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Dialog states
            var showLoginDialog by remember { mutableStateOf(false) }
            var showAdminAddExerciseDialog by remember { mutableStateOf(false) }

            val isAdmin = currentUser.role == UserRole.ADMIN

            // Show bottom navigation bar only on main top-level tabs
            val showBottomBar = currentRoute in bottomNavItems.map { it.route }

            GymTheme(darkTheme = userProfile.darkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                            // TAB 1: HOME
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    currentUser = currentUser,
                                    userProfile = userProfile,
                                    workouts = workouts,
                                    exercises = exercises,
                                    history = history,
                                    onStartWorkout = { workoutId ->
                                        navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                                    },
                                    onViewWorkoutDetail = { workoutId ->
                                        navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                                    },
                                    onViewExerciseDetail = { exerciseId ->
                                        navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                                    },
                                    onNavigateToWorkouts = {
                                        navController.navigate(Screen.Workouts.route)
                                    },
                                    onNavigateToExercises = {
                                        navController.navigate(Screen.Exercises.route)
                                    },
                                    onNavigateToProgress = {
                                        navController.navigate(Screen.Progress.route)
                                    },
                                    onNavigateToProfile = {
                                        navController.navigate(Screen.Profile.route)
                                    }
                                )
                            }

                            // TAB 2: WORKOUTS
                            composable(Screen.Workouts.route) {
                                WorkoutsScreen(
                                    workouts = workouts,
                                    onSelectWorkout = { workoutId ->
                                        navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                                    },
                                    onStartWorkout = { workoutId ->
                                        navController.navigate(Screen.ActiveWorkout.createRoute(workoutId))
                                    },
                                    onToggleFavorite = { workoutId ->
                                        repository.toggleWorkoutFavorite(workoutId)
                                    },
                                    onCreateCustomWorkout = {
                                        navController.navigate(Screen.CustomWorkout.route)
                                    },
                                    onDeleteWorkout = { workoutId ->
                                        repository.deleteWorkout(workoutId)
                                    }
                                )
                            }

                            // TAB 3: EXERCISES LIBRARY
                            composable(Screen.Exercises.route) {
                                com.dk.gymapp.ui.screens.exercises.ExerciseListScreen(
                                    exercises = exercises,
                                    onExerciseClick = { exerciseId ->
                                        navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                                    },
                                    onToggleFavorite = { exerciseId ->
                                        repository.toggleExerciseFavorite(exerciseId)
                                    },
                                    isAdmin = isAdmin,
                                    onAddExerciseClick = {
                                        showAdminAddExerciseDialog = true
                                    }
                                )
                            }

                            // TAB 4: PROGRESS
                            composable(Screen.Progress.route) {
                                ProgressScreen(
                                    history = history,
                                    measurements = measurements,
                                    personalRecords = personalRecords,
                                    onLogMeasurement = { measurement ->
                                        repository.logMeasurement(measurement)
                                    },
                                    onAddPersonalRecord = { pr ->
                                        repository.savePersonalRecord(pr)
                                    }
                                )
                            }

                            // TAB 5: PROFILE
                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    currentUser = currentUser,
                                    userProfile = userProfile,
                                    onUpdateProfile = { updated ->
                                        repository.updateUserProfile(updated)
                                    },
                                    onResetData = {
                                        repository.resetAllData()
                                    },
                                    onOpenLoginDialog = {
                                        showLoginDialog = true
                                    },
                                    onOpenAdminAddExercise = {
                                        showAdminAddExerciseDialog = true
                                    }
                                )
                            }

                            // SUB-SCREEN: WORKOUT DETAIL
                            composable(Screen.WorkoutDetail.route) { backStackEntry ->
                                val workoutId = backStackEntry.arguments?.getString("workoutId")
                                val workout = workouts.find { it.id == workoutId }
                                if (workout != null) {
                                    WorkoutDetailScreen(
                                        workout = workout,
                                        onBack = { navController.popBackStack() },
                                        onStartWorkout = { id ->
                                            navController.navigate(Screen.ActiveWorkout.createRoute(id))
                                        },
                                        onViewExerciseDetail = { exId ->
                                            navController.navigate(Screen.ExerciseDetail.createRoute(exId))
                                        },
                                        onToggleFavorite = { id ->
                                            repository.toggleWorkoutFavorite(id)
                                        }
                                    )
                                }
                            }

                            // SUB-SCREEN: ACTIVE WORKOUT (PLAYER)
                            composable(Screen.ActiveWorkout.route) { backStackEntry ->
                                val workoutId = backStackEntry.arguments?.getString("workoutId")
                                val workout = workouts.find { it.id == workoutId }
                                if (workout != null) {
                                    ActiveWorkoutScreen(
                                        workout = workout,
                                        allExercises = exercises,
                                        onFinishWorkout = { completedHistory ->
                                            repository.recordCompletedWorkout(completedHistory)
                                            navController.popBackStack()
                                        },
                                        onCancelWorkout = {
                                            navController.popBackStack()
                                        }
                                    )
                                }
                            }

                            // SUB-SCREEN: EXERCISE DETAIL
                            composable(Screen.ExerciseDetail.route) { backStackEntry ->
                                val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                                val exercise = exercises.find { it.id == exerciseId } ?: exercises.find { it.name == exerciseId }
                                if (exercise != null) {
                                    com.dk.gymapp.ui.screens.exercises.ExerciseDetailScreen(
                                        exercise = exercise,
                                        onBack = { navController.popBackStack() },
                                        onToggleFavorite = { id ->
                                            repository.toggleExerciseFavorite(id)
                                        },
                                        isAdmin = isAdmin,
                                        onUpdateExercise = { updated ->
                                            repository.updateExercise(updated)
                                        },
                                        onDeleteExercise = { id ->
                                            repository.deleteExercise(id)
                                        }
                                    )
                                }
                            }

                            // SUB-SCREEN: CUSTOM WORKOUT BUILDER
                            composable(Screen.CustomWorkout.route) {
                                CustomWorkoutScreen(
                                    availableExercises = exercises,
                                    onBack = { navController.popBackStack() },
                                    onSaveWorkout = { customPlan ->
                                        repository.saveWorkout(customPlan)
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                    // TOP PROTECTIVE STATUS BAR / CUTOUT GRADIENT OVERLAY
                    // Fades seamlessly to transparent below the camera cutout/status bar
                    // Ensures that time, battery, and status icons are ALWAYS crisp and clearly visible
                    // on any phone regardless of front camera size, notch shape, or background content
                    val safeTopCutoutPadding = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(safeTopCutoutPadding + 6.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.88f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .align(Alignment.TopCenter)
                    )

                    // FLOATING TRANSPARENT / FROSTED-GLASS BOTTOM NAVIGATION BAR
                    // Displayed on top-level tabs, content flows seamlessly behind it!
                    if (showBottomBar) {
                        GymBottomNavBar(
                            currentRoute = currentRoute,
                            onNavigate = { targetRoute ->
                                if (targetRoute == Screen.Home.route) {
                                    navController.popBackStack(Screen.Home.route, inclusive = false)
                                    if (currentRoute != Screen.Home.route) {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = false
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                } else {
                                    navController.navigate(targetRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    // MODAL: LOGIN / SWITCH ACCOUNT DIALOG
                    if (showLoginDialog) {
                        LoginDialog(
                            onDismiss = { showLoginDialog = false },
                            onLogin = { u, p -> repository.login(u, p) },
                            onRegister = { u, p, name, role -> repository.register(u, p, name, role) },
                            onQuickSwitch = { role -> repository.switchRoleQuick(role) }
                        )
                    }

                    // MODAL: ADMIN ADD EXERCISE DIALOG
                    if (showAdminAddExerciseDialog) {
                        AdminExerciseDialog(
                            exerciseToEdit = null,
                            onDismiss = { showAdminAddExerciseDialog = false },
                            onSave = { newExercise ->
                                repository.addExercise(newExercise)
                                showAdminAddExerciseDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}