package com.dk.gymapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.dk.gymapp.data.database.GymDatabaseHelper
import com.dk.gymapp.data.model.BodyMeasurement
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.data.model.PersonalRecord
import com.dk.gymapp.data.model.UserAccount
import com.dk.gymapp.data.model.UserProfile
import com.dk.gymapp.data.model.UserRole
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.data.model.WorkoutPlan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GymRepository(private val context: Context) {

    private val dbHelper = GymDatabaseHelper(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("gymfit_prefs", Context.MODE_PRIVATE)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Current logged-in user
    private val _currentUser = MutableStateFlow(loadCurrentAccount())
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _workouts = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val workouts: StateFlow<List<WorkoutPlan>> = _workouts.asStateFlow()

    private val _history = MutableStateFlow<List<WorkoutHistory>>(emptyList())
    val history: StateFlow<List<WorkoutHistory>> = _history.asStateFlow()

    private val _measurements = MutableStateFlow<List<BodyMeasurement>>(emptyList())
    val measurements: StateFlow<List<BodyMeasurement>> = _measurements.asStateFlow()

    private val _personalRecords = MutableStateFlow<List<PersonalRecord>>(emptyList())
    val personalRecords: StateFlow<List<PersonalRecord>> = _personalRecords.asStateFlow()

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        coroutineScope.launch {
            _exercises.value = dbHelper.getAllExercises()
            _workouts.value = dbHelper.getAllWorkouts()
            _history.value = dbHelper.getAllHistory()
            _measurements.value = dbHelper.getAllMeasurements()
            _personalRecords.value = dbHelper.getAllPersonalRecords()
        }
    }

    // AUTHENTICATION
    fun login(username: String, pass: String): Boolean {
        val user = dbHelper.authenticate(username, pass)
        return if (user != null) {
            _currentUser.value = user
            saveCurrentAccount(user)
            if (user.role == UserRole.ATHLETE) {
                updateUserProfile(_userProfile.value.copy(name = user.displayName))
            }
            true
        } else {
            false
        }
    }

    fun register(username: String, pass: String, displayName: String, role: UserRole): Boolean {
        val newUser = UserAccount(
            id = "user_" + UUID.randomUUID().toString().take(8),
            username = username,
            password = pass,
            displayName = displayName,
            role = role
        )
        val success = dbHelper.createUser(newUser)
        if (success) {
            _currentUser.value = newUser
            saveCurrentAccount(newUser)
            updateUserProfile(_userProfile.value.copy(name = displayName))
        }
        return success
    }

    fun switchRoleQuick(role: UserRole) {
        if (role == UserRole.ADMIN) {
            val adminUser = UserAccount("admin_1", "admin", "", "System Administrator", UserRole.ADMIN)
            _currentUser.value = adminUser
            saveCurrentAccount(adminUser)
        } else {
            val athleteUser = UserAccount("user_1", "athlete", "", "Alex Morgan", UserRole.ATHLETE)
            _currentUser.value = athleteUser
            saveCurrentAccount(athleteUser)
            updateUserProfile(_userProfile.value.copy(name = "Alex Morgan"))
        }
    }

    fun logout() {
        val defaultAthlete = UserAccount("user_1", "athlete", "", "Alex Morgan", UserRole.ATHLETE)
        _currentUser.value = defaultAthlete
        saveCurrentAccount(defaultAthlete)
    }

    private fun saveCurrentAccount(user: UserAccount) {
        prefs.edit().apply {
            putString("auth_id", user.id)
            putString("auth_username", user.username)
            putString("auth_display_name", user.displayName)
            putString("auth_role", user.role.name)
            apply()
        }
    }

    private fun loadCurrentAccount(): UserAccount {
        val id = prefs.getString("auth_id", "user_1") ?: "user_1"
        val username = prefs.getString("auth_username", "athlete") ?: "athlete"
        val name = prefs.getString("auth_display_name", "Alex Morgan") ?: "Alex Morgan"
        val roleStr = prefs.getString("auth_role", UserRole.ATHLETE.name) ?: UserRole.ATHLETE.name
        val role = if (roleStr == UserRole.ADMIN.name) UserRole.ADMIN else UserRole.ATHLETE
        return UserAccount(id, username, "", name, role)
    }

    // ADMIN EXERCISE MANAGEMENT
    fun addExercise(exercise: Exercise) {
        coroutineScope.launch {
            dbHelper.insertExercise(exercise)
            _exercises.value = dbHelper.getAllExercises()
        }
    }

    fun updateExercise(exercise: Exercise) {
        coroutineScope.launch {
            dbHelper.updateExercise(exercise)
            _exercises.value = dbHelper.getAllExercises()
        }
    }

    fun deleteExercise(id: String) {
        coroutineScope.launch {
            dbHelper.deleteExercise(id)
            _exercises.value = dbHelper.getAllExercises()
        }
    }

    fun toggleExerciseFavorite(id: String) {
        coroutineScope.launch {
            val current = _exercises.value.find { it.id == id } ?: return@launch
            val updated = !current.isFavorite
            dbHelper.setExerciseFavorite(id, updated)
            _exercises.value = dbHelper.getAllExercises()
        }
    }

    fun toggleWorkoutFavorite(id: String) {
        coroutineScope.launch {
            val current = _workouts.value.find { it.id == id } ?: return@launch
            val updated = !current.isFavorite
            dbHelper.setWorkoutFavorite(id, updated)
            _workouts.value = dbHelper.getAllWorkouts()
        }
    }

    fun saveWorkout(workout: WorkoutPlan) {
        coroutineScope.launch {
            dbHelper.saveWorkout(workout)
            _workouts.value = dbHelper.getAllWorkouts()
        }
    }

    fun deleteWorkout(workoutId: String) {
        coroutineScope.launch {
            dbHelper.deleteWorkout(workoutId)
            _workouts.value = dbHelper.getAllWorkouts()
        }
    }

    fun recordCompletedWorkout(hist: WorkoutHistory) {
        coroutineScope.launch {
            dbHelper.insertHistory(hist)
            _history.value = dbHelper.getAllHistory()
        }
    }

    fun logMeasurement(measurement: BodyMeasurement) {
        coroutineScope.launch {
            dbHelper.insertMeasurement(measurement)
            _measurements.value = dbHelper.getAllMeasurements()
            val currentProfile = _userProfile.value
            updateUserProfile(currentProfile.copy(weightKg = measurement.weightKg))
        }
    }

    fun savePersonalRecord(pr: PersonalRecord) {
        coroutineScope.launch {
            dbHelper.insertOrUpdatePersonalRecord(pr)
            _personalRecords.value = dbHelper.getAllPersonalRecords()
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs.edit().apply {
            putString("name", profile.name)
            putString("tagline", profile.tagline)
            putInt("age", profile.age)
            putFloat("heightCm", profile.heightCm.toFloat())
            putFloat("weightKg", profile.weightKg.toFloat())
            putString("fitnessGoal", profile.fitnessGoal)
            putString("experienceLevel", profile.experienceLevel)
            putInt("preferredWorkoutDays", profile.preferredWorkoutDays)
            putBoolean("useKg", profile.useKg)
            putBoolean("soundEnabled", profile.soundEnabled)
            putBoolean("vibrationEnabled", profile.vibrationEnabled)
            putBoolean("darkTheme", profile.darkTheme)
            putInt("defaultRestSeconds", profile.defaultRestSeconds)
            apply()
        }
    }

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("name", "Alex Morgan") ?: "Alex Morgan",
            tagline = prefs.getString("tagline", "Train Hard. Stay Strong.") ?: "Train Hard. Stay Strong.",
            age = prefs.getInt("age", 26),
            heightCm = prefs.getFloat("heightCm", 180.0f).toDouble(),
            weightKg = prefs.getFloat("weightKg", 78.0f).toDouble(),
            fitnessGoal = prefs.getString("fitnessGoal", "Build Muscle") ?: "Build Muscle",
            experienceLevel = prefs.getString("experienceLevel", "Intermediate") ?: "Intermediate",
            preferredWorkoutDays = prefs.getInt("preferredWorkoutDays", 6),
            useKg = prefs.getBoolean("useKg", true),
            soundEnabled = prefs.getBoolean("soundEnabled", true),
            vibrationEnabled = prefs.getBoolean("vibrationEnabled", true),
            darkTheme = prefs.getBoolean("darkTheme", true),
            defaultRestSeconds = prefs.getInt("defaultRestSeconds", 60)
        )
    }

    fun resetAllData() {
        coroutineScope.launch {
            dbHelper.resetToDefaults()
            prefs.edit().clear().apply()
            _userProfile.value = UserProfile()
            _currentUser.value = loadCurrentAccount()
            refreshAll()
        }
    }
}
