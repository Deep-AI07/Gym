package com.dk.gymapp.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dk.gymapp.data.model.BodyMeasurement
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.data.model.PersonalRecord
import com.dk.gymapp.data.model.UserAccount
import com.dk.gymapp.data.model.UserRole
import com.dk.gymapp.data.model.WorkoutExercise
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.data.model.WorkoutPlan
import org.json.JSONArray
import org.json.JSONObject

class GymDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "gymfit.db"
        const val DATABASE_VERSION = 3

        private const val TABLE_USERS = "users"
        private const val TABLE_EXERCISES = "exercises"
        private const val TABLE_WORKOUTS = "workouts"
        private const val TABLE_HISTORY = "workout_history"
        private const val TABLE_MEASUREMENTS = "body_measurements"
        private const val TABLE_PRS = "personal_records"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                id TEXT PRIMARY KEY,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                displayName TEXT NOT NULL,
                role TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_EXERCISES (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                muscleGroup TEXT NOT NULL,
                secondaryMuscles TEXT,
                equipment TEXT,
                difficulty TEXT,
                instructions TEXT,
                setsRecommendation INTEGER,
                repsRecommendation TEXT,
                restTimeSeconds INTEGER,
                safetyTips TEXT,
                commonMistakes TEXT,
                videoFileName TEXT,
                isFavorite INTEGER DEFAULT 0,
                photoUrl TEXT,
                videoUrl TEXT,
                beginnerGuide TEXT,
                intermediateGuide TEXT,
                advancedGuide TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUTS (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                subtitle TEXT,
                category TEXT NOT NULL,
                difficulty TEXT,
                durationMinutes INTEGER,
                muscleGroups TEXT,
                equipment TEXT,
                description TEXT,
                exercisesJson TEXT,
                isFavorite INTEGER DEFAULT 0,
                isCustom INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_HISTORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                workoutId TEXT NOT NULL,
                workoutTitle TEXT NOT NULL,
                dateMillis INTEGER NOT NULL,
                durationSeconds INTEGER NOT NULL,
                caloriesBurned INTEGER NOT NULL,
                exercisesCompleted INTEGER NOT NULL,
                totalSets INTEGER NOT NULL,
                notes TEXT,
                rating INTEGER DEFAULT 5
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MEASUREMENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dateMillis INTEGER NOT NULL,
                weightKg REAL NOT NULL,
                chestCm REAL,
                waistCm REAL,
                armsCm REAL,
                legsCm REAL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PRS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                exerciseName TEXT NOT NULL,
                recordValue REAL NOT NULL,
                unit TEXT NOT NULL,
                dateMillis INTEGER NOT NULL
            )
            """.trimIndent()
        )

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            // Update default exercises with corrected working video URLs without wiping data
            try {
                SampleData.defaultExercises.forEach { ex ->
                    val cv = ContentValues().apply {
                        put("videoUrl", ex.videoUrl)
                    }
                    db.update(TABLE_EXERCISES, cv, "id = ?", arrayOf(ex.id))
                }
            } catch (_: Exception) {}
        } else {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MEASUREMENTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRS")
            onCreate(db)
        }
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Seed default accounts
        val admin = ContentValues().apply {
            put("id", "admin_1")
            put("username", "admin")
            put("password", "admin123")
            put("displayName", "System Administrator")
            put("role", UserRole.ADMIN.name)
        }
        db.insert(TABLE_USERS, null, admin)

        val athlete = ContentValues().apply {
            put("id", "user_1")
            put("username", "athlete")
            put("password", "user123")
            put("displayName", "Alex Morgan")
            put("role", UserRole.ATHLETE.name)
        }
        db.insert(TABLE_USERS, null, athlete)

        SampleData.defaultExercises.forEach { ex ->
            val cv = ContentValues().apply {
                put("id", ex.id)
                put("name", ex.name)
                put("muscleGroup", ex.muscleGroup)
                put("secondaryMuscles", ex.secondaryMuscles.joinToString(";"))
                put("equipment", ex.equipment)
                put("difficulty", ex.difficulty)
                put("instructions", ex.instructions.joinToString(";;;"))
                put("setsRecommendation", ex.setsRecommendation)
                put("repsRecommendation", ex.repsRecommendation)
                put("restTimeSeconds", ex.restTimeSeconds)
                put("safetyTips", ex.safetyTips.joinToString(";;;"))
                put("commonMistakes", ex.commonMistakes.joinToString(";;;"))
                put("videoFileName", ex.videoFileName)
                put("isFavorite", if (ex.isFavorite) 1 else 0)
                put("photoUrl", ex.photoUrl)
                put("videoUrl", ex.videoUrl)
                put("beginnerGuide", ex.beginnerGuide)
                put("intermediateGuide", ex.intermediateGuide)
                put("advancedGuide", ex.advancedGuide)
            }
            db.insert(TABLE_EXERCISES, null, cv)
        }

        SampleData.defaultWorkouts.forEach { plan ->
            val cv = ContentValues().apply {
                put("id", plan.id)
                put("title", plan.title)
                put("subtitle", plan.subtitle)
                put("category", plan.category)
                put("difficulty", plan.difficulty)
                put("durationMinutes", plan.durationMinutes)
                put("muscleGroups", plan.muscleGroups.joinToString(","))
                put("equipment", plan.equipment)
                put("description", plan.description)
                put("exercisesJson", serializeExercises(plan.exercises))
                put("isFavorite", if (plan.isFavorite) 1 else 0)
                put("isCustom", if (plan.isCustom) 1 else 0)
            }
            db.insert(TABLE_WORKOUTS, null, cv)
        }

        SampleData.getSampleWorkoutHistory().forEach { hist ->
            val cv = ContentValues().apply {
                put("workoutId", hist.workoutId)
                put("workoutTitle", hist.workoutTitle)
                put("dateMillis", hist.dateMillis)
                put("durationSeconds", hist.durationSeconds)
                put("caloriesBurned", hist.caloriesBurned)
                put("exercisesCompleted", hist.exercisesCompleted)
                put("totalSets", hist.totalSets)
                put("notes", hist.notes)
                put("rating", hist.rating)
            }
            db.insert(TABLE_HISTORY, null, cv)
        }

        SampleData.getSampleMeasurements().forEach { m ->
            val cv = ContentValues().apply {
                put("dateMillis", m.dateMillis)
                put("weightKg", m.weightKg)
                put("chestCm", m.chestCm)
                put("waistCm", m.waistCm)
                put("armsCm", m.armsCm)
                put("legsCm", m.legsCm)
            }
            db.insert(TABLE_MEASUREMENTS, null, cv)
        }

        SampleData.defaultPersonalRecords.forEach { pr ->
            val cv = ContentValues().apply {
                put("exerciseName", pr.exerciseName)
                put("recordValue", pr.recordValue)
                put("unit", pr.unit)
                put("dateMillis", pr.dateMillis)
            }
            db.insert(TABLE_PRS, null, cv)
        }
    }

    // USER AUTHENTICATION
    fun authenticate(username: String, pass: String): UserAccount? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE LOWER(username) = LOWER(?) AND password = ?", arrayOf(username.trim(), pass.trim()))
        cursor.use { c ->
            if (c.moveToFirst()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val uName = c.getString(c.getColumnIndexOrThrow("username"))
                val dName = c.getString(c.getColumnIndexOrThrow("displayName"))
                val rStr = c.getString(c.getColumnIndexOrThrow("role"))
                val role = if (rStr == UserRole.ADMIN.name) UserRole.ADMIN else UserRole.ATHLETE
                return UserAccount(id, uName, "", dName, role)
            }
        }
        return null
    }

    fun createUser(user: UserAccount): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", user.id)
            put("username", user.username.trim().lowercase())
            put("password", user.password)
            put("displayName", user.displayName)
            put("role", user.role.name)
        }
        val result = db.insert(TABLE_USERS, null, cv)
        return result != -1L
    }

    // EXERCISES CRUD
    fun getAllExercises(): List<Exercise> {
        val list = mutableListOf<Exercise>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EXERCISES ORDER BY name ASC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val muscleGroup = c.getString(c.getColumnIndexOrThrow("muscleGroup"))
                val secMusclesRaw = c.getString(c.getColumnIndexOrThrow("secondaryMuscles")) ?: ""
                val equipment = c.getString(c.getColumnIndexOrThrow("equipment")) ?: ""
                val difficulty = c.getString(c.getColumnIndexOrThrow("difficulty")) ?: "All Levels"
                val instructionsRaw = c.getString(c.getColumnIndexOrThrow("instructions")) ?: ""
                val sets = c.getInt(c.getColumnIndexOrThrow("setsRecommendation"))
                val reps = c.getString(c.getColumnIndexOrThrow("repsRecommendation")) ?: "8-12"
                val rest = c.getInt(c.getColumnIndexOrThrow("restTimeSeconds"))
                val safetyRaw = c.getString(c.getColumnIndexOrThrow("safetyTips")) ?: ""
                val mistakesRaw = c.getString(c.getColumnIndexOrThrow("commonMistakes")) ?: ""
                val video = c.getString(c.getColumnIndexOrThrow("videoFileName")) ?: ""
                val isFav = c.getInt(c.getColumnIndexOrThrow("isFavorite")) == 1
                val photoUrl = c.getString(c.getColumnIndexOrThrow("photoUrl")) ?: ""
                val videoUrl = c.getString(c.getColumnIndexOrThrow("videoUrl")) ?: ""
                val beginnerGuide = c.getString(c.getColumnIndexOrThrow("beginnerGuide")) ?: ""
                val intermediateGuide = c.getString(c.getColumnIndexOrThrow("intermediateGuide")) ?: ""
                val advancedGuide = c.getString(c.getColumnIndexOrThrow("advancedGuide")) ?: ""

                list.add(
                    Exercise(
                        id = id,
                        name = name,
                        muscleGroup = muscleGroup,
                        secondaryMuscles = if (secMusclesRaw.isBlank()) emptyList() else secMusclesRaw.split(";"),
                        equipment = equipment,
                        difficulty = difficulty,
                        instructions = if (instructionsRaw.isBlank()) emptyList() else instructionsRaw.split(";;;"),
                        setsRecommendation = sets,
                        repsRecommendation = reps,
                        restTimeSeconds = rest,
                        safetyTips = if (safetyRaw.isBlank()) emptyList() else safetyRaw.split(";;;"),
                        commonMistakes = if (mistakesRaw.isBlank()) emptyList() else mistakesRaw.split(";;;"),
                        videoFileName = video,
                        isFavorite = isFav,
                        photoUrl = photoUrl,
                        videoUrl = videoUrl,
                        beginnerGuide = beginnerGuide,
                        intermediateGuide = intermediateGuide,
                        advancedGuide = advancedGuide
                    )
                )
            }
        }
        return list
    }

    fun insertExercise(ex: Exercise) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", ex.id)
            put("name", ex.name)
            put("muscleGroup", ex.muscleGroup)
            put("secondaryMuscles", ex.secondaryMuscles.joinToString(";"))
            put("equipment", ex.equipment)
            put("difficulty", ex.difficulty)
            put("instructions", ex.instructions.joinToString(";;;"))
            put("setsRecommendation", ex.setsRecommendation)
            put("repsRecommendation", ex.repsRecommendation)
            put("restTimeSeconds", ex.restTimeSeconds)
            put("safetyTips", ex.safetyTips.joinToString(";;;"))
            put("commonMistakes", ex.commonMistakes.joinToString(";;;"))
            put("videoFileName", ex.videoFileName)
            put("isFavorite", if (ex.isFavorite) 1 else 0)
            put("photoUrl", ex.photoUrl)
            put("videoUrl", ex.videoUrl)
            put("beginnerGuide", ex.beginnerGuide)
            put("intermediateGuide", ex.intermediateGuide)
            put("advancedGuide", ex.advancedGuide)
        }
        db.insertWithOnConflict(TABLE_EXERCISES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateExercise(ex: Exercise) {
        insertExercise(ex)
    }

    fun deleteExercise(id: String) {
        val db = writableDatabase
        db.delete(TABLE_EXERCISES, "id = ?", arrayOf(id))
    }

    fun setExerciseFavorite(id: String, isFavorite: Boolean) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("isFavorite", if (isFavorite) 1 else 0)
        }
        db.update(TABLE_EXERCISES, cv, "id = ?", arrayOf(id))
    }

    // WORKOUTS CRUD
    fun getAllWorkouts(): List<WorkoutPlan> {
        val list = mutableListOf<WorkoutPlan>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WORKOUTS ORDER BY isCustom ASC, title ASC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val title = c.getString(c.getColumnIndexOrThrow("title"))
                val subtitle = c.getString(c.getColumnIndexOrThrow("subtitle")) ?: ""
                val category = c.getString(c.getColumnIndexOrThrow("category"))
                val difficulty = c.getString(c.getColumnIndexOrThrow("difficulty")) ?: "Intermediate"
                val duration = c.getInt(c.getColumnIndexOrThrow("durationMinutes"))
                val musclesRaw = c.getString(c.getColumnIndexOrThrow("muscleGroups")) ?: ""
                val equipment = c.getString(c.getColumnIndexOrThrow("equipment")) ?: ""
                val description = c.getString(c.getColumnIndexOrThrow("description")) ?: ""
                val exercisesJson = c.getString(c.getColumnIndexOrThrow("exercisesJson")) ?: "[]"
                val isFav = c.getInt(c.getColumnIndexOrThrow("isFavorite")) == 1
                val isCustom = c.getInt(c.getColumnIndexOrThrow("isCustom")) == 1

                list.add(
                    WorkoutPlan(
                        id = id,
                        title = title,
                        subtitle = subtitle,
                        category = category,
                        difficulty = difficulty,
                        durationMinutes = duration,
                        muscleGroups = if (musclesRaw.isBlank()) emptyList() else musclesRaw.split(","),
                        equipment = equipment,
                        description = description,
                        exercises = deserializeExercises(exercisesJson),
                        isFavorite = isFav,
                        isCustom = isCustom
                    )
                )
            }
        }
        return list
    }

    fun saveWorkout(plan: WorkoutPlan) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("id", plan.id)
            put("title", plan.title)
            put("subtitle", plan.subtitle)
            put("category", plan.category)
            put("difficulty", plan.difficulty)
            put("durationMinutes", plan.durationMinutes)
            put("muscleGroups", plan.muscleGroups.joinToString(","))
            put("equipment", plan.equipment)
            put("description", plan.description)
            put("exercisesJson", serializeExercises(plan.exercises))
            put("isFavorite", if (plan.isFavorite) 1 else 0)
            put("isCustom", if (plan.isCustom) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_WORKOUTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteWorkout(id: String) {
        val db = writableDatabase
        db.delete(TABLE_WORKOUTS, "id = ?", arrayOf(id))
    }

    fun setWorkoutFavorite(id: String, isFavorite: Boolean) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("isFavorite", if (isFavorite) 1 else 0)
        }
        db.update(TABLE_WORKOUTS, cv, "id = ?", arrayOf(id))
    }

    // HISTORY CRUD
    fun getAllHistory(): List<WorkoutHistory> {
        val list = mutableListOf<WorkoutHistory>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_HISTORY ORDER BY dateMillis DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    WorkoutHistory(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        workoutId = c.getString(c.getColumnIndexOrThrow("workoutId")),
                        workoutTitle = c.getString(c.getColumnIndexOrThrow("workoutTitle")),
                        dateMillis = c.getLong(c.getColumnIndexOrThrow("dateMillis")),
                        durationSeconds = c.getInt(c.getColumnIndexOrThrow("durationSeconds")),
                        caloriesBurned = c.getInt(c.getColumnIndexOrThrow("caloriesBurned")),
                        exercisesCompleted = c.getInt(c.getColumnIndexOrThrow("exercisesCompleted")),
                        totalSets = c.getInt(c.getColumnIndexOrThrow("totalSets")),
                        notes = c.getString(c.getColumnIndexOrThrow("notes")) ?: "",
                        rating = c.getInt(c.getColumnIndexOrThrow("rating"))
                    )
                )
            }
        }
        return list
    }

    fun insertHistory(hist: WorkoutHistory): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("workoutId", hist.workoutId)
            put("workoutTitle", hist.workoutTitle)
            put("dateMillis", hist.dateMillis)
            put("durationSeconds", hist.durationSeconds)
            put("caloriesBurned", hist.caloriesBurned)
            put("exercisesCompleted", hist.exercisesCompleted)
            put("totalSets", hist.totalSets)
            put("notes", hist.notes)
            put("rating", hist.rating)
        }
        return db.insert(TABLE_HISTORY, null, cv)
    }

    // MEASUREMENTS CRUD
    fun getAllMeasurements(): List<BodyMeasurement> {
        val list = mutableListOf<BodyMeasurement>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MEASUREMENTS ORDER BY dateMillis DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    BodyMeasurement(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        dateMillis = c.getLong(c.getColumnIndexOrThrow("dateMillis")),
                        weightKg = c.getDouble(c.getColumnIndexOrThrow("weightKg")),
                        chestCm = c.getDouble(c.getColumnIndexOrThrow("chestCm")),
                        waistCm = c.getDouble(c.getColumnIndexOrThrow("waistCm")),
                        armsCm = c.getDouble(c.getColumnIndexOrThrow("armsCm")),
                        legsCm = c.getDouble(c.getColumnIndexOrThrow("legsCm"))
                    )
                )
            }
        }
        return list
    }

    fun insertMeasurement(m: BodyMeasurement): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("dateMillis", m.dateMillis)
            put("weightKg", m.weightKg)
            put("chestCm", m.chestCm)
            put("waistCm", m.waistCm)
            put("armsCm", m.armsCm)
            put("legsCm", m.legsCm)
        }
        return db.insert(TABLE_MEASUREMENTS, null, cv)
    }

    // PERSONAL RECORDS CRUD
    fun getAllPersonalRecords(): List<PersonalRecord> {
        val list = mutableListOf<PersonalRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRS ORDER BY recordValue DESC", null)
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    PersonalRecord(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        exerciseName = c.getString(c.getColumnIndexOrThrow("exerciseName")),
                        recordValue = c.getDouble(c.getColumnIndexOrThrow("recordValue")),
                        unit = c.getString(c.getColumnIndexOrThrow("unit")),
                        dateMillis = c.getLong(c.getColumnIndexOrThrow("dateMillis"))
                    )
                )
            }
        }
        return list
    }

    fun insertOrUpdatePersonalRecord(pr: PersonalRecord): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("exerciseName", pr.exerciseName)
            put("recordValue", pr.recordValue)
            put("unit", pr.unit)
            put("dateMillis", pr.dateMillis)
        }
        return db.insert(TABLE_PRS, null, cv)
    }

    fun resetToDefaults() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_EXERCISES")
        db.execSQL("DELETE FROM $TABLE_WORKOUTS")
        db.execSQL("DELETE FROM $TABLE_HISTORY")
        db.execSQL("DELETE FROM $TABLE_MEASUREMENTS")
        db.execSQL("DELETE FROM $TABLE_PRS")
        seedInitialData(db)
    }

    // JSON HELPERS FOR WORKOUT EXERCISES
    private fun serializeExercises(exercises: List<WorkoutExercise>): String {
        val arr = JSONArray()
        exercises.forEach { ex ->
            val obj = JSONObject().apply {
                put("exerciseId", ex.exerciseId)
                put("exerciseName", ex.exerciseName)
                put("targetSets", ex.targetSets)
                put("targetReps", ex.targetReps)
                put("targetWeightKg", ex.targetWeightKg)
                put("restSeconds", ex.restSeconds)
                put("videoFileName", ex.videoFileName)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeExercises(json: String): List<WorkoutExercise> {
        val list = mutableListOf<WorkoutExercise>()
        if (json.isBlank()) return list
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    WorkoutExercise(
                        exerciseId = obj.optString("exerciseId"),
                        exerciseName = obj.optString("exerciseName"),
                        targetSets = obj.optInt("targetSets", 3),
                        targetReps = obj.optString("targetReps", "10-12"),
                        targetWeightKg = obj.optDouble("targetWeightKg", 0.0),
                        restSeconds = obj.optInt("restSeconds", 60),
                        videoFileName = obj.optString("videoFileName", "")
                    )
                )
            }
        } catch (_: Exception) { }
        return list
    }
}
