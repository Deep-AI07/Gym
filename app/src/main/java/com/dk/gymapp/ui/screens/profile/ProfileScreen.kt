package com.dk.gymapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.dk.gymapp.data.model.UserAccount
import com.dk.gymapp.data.model.UserProfile
import com.dk.gymapp.data.model.UserRole
import com.dk.gymapp.ui.components.*
import com.dk.gymapp.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserAccount,
    userProfile: UserProfile,
    onUpdateProfile: (UserProfile) -> Unit,
    onResetData: () -> Unit,
    onOpenLoginDialog: () -> Unit,
    onOpenAdminAddExercise: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetConfirmationDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

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
        // HEADER / AVATAR CARD
        item {
            GymCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (currentUser.role == UserRole.ADMIN) AccentOrange.copy(alpha = 0.5f) else GymGreen.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (currentUser.role == UserRole.ADMIN) listOf(AccentOrange, GymGreen)
                                    else listOf(GymGreen, AccentCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.displayName.take(2).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = currentUser.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (currentUser.role == UserRole.ADMIN) AccentOrange.copy(alpha = 0.2f) else GymGreen.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (currentUser.role == UserRole.ADMIN) "ADMINISTRATOR (Full App Control)" else "ACTIVE ATHLETE",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (currentUser.role == UserRole.ADMIN) AccentOrange else GymGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = if (currentUser.role == UserRole.ADMIN) "App Content Management Enabled" else userProfile.fitnessGoal,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = GymGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PHYSICAL METRICS ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${userProfile.age} yrs", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Age", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${userProfile.heightCm.toInt()} cm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Height", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val weightDisplay = if (userProfile.useKg) "${userProfile.weightKg} kg" else "${(userProfile.weightKg * 2.20462).toInt()} lbs"
                        Text(weightDisplay, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Weight", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${userProfile.preferredWorkoutDays} d/wk", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Target", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SWITCH ACCOUNT / LOGIN BUTTON
                OutlinedButton(
                    onClick = onOpenLoginDialog,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentUser.role == UserRole.ADMIN) "Switch Account (Currently Admin)" else "Switch to Admin / User Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // ADMIN MANAGEMENT PANEL (VISIBLE FOR ADMIN ONLY)
        if (currentUser.role == UserRole.ADMIN) {
            item {
                SectionHeader(
                    title = "Admin Management Hub",
                    subtitle = "Manage exercises, routines, and app database"
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GymCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAdminAddExercise() },
                        borderColor = GymGreen.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = GymGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Add New Exercise to Library", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                    Text("Configure name, muscles, 3 tiers, and video links", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // PREFERENCES & SETTINGS
        item {
            SectionHeader(title = "App Preferences", subtitle = "Units, sound & timer defaults")

            GymCard(modifier = Modifier.fillMaxWidth()) {
                // UNIT TOGGLE (KG / LB)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Units of Measurement", fontWeight = FontWeight.SemiBold)
                        Text(if (userProfile.useKg) "Kilograms (kg)" else "Pounds (lbs)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = userProfile.useKg,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(useKg = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GymGreen, checkedTrackColor = GymGreenDark)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // SOUND FX TOGGLE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sound Feedback", fontWeight = FontWeight.SemiBold)
                        Text("Cues for rest timer and set completion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = userProfile.soundEnabled,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(soundEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GymGreen, checkedTrackColor = GymGreenDark)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // VIBRATION TOGGLE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Vibration / Haptics", fontWeight = FontWeight.SemiBold)
                        Text("Haptic pulse when rest timer ends", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = userProfile.vibrationEnabled,
                        onCheckedChange = { onUpdateProfile(userProfile.copy(vibrationEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GymGreen, checkedTrackColor = GymGreenDark)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // DEFAULT REST INTERVAL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default Rest Interval", fontWeight = FontWeight.SemiBold)
                        Text("${userProfile.defaultRestSeconds} seconds between sets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(45, 60, 90).forEach { sec ->
                            val isSelected = userProfile.defaultRestSeconds == sec
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onUpdateProfile(userProfile.copy(defaultRestSeconds = sec)) },
                                color = if (isSelected) GymGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${sec}s",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // DATA MANAGEMENT & ABOUT
        item {
            SectionHeader(title = "Data & App", subtitle = "Maintenance & Information")

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // RESET DATA BUTTON
                GymCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showResetConfirmationDialog = true },
                    borderColor = AccentRed.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = AccentRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Reset Sample Data", fontWeight = FontWeight.Bold, color = AccentRed)
                                Text("Restores initial exercises, weekly split & history", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // ABOUT BUTTON
                GymCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAboutDialog = true }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GymGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("About GYM FIT", fontWeight = FontWeight.Bold)
                                Text("Version 1.0 • Train Hard. Stay Strong.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // EDIT PROFILE MODAL DIALOG
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(userProfile.name) }
        var taglineInput by remember { mutableStateOf(userProfile.tagline) }
        var ageInput by remember { mutableStateOf("${userProfile.age}") }
        var heightInput by remember { mutableStateOf("${userProfile.heightCm.toInt()}") }
        var weightInput by remember { mutableStateOf("${userProfile.weightKg}") }
        var selectedGoal by remember { mutableStateOf(userProfile.fitnessGoal) }
        var selectedExperience by remember { mutableStateOf(userProfile.experienceLevel) }
        var daysInput by remember { mutableStateOf("${userProfile.preferredWorkoutDays}") }

        val goals = listOf("Build Muscle", "Lose Weight", "Improve Strength", "Improve Endurance", "Stay Fit", "General Fitness")
        val experiences = listOf("Beginner", "Intermediate", "Advanced", "Elite")

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Athlete Profile", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Athlete Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = taglineInput,
                            onValueChange = { taglineInput = it },
                            label = { Text("Motivation Tagline") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { heightInput = it },
                                label = { Text("Height (cm)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = daysInput,
                                onValueChange = { daysInput = it },
                                label = { Text("Target Days/Wk") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Text("Primary Fitness Goal", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            goals.forEach { g ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedGoal = g }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedGoal == g, onClick = { selectedGoal = g })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(g, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                    item {
                        Text("Experience Level", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            experiences.forEach { exp ->
                                FilterChip(
                                    selected = selectedExperience == exp,
                                    onClick = { selectedExperience = exp },
                                    label = { Text(exp, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GymGreen,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated = userProfile.copy(
                            name = nameInput.trim().ifBlank { "Alex Morgan" },
                            tagline = taglineInput.trim().ifBlank { "Train Hard. Stay Strong." },
                            age = ageInput.toIntOrNull() ?: 26,
                            heightCm = heightInput.toDoubleOrNull() ?: 180.0,
                            weightKg = weightInput.toDoubleOrNull() ?: 78.0,
                            preferredWorkoutDays = (daysInput.toIntOrNull() ?: 6).coerceIn(1, 7),
                            fitnessGoal = selectedGoal,
                            experienceLevel = selectedExperience
                        )
                        onUpdateProfile(updated)
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save Changes", color = GymGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // RESET CONFIRMATION DIALOG
    if (showResetConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmationDialog = false },
            title = { Text("Reset Sample Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will reset all workouts, exercises, history logs, and measurements back to default factory sample data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetData()
                        showResetConfirmationDialog = false
                    }
                ) {
                    Text("Reset All", color = AccentRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmationDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // ABOUT DIALOG
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = GymGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GYM FIT", fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Version 1.0 (Production Release)", fontWeight = FontWeight.Bold)
                    Text("Tagline: Train Hard. Stay Strong.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("GYM FIT features Mon-Sat workout splits, 3-tier exercise levels, video tutorial guidance, rest countdowns, and Admin app management.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Built with Jetpack Compose & Material 3.", color = GymGreenLight, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = GymGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
