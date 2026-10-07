package com.dk.gymapp.data.database

import com.dk.gymapp.data.model.BodyMeasurement
import com.dk.gymapp.data.model.Exercise
import com.dk.gymapp.data.model.PersonalRecord
import com.dk.gymapp.data.model.WorkoutExercise
import com.dk.gymapp.data.model.WorkoutHistory
import com.dk.gymapp.data.model.WorkoutPlan

object SampleData {

    val defaultExercises: List<Exercise> = listOf(
        Exercise(
            id = "bench_press",
            name = "Barbell Bench Press",
            muscleGroup = "Chest",
            secondaryMuscles = listOf("Triceps", "Anterior Deltoids"),
            equipment = "Barbell & Bench",
            difficulty = "All Levels",
            instructions = listOf(
                "Lie flat on the bench with eyes directly under the racked bar.",
                "Grip the bar slightly wider than shoulder-width, wrists straight.",
                "Unrack the bar and bring it directly over your mid-chest with arms locked.",
                "Lower the bar slowly under control until it gently touches your sternum.",
                "Press forcefully upwards by driving through your feet and contracting your pecs.",
                "Lock out arms briefly at top position and repeat."
            ),
            setsRecommendation = 4,
            repsRecommendation = "8-10",
            restTimeSeconds = 90,
            safetyTips = listOf(
                "Always keep your feet flat on the floor for stability.",
                "Do not bounce the bar off your chest.",
                "Use a spotter or safety pins when lifting heavy weights."
            ),
            commonMistakes = listOf(
                "Flaring elbows at 90 degrees (keep them at roughly 45-75 degrees).",
                "Arching lower back excessively off the bench.",
                "Bouncing the bar violently off the sternum."
            ),
            videoFileName = "benchpress.mp4",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=rT7DgCr-3pg",
            beginnerGuide = "Level: Beginner • 3 sets × 10-12 reps with empty bar or light dumbbells. Focus entirely on controlled elbow tuck (45°) and bar path.",
            intermediateGuide = "Level: Intermediate • 4 sets × 8-10 reps at 65-75% 1RM. Implement progressive overload and a 1-second pause on the chest.",
            advancedGuide = "Level: Advanced • 5 sets × 3-5 reps heavy power protocol (80-90% 1RM). Drive legs through the floor and use drop sets or paused eccentrics."
        ),
        Exercise(
            id = "push_up",
            name = "Push-Up",
            muscleGroup = "Chest",
            secondaryMuscles = listOf("Triceps", "Core", "Shoulders"),
            equipment = "Bodyweight",
            difficulty = "All Levels",
            instructions = listOf(
                "Start in a high plank position with hands slightly wider than shoulders.",
                "Maintain a rigid straight line from head to heels by tightening core and glutes.",
                "Lower your chest until it is about an inch from the floor.",
                "Push the floor away through your palms to return to the starting position."
            ),
            setsRecommendation = 3,
            repsRecommendation = "15-20",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Avoid letting your lower back sag.",
                "Keep your neck neutral looking slightly ahead."
            ),
            commonMistakes = listOf(
                "Sagging hips or piking hips into the air.",
                "Flaring elbows too far outward."
            ),
            videoFileName = "pushup.mp4",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=IODxDxX7oi4",
            beginnerGuide = "Level: Beginner • 3 sets × 8-10 reps on knees or incline bench to master rigid pelvic alignment.",
            intermediateGuide = "Level: Intermediate • 3 sets × 15-20 standard floor push-ups with 2-second negative tempo.",
            advancedGuide = "Level: Advanced • 4 sets × 25+ reps, or feet-elevated / weighted vest push-ups with explosive concentric drive."
        ),
        Exercise(
            id = "squat",
            name = "Barbell Squat",
            muscleGroup = "Legs",
            secondaryMuscles = listOf("Glutes", "Hamstrings", "Lower Back", "Core"),
            equipment = "Barbell & Squat Rack",
            difficulty = "All Levels",
            instructions = listOf(
                "Position bar across upper traps, feet shoulder-width apart, toes pointed slightly out.",
                "Inhale, brace your core, and initiate the movement by hinging hips backward.",
                "Bend knees and descend until thighs are at least parallel to the floor.",
                "Drive through whole foot to stand back up, exhaling near the top."
            ),
            setsRecommendation = 4,
            repsRecommendation = "6-8",
            restTimeSeconds = 120,
            safetyTips = listOf(
                "Ensure knees track in the same direction as your toes.",
                "Never let knees cave inwards.",
                "Keep chest upright and spine neutral."
            ),
            commonMistakes = listOf(
                "Allowing knees to cave inward during ascent.",
                "Shifting weight forward onto toes.",
                "Rounding lower back at bottom depth."
            ),
            videoFileName = "squat.mp4",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1574680096145-d05b474e2155?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=gcNh17Ckjgg",
            beginnerGuide = "Level: Beginner • 3 sets × 10 goblet squats or bodyweight box squats to engrain hip crease depth.",
            intermediateGuide = "Level: Intermediate • 4 sets × 8 reps with barbell at 70% 1RM, breaking parallel smoothly.",
            advancedGuide = "Level: Advanced • 5 sets × 4-6 heavy reps at 85% 1RM, utilizing pause squats and dynamic pin squats."
        ),
        Exercise(
            id = "deadlift",
            name = "Conventional Deadlift",
            muscleGroup = "Back",
            secondaryMuscles = listOf("Hamstrings", "Glutes", "Traps", "Forearms"),
            equipment = "Barbell",
            difficulty = "All Levels",
            instructions = listOf(
                "Stand with feet hip-width apart, bar over the middle of your feet.",
                "Hinge forward at hips and grip bar just outside your knees.",
                "Pull chest up, drop hips slightly, and engage lats to remove bar slack.",
                "Drive through the floor, extending hips and knees simultaneously to stand tall."
            ),
            setsRecommendation = 3,
            repsRecommendation = "5",
            restTimeSeconds = 180,
            safetyTips = listOf(
                "Never round your lower back under heavy load.",
                "Keep the bar tight to your shins throughout the lift."
            ),
            commonMistakes = listOf(
                "Jerking the bar off the floor.",
                "Hyperextending lower back at the lockout."
            ),
            videoFileName = "",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=op9kVnSso6Q",
            beginnerGuide = "Level: Beginner • 3 sets × 6 reps with kettlebell or hex-bar to master the hip-hinge without spinal flex.",
            intermediateGuide = "Level: Intermediate • 3 sets × 5 reps at 75% 1RM, resetting bar fully on floor each rep.",
            advancedGuide = "Level: Advanced • 4-5 sets × 3 reps heavy pulling at 85-90% 1RM with chalk or hook grip."
        ),
        Exercise(
            id = "pull_up",
            name = "Pull-Up",
            muscleGroup = "Back",
            secondaryMuscles = listOf("Biceps", "Forearms", "Rear Deltoids"),
            equipment = "Pull-Up Bar",
            difficulty = "All Levels",
            instructions = listOf(
                "Grip the overhead bar with an overhand grip slightly wider than shoulders.",
                "Hang with arms fully extended and engage your shoulder blades.",
                "Pull chest toward bar by driving elbows down and back.",
                "Lower under control to a full dead hang before starting next rep."
            ),
            setsRecommendation = 3,
            repsRecommendation = "8-12",
            restTimeSeconds = 90,
            safetyTips = listOf(
                "Avoid kipping or swinging violently.",
                "Control the descent to protect shoulder joints."
            ),
            commonMistakes = listOf(
                "Not achieving full extension at bottom.",
                "Reaching with chin instead of pulling chest up."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=eGo4IYlbE5g",
            beginnerGuide = "Level: Beginner • 3 sets × 8 resistance band assisted pull-ups or slow eccentric negatives.",
            intermediateGuide = "Level: Intermediate • 3 sets × 8-12 bodyweight pull-ups with full dead hang.",
            advancedGuide = "Level: Advanced • 4 sets × 6-8 weighted pull-ups with +10-20kg belt."
        ),
        Exercise(
            id = "overhead_press",
            name = "Overhead Shoulder Press",
            muscleGroup = "Shoulders",
            secondaryMuscles = listOf("Triceps", "Upper Chest", "Core"),
            equipment = "Barbell or Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Rest barbell on front deltoids with hands just outside shoulders.",
                "Brace core, glutes, and quadriceps.",
                "Press bar straight overhead, moving head back slightly to clear bar path.",
                "Lock out arms directly overhead and lower slowly to collarbone."
            ),
            setsRecommendation = 4,
            repsRecommendation = "8-10",
            restTimeSeconds = 90,
            safetyTips = listOf(
                "Keep core tight to prevent hyper-extending the spine."
            ),
            commonMistakes = listOf(
                "Leaning backward excessively.",
                "Pushing bar forward instead of vertical."
            ),
            videoFileName = "",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=2yjwXTZQDDI",
            beginnerGuide = "Level: Beginner • 3 sets × 10 seated dumbbell press to build overhead mobility and stabilization.",
            intermediateGuide = "Level: Intermediate • 4 sets × 8-10 standing barbell military press with tight glute squeeze.",
            advancedGuide = "Level: Advanced • 5 sets × 5 heavy standing overhead press with push-press overload finishers."
        ),
        Exercise(
            id = "dumbbell_curl",
            name = "Dumbbell Bicep Curls",
            muscleGroup = "Arms",
            secondaryMuscles = listOf("Forearms", "Brachialis"),
            equipment = "Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Stand tall with a dumbbell in each hand, palms facing thighs.",
                "Keep elbows stationary near torso and curl weights while supinating palms up.",
                "Squeeze biceps firmly at the top contraction.",
                "Lower weights with controlled 3-second negative tempo."
            ),
            setsRecommendation = 3,
            repsRecommendation = "10-12",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Do not swing body to gain momentum."
            ),
            commonMistakes = listOf(
                "Using shoulder movement to hoist weights.",
                "Dropping weights quickly without resisting eccentric phase."
            ),
            videoFileName = "",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=ykJmrZ5v0Oo",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps with light dumbbells focusing on wrist supination.",
            intermediateGuide = "Level: Intermediate • 4 sets × 10 reps with 3-second eccentric lower.",
            advancedGuide = "Level: Advanced • 4 sets × 8 heavy reps followed immediately by 2 drop sets to complete failure."
        ),
        Exercise(
            id = "tricep_pushdown",
            name = "Cable Tricep Pushdown",
            muscleGroup = "Arms",
            secondaryMuscles = listOf("Forearms"),
            equipment = "Cable Machine",
            difficulty = "All Levels",
            instructions = listOf(
                "Attach rope or straight bar to high cable pulley.",
                "Tuck elbows tight at sides and hinge forward slightly at hips.",
                "Push cable down until arms are fully locked out.",
                "Hold peak squeeze for one second, then return to 90 degrees."
            ),
            setsRecommendation = 3,
            repsRecommendation = "12-15",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Anchor elbows in place; do not let them flare or drift forward."
            ),
            commonMistakes = listOf(
                "Letting upper arms swing back and forth."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1530822847156-5df684ec5ee1?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=2-LAMcpzODU",
            beginnerGuide = "Level: Beginner • 3 sets × 15 reps with moderate cable weight focusing on elbow lockout.",
            intermediateGuide = "Level: Intermediate • 4 sets × 12 reps with rope attachment spreading handles at bottom.",
            advancedGuide = "Level: Advanced • 4 sets × 10 reps heavy + rest-pause protocol."
        ),
        Exercise(
            id = "lat_pulldown",
            name = "Lat Pulldown",
            muscleGroup = "Back",
            secondaryMuscles = listOf("Biceps", "Rear Delts"),
            equipment = "Cable Pulldown Machine",
            difficulty = "All Levels",
            instructions = listOf(
                "Sit with thighs snug beneath pad, grasping bar with wide overhand grip.",
                "Lean torso back about 10-15 degrees with chest lifted.",
                "Pull bar down smoothly to upper chest by retracting shoulder blades.",
                "Slowly let bar return up until arms are fully outstretched."
            ),
            setsRecommendation = 4,
            repsRecommendation = "10-12",
            restTimeSeconds = 75,
            safetyTips = listOf(
                "Do not pull the bar behind your neck."
            ),
            commonMistakes = listOf(
                "Leaning back excessively to turn exercise into a row.",
                "Using jerky momentum."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1605296867304-46d5465a13f1?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=CAwf7n6Luuc",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps feeling the scapular depression.",
            intermediateGuide = "Level: Intermediate • 4 sets × 10-12 reps with full stretch at top.",
            advancedGuide = "Level: Advanced • 4 sets × 8 heavy reps with 2-second hold at collarbone."
        ),
        Exercise(
            id = "walking_lunges",
            name = "Walking Lunges",
            muscleGroup = "Legs",
            secondaryMuscles = listOf("Glutes", "Hamstrings", "Calves"),
            equipment = "Bodyweight or Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Take a controlled step forward with lead foot.",
                "Lower back knee toward ground until both knees are bent at 90 degrees.",
                "Push through front heel to step forward into next lunge.",
                "Alternate legs with a continuous, smooth walking cadence."
            ),
            setsRecommendation = 3,
            repsRecommendation = "12 each leg",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Keep front knee behind front toes.",
                "Maintain upright torso posture."
            ),
            commonMistakes = listOf(
                "Banging rear knee against the floor.",
                "Collapsing forward at the waist."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1434682881908-b43d0467b798?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=L8fvypPrzzs",
            beginnerGuide = "Level: Beginner • 3 sets × 10 steps per leg with bodyweight only.",
            intermediateGuide = "Level: Intermediate • 3 sets × 12 steps per leg holding 10-14kg dumbbells.",
            advancedGuide = "Level: Advanced • 4 sets × 16 steps per leg with barbell across traps or heavy kettlebells."
        ),
        Exercise(
            id = "plank",
            name = "Forearm Plank",
            muscleGroup = "Core",
            secondaryMuscles = listOf("Shoulders", "Glutes"),
            equipment = "Bodyweight",
            difficulty = "All Levels",
            instructions = listOf(
                "Place forearms on the floor with elbows aligned below shoulders.",
                "Extend legs behind you with feet together.",
                "Draw belly button toward spine and squeeze glutes.",
                "Hold static position without letting hips drop or rise."
            ),
            setsRecommendation = 3,
            repsRecommendation = "45-60 sec",
            restTimeSeconds = 45,
            safetyTips = listOf(
                "Breathe continuously; do not hold your breath."
            ),
            commonMistakes = listOf(
                "Sagging hips that stress lumbar spine.",
                "Looking up, straining cervical spine."
            ),
            videoFileName = "",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1566241142559-40e1dab266c6?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=pSHjTRCQxIw",
            beginnerGuide = "Level: Beginner • 3 sets × 30-second hold with knees supported if needed.",
            intermediateGuide = "Level: Intermediate • 3 sets × 60-second strict forearm plank.",
            advancedGuide = "Level: Advanced • 3 sets × 90-120 seconds with weight plate on back."
        ),
        Exercise(
            id = "leg_press",
            name = "Leg Press Machine",
            muscleGroup = "Legs",
            secondaryMuscles = listOf("Glutes", "Hamstrings"),
            equipment = "45-Degree Leg Press Machine",
            difficulty = "All Levels",
            instructions = listOf(
                "Seat yourself with back and head resting against padded support.",
                "Place feet hip-width on sled platform.",
                "Release safety pins and lower weight sled until knees bend at 90 degrees.",
                "Press through heels and midfoot back to starting position without locking knees."
            ),
            setsRecommendation = 4,
            repsRecommendation = "10-12",
            restTimeSeconds = 90,
            safetyTips = listOf(
                "Never lock knees completely at top.",
                "Keep lower back flat against seat pad."
            ),
            commonMistakes = listOf(
                "Butt curling off the seat at deep angle.",
                "Hyperextending knees."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1534367507873-d2d7e24c797f?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=IZxyjW7MPJQ",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps with light sled weight establishing deep knee bend.",
            intermediateGuide = "Level: Intermediate • 4 sets × 10 reps pushing 1.5× bodyweight.",
            advancedGuide = "Level: Advanced • 4 sets × 8 heavy reps + 1 drop set to complete quad burn."
        ),
        Exercise(
            id = "leg_curl",
            name = "Lying Leg Curl",
            muscleGroup = "Legs",
            secondaryMuscles = listOf("Calves"),
            equipment = "Leg Curl Machine",
            difficulty = "All Levels",
            instructions = listOf(
                "Lie face down with pad resting against lower calves/Achilles tendon.",
                "Keep torso flat and grip handles for stability.",
                "Curl pad toward glutes through full hamstring contraction.",
                "Hold for a count, then resist on the way down."
            ),
            setsRecommendation = 3,
            repsRecommendation = "12",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Do not jerk hips up off the bench."
            ),
            commonMistakes = listOf(
                "Using momentum to swing pad upwards."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=1Tq3QdYUuHs",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps with controlled tempo.",
            intermediateGuide = "Level: Intermediate • 3 sets × 10-12 reps with 2-second hold at peak.",
            advancedGuide = "Level: Advanced • 4 sets × 8 heavy reps with slow 4-second negatives."
        ),
        Exercise(
            id = "dumbbell_row",
            name = "One-Arm Dumbbell Row",
            muscleGroup = "Back",
            secondaryMuscles = listOf("Biceps", "Rear Delts", "Core"),
            equipment = "Dumbbell & Flat Bench",
            difficulty = "All Levels",
            instructions = listOf(
                "Place left knee and left hand on flat bench, back horizontal to floor.",
                "Hold dumbbell in right hand with arm extended.",
                "Pull dumbbell toward hip crease, keeping elbow tucked.",
                "Squeeze lat at top, then lower with control."
            ),
            setsRecommendation = 3,
            repsRecommendation = "10 each arm",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Keep spine flat and parallel to bench throughout."
            ),
            commonMistakes = listOf(
                "Twisting torso to heave dumbbell up."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=roCP6wCXPqo",
            beginnerGuide = "Level: Beginner • 3 sets × 10 reps with light dumbbell prioritizing lat contraction.",
            intermediateGuide = "Level: Intermediate • 4 sets × 10 reps pulling heavy with strict spine angle.",
            advancedGuide = "Level: Advanced • 4 sets × 8 Kroc rows with high weight and massive volume."
        ),
        Exercise(
            id = "lateral_raise",
            name = "Dumbbell Lateral Raise",
            muscleGroup = "Shoulders",
            secondaryMuscles = listOf("Traps"),
            equipment = "Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Stand upright with dumbbells at sides, slight bend in elbows.",
                "Raise arms out to sides until parallel to the floor.",
                "Lead with elbows and keep pinkies slightly elevated.",
                "Lower slowly under full tension."
            ),
            setsRecommendation = 4,
            repsRecommendation = "12-15",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Use moderate weight; avoid swinging hips."
            ),
            commonMistakes = listOf(
                "Shrugging shoulders into ears."
            ),
            videoFileName = "",
            isFavorite = true,
            photoUrl = "https://images.unsplash.com/photo-1541534741688-6078c6bfb5c5?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=3VcKaXpzqRo",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps with light weight mastering deltoid isolation.",
            intermediateGuide = "Level: Intermediate • 4 sets × 15 reps with slow eccentric.",
            advancedGuide = "Level: Advanced • 4 sets × 15 reps followed by 10 partial reps to incinerate delts."
        ),
        Exercise(
            id = "mountain_climbers",
            name = "Mountain Climbers",
            muscleGroup = "Core",
            secondaryMuscles = listOf("Shoulders", "Hip Flexors", "Cardio"),
            equipment = "Bodyweight",
            difficulty = "All Levels",
            instructions = listOf(
                "Start in pushup plank position with wrists under shoulders.",
                "Drive right knee toward chest swiftly.",
                "Quickly switch legs, extending right leg while driving left knee forward.",
                "Maintain quick, rhythmic cadence like running horizontally."
            ),
            setsRecommendation = 3,
            repsRecommendation = "30-45 sec",
            restTimeSeconds = 45,
            safetyTips = listOf(
                "Keep hips level and do not let butt bounce into the air."
            ),
            commonMistakes = listOf(
                "Bouncing hips excessively."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=nmwgirgXLYM",
            beginnerGuide = "Level: Beginner • 3 sets × 25 seconds slow and controlled.",
            intermediateGuide = "Level: Intermediate • 3 sets × 45 seconds steady pace.",
            advancedGuide = "Level: Advanced • 4 sets × 60 seconds sprint cadence."
        ),
        Exercise(
            id = "russian_twist",
            name = "Russian Twists",
            muscleGroup = "Core",
            secondaryMuscles = listOf("Obliques"),
            equipment = "Bodyweight or Medicine Ball",
            difficulty = "All Levels",
            instructions = listOf(
                "Sit on floor with knees bent and feet elevated slightly.",
                "Lean torso back 45 degrees to engage core.",
                "Rotate torso side to side, touching floor beside hips with both hands."
            ),
            setsRecommendation = 3,
            repsRecommendation = "20 total reps",
            restTimeSeconds = 45,
            safetyTips = listOf(
                "Rotate from thoracic spine, not just waving arms."
            ),
            commonMistakes = listOf(
                "Rounding lower back excessively."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1566241142559-40e1dab266c6?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=wkD8rjkodUI",
            beginnerGuide = "Level: Beginner • 3 sets × 16 reps with feet planted on floor.",
            intermediateGuide = "Level: Intermediate • 3 sets × 20 reps with feet elevated.",
            advancedGuide = "Level: Advanced • 4 sets × 24 reps holding 5-10kg weight plate."
        ),
        Exercise(
            id = "incline_dumbbell_fly",
            name = "Incline Dumbbell Fly",
            muscleGroup = "Chest",
            secondaryMuscles = listOf("Anterior Deltoids"),
            equipment = "Incline Bench & Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Set bench to 30-degree incline, lie back holding dumbbells above chest.",
                "Maintain slight bend in elbows.",
                "Open arms out wide in an arc until deep chest stretch is felt.",
                "Contract pecs to bring dumbbells back together over chest."
            ),
            setsRecommendation = 3,
            repsRecommendation = "12",
            restTimeSeconds = 60,
            safetyTips = listOf(
                "Do not drop elbows too low below shoulder plane."
            ),
            commonMistakes = listOf(
                "Turning movement into a pressing exercise."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=bDaIL_zKbGs",
            beginnerGuide = "Level: Beginner • 3 sets × 12 reps light weight emphasizing stretch.",
            intermediateGuide = "Level: Intermediate • 3 sets × 12 reps holding stretch for 1 second.",
            advancedGuide = "Level: Advanced • 4 sets × 10 reps with drop sets on final set."
        ),
        Exercise(
            id = "jumping_jacks",
            name = "Jumping Jacks",
            muscleGroup = "Cardio",
            secondaryMuscles = listOf("Calves", "Shoulders", "Full Body"),
            equipment = "Bodyweight",
            difficulty = "All Levels",
            instructions = listOf(
                "Stand upright with feet together and arms at sides.",
                "Jump feet outward while raising arms overhead until hands touch.",
                "Immediately jump feet back together while lowering arms to sides.",
                "Maintain energetic, rhythmic pace."
            ),
            setsRecommendation = 3,
            repsRecommendation = "60 sec",
            restTimeSeconds = 30,
            safetyTips = listOf(
                "Land softly on balls of feet to protect knee joints."
            ),
            commonMistakes = listOf(
                "Landing flat-footed with heavy impact."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1598971639058-fab3c3109a00?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=c4DAnQ6DtF8",
            beginnerGuide = "Level: Beginner • 3 sets × 30 seconds steady rhythm.",
            intermediateGuide = "Level: Intermediate • 3 sets × 60 seconds rapid cadence.",
            advancedGuide = "Level: Advanced • 4 sets × 90 seconds continuous cardio intervals."
        ),
        Exercise(
            id = "wrist_curls",
            name = "Barbell Wrist Curls",
            muscleGroup = "Arms",
            secondaryMuscles = listOf("Forearms"),
            equipment = "Barbell or Dumbbells",
            difficulty = "All Levels",
            instructions = listOf(
                "Kneel next to bench with forearms resting across the pad, wrists hanging over edge.",
                "Allow bar to roll down into fingers, then curl back up using forearm flexors.",
                "Hold peak squeeze for one second before lowering."
            ),
            setsRecommendation = 3,
            repsRecommendation = "15-20",
            restTimeSeconds = 45,
            safetyTips = listOf(
                "Do not use excessive weight; forearms respond to high repetition."
            ),
            commonMistakes = listOf(
                "Jerking wrists sharply."
            ),
            videoFileName = "",
            isFavorite = false,
            photoUrl = "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?w=600&q=80",
            videoUrl = "https://www.youtube.com/watch?v=r5-nYE_1CXo",
            beginnerGuide = "Level: Beginner • 3 sets × 15 reps light bar.",
            intermediateGuide = "Level: Intermediate • 4 sets × 15-20 reps with peak flex.",
            advancedGuide = "Level: Advanced • 4 sets × 25 reps paired as superset with reverse wrist curls."
        )
    )

    // WEEKLY SPLIT PLANS: Monday-Saturday + Sunday Rest
    val defaultWorkouts: List<WorkoutPlan> = listOf(
        // MON: Chest & Biceps
        WorkoutPlan(
            id = "split_mon",
            title = "Monday: Chest & Biceps",
            subtitle = "Push & Pull Hypertrophy Split (Day 1)",
            category = "Weekly Split",
            difficulty = "Intermediate",
            durationMinutes = 50,
            muscleGroups = listOf("Chest", "Biceps", "Forearms"),
            equipment = "Barbell, Dumbbells, Bench",
            description = "Start the week with high-volume chest pressing and bicep peaks to maximize upper body growth.",
            exercises = listOf(
                WorkoutExercise("bench_press", "Barbell Bench Press", 4, "8-10", 75.0, 90, "benchpress.mp4"),
                WorkoutExercise("incline_dumbbell_fly", "Incline Dumbbell Fly", 3, "12", 16.0, 60),
                WorkoutExercise("push_up", "Push-Up", 3, "15-20", 0.0, 60, "pushup.mp4"),
                WorkoutExercise("dumbbell_curl", "Dumbbell Bicep Curls", 4, "10-12", 14.0, 60)
            ),
            isFavorite = true
        ),
        // TUE: Back & Triceps
        WorkoutPlan(
            id = "split_tue",
            title = "Tuesday: Back & Triceps",
            subtitle = "V-Taper Pull & Arm Lockout (Day 2)",
            category = "Weekly Split",
            difficulty = "Intermediate",
            durationMinutes = 50,
            muscleGroups = listOf("Back", "Triceps"),
            equipment = "Barbell, Cable Machine, Dumbbells",
            description = "Build a wide lat spread, upper back density, and horseshoe triceps with compound pulling and cable pushdowns.",
            exercises = listOf(
                WorkoutExercise("deadlift", "Conventional Deadlift", 4, "5", 120.0, 150),
                WorkoutExercise("lat_pulldown", "Lat Pulldown", 4, "10-12", 60.0, 75),
                WorkoutExercise("dumbbell_row", "One-Arm Dumbbell Row", 3, "10", 22.0, 60),
                WorkoutExercise("tricep_pushdown", "Cable Tricep Pushdown", 4, "12-15", 25.0, 60)
            ),
            isFavorite = true
        ),
        // WED: Legs, Shoulders & Abs
        WorkoutPlan(
            id = "split_wed",
            title = "Wednesday: Legs, Shoulders & Abs",
            subtitle = "Lower Body Drive & Deltoid Power (Day 3)",
            category = "Weekly Split",
            difficulty = "Intermediate",
            durationMinutes = 55,
            muscleGroups = listOf("Legs", "Shoulders", "Core"),
            equipment = "Barbell, Squat Rack, Dumbbells, Mat",
            description = "Heavy leg drive combined with overhead deltoid pressing and isometric core stability.",
            exercises = listOf(
                WorkoutExercise("squat", "Barbell Squat", 4, "6-8", 90.0, 120, "squat.mp4"),
                WorkoutExercise("leg_press", "Leg Press Machine", 3, "10-12", 160.0, 90),
                WorkoutExercise("overhead_press", "Overhead Shoulder Press", 4, "8", 40.0, 90),
                WorkoutExercise("lateral_raise", "Dumbbell Lateral Raise", 3, "15", 10.0, 60),
                WorkoutExercise("plank", "Forearm Plank", 3, "60s", 0.0, 45)
            ),
            isFavorite = true
        ),
        // THU: Chest & Biceps (Volume)
        WorkoutPlan(
            id = "split_thu",
            title = "Thursday: Chest & Biceps (Volume)",
            subtitle = "Hypertrophy Pump & Density (Day 4)",
            category = "Weekly Split",
            difficulty = "Intermediate",
            durationMinutes = 45,
            muscleGroups = listOf("Chest", "Biceps"),
            equipment = "Dumbbells, Bench, Bodyweight",
            description = "High rep volume and controlled eccentrics to trigger muscle cell swelling in chest and biceps.",
            exercises = listOf(
                WorkoutExercise("incline_dumbbell_fly", "Incline Dumbbell Fly", 4, "12-15", 14.0, 60),
                WorkoutExercise("push_up", "Push-Up", 4, "20", 0.0, 45, "pushup.mp4"),
                WorkoutExercise("dumbbell_curl", "Dumbbell Bicep Curls", 4, "12", 12.0, 60)
            ),
            isFavorite = true
        ),
        // FRI: Back & Triceps (Thickness)
        WorkoutPlan(
            id = "split_fri",
            title = "Friday: Back & Triceps (Thickness)",
            subtitle = "Lat Pulling & Tricep Burnout (Day 5)",
            category = "Weekly Split",
            difficulty = "Intermediate",
            durationMinutes = 45,
            muscleGroups = listOf("Back", "Triceps"),
            equipment = "Pull-Up Bar, Cables, Dumbbells",
            description = "Focus on horizontal rows, vertical pull-ups, and long-head tricep contractions.",
            exercises = listOf(
                WorkoutExercise("pull_up", "Pull-Up", 4, "8-10", 0.0, 90),
                WorkoutExercise("dumbbell_row", "One-Arm Dumbbell Row", 4, "10", 24.0, 60),
                WorkoutExercise("lat_pulldown", "Lat Pulldown", 3, "12", 55.0, 60),
                WorkoutExercise("tricep_pushdown", "Cable Tricep Pushdown", 4, "15", 22.5, 60)
            ),
            isFavorite = true
        ),
        // SAT: Legs, Shoulders, Forearms & Abs
        WorkoutPlan(
            id = "split_sat",
            title = "Saturday: Legs, Shoulders, Forearms & Abs",
            subtitle = "Full Power Circuit & Grip (Day 6)",
            category = "Weekly Split",
            difficulty = "Advanced",
            durationMinutes = 55,
            muscleGroups = listOf("Legs", "Shoulders", "Forearms", "Core"),
            equipment = "Barbell, Dumbbells, Leg Curl Machine, Mat",
            description = "Wrap up the training week with leg curls, walking lunges, boulder shoulder isolation, grip training, and core rotators.",
            exercises = listOf(
                WorkoutExercise("walking_lunges", "Walking Lunges", 3, "12 each", 12.0, 60),
                WorkoutExercise("leg_curl", "Lying Leg Curl", 3, "12", 40.0, 60),
                WorkoutExercise("overhead_press", "Overhead Shoulder Press", 3, "8", 40.0, 90),
                WorkoutExercise("lateral_raise", "Dumbbell Lateral Raise", 4, "15", 10.0, 45),
                WorkoutExercise("wrist_curls", "Barbell Wrist Curls", 3, "20", 20.0, 45),
                WorkoutExercise("russian_twist", "Russian Twists", 3, "25", 5.0, 45)
            ),
            isFavorite = true
        ),
        // SUN: Rest & Active Recovery
        WorkoutPlan(
            id = "split_sun",
            title = "Sunday: Rest & Active Recovery",
            subtitle = "Mobility, Stretching & Regeneration (Day 7)",
            category = "Weekly Split",
            difficulty = "Beginner",
            durationMinutes = 20,
            muscleGroups = listOf("Full Body", "Mobility"),
            equipment = "Mat, Foam Roller",
            description = "Active recovery protocol: promote blood flow, relieve muscle soreness, stretch tight hip flexors and lats.",
            exercises = listOf(
                WorkoutExercise("plank", "Forearm Plank", 2, "45s", 0.0, 60),
                WorkoutExercise("mountain_climbers", "Mountain Climbers", 2, "30s", 0.0, 60),
                WorkoutExercise("jumping_jacks", "Jumping Jacks", 2, "45s", 0.0, 60)
            ),
            isFavorite = true
        ),
        // ADDITIONAL FAVORITES
        WorkoutPlan(
            id = "plan_full_body",
            title = "Full Body Power Blast",
            subtitle = "Complete strength & functional movement",
            category = "Full Body",
            difficulty = "Beginner",
            durationMinutes = 40,
            muscleGroups = listOf("Chest", "Back", "Legs", "Core"),
            equipment = "Barbell, Dumbbells, Bodyweight",
            description = "A time-efficient full body routine targeting all major muscle groups for maximum calorie burn and balanced muscle development.",
            exercises = listOf(
                WorkoutExercise("squat", "Barbell Squat", 3, "10", 60.0, 90, "squat.mp4"),
                WorkoutExercise("bench_press", "Barbell Bench Press", 3, "10", 60.0, 90, "benchpress.mp4"),
                WorkoutExercise("dumbbell_row", "One-Arm Dumbbell Row", 3, "10", 18.0, 60),
                WorkoutExercise("overhead_press", "Overhead Shoulder Press", 3, "10", 35.0, 75),
                WorkoutExercise("plank", "Forearm Plank", 3, "45s", 0.0, 45)
            ),
            isFavorite = false
        )
    )

    fun getSampleWorkoutHistory(): List<WorkoutHistory> {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        return listOf(
            WorkoutHistory(
                id = 1,
                workoutId = "split_mon",
                workoutTitle = "Monday: Chest & Biceps",
                dateMillis = now - (oneDay * 0),
                durationSeconds = 2520, // 42 min
                caloriesBurned = 380,
                exercisesCompleted = 4,
                totalSets = 14,
                notes = "Felt strong on bench press! Hit 80kg on final set.",
                rating = 5
            ),
            WorkoutHistory(
                id = 2,
                workoutId = "split_tue",
                workoutTitle = "Tuesday: Back & Triceps",
                dateMillis = now - (oneDay * 1),
                durationSeconds = 2700, // 45 min
                caloriesBurned = 410,
                exercisesCompleted = 4,
                totalSets = 13,
                notes = "Great lat pump from pull-ups and deadlifts.",
                rating = 5
            ),
            WorkoutHistory(
                id = 3,
                workoutId = "split_wed",
                workoutTitle = "Wednesday: Legs, Shoulders & Abs",
                dateMillis = now - (oneDay * 3),
                durationSeconds = 3100, // 51 min
                caloriesBurned = 490,
                exercisesCompleted = 5,
                totalSets = 16,
                notes = "Heavy squats felt smooth. Great shoulder pump.",
                rating = 5
            ),
            WorkoutHistory(
                id = 4,
                workoutId = "split_thu",
                workoutTitle = "Thursday: Chest & Biceps (Volume)",
                dateMillis = now - (oneDay * 4),
                durationSeconds = 2400, // 40 min
                caloriesBurned = 340,
                exercisesCompleted = 3,
                totalSets = 12,
                notes = "Incline fly stretch was incredible.",
                rating = 5
            ),
            WorkoutHistory(
                id = 5,
                workoutId = "split_fri",
                workoutTitle = "Friday: Back & Triceps (Thickness)",
                dateMillis = now - (oneDay * 6),
                durationSeconds = 2300, // 38 min
                caloriesBurned = 360,
                exercisesCompleted = 4,
                totalSets = 14,
                notes = "Pull-ups form was crisp and strict.",
                rating = 5
            )
        )
    }

    fun getSampleMeasurements(): List<BodyMeasurement> {
        val now = System.currentTimeMillis()
        val oneWeek = 86400000L * 7
        return listOf(
            BodyMeasurement(1, now, 77.8, 104.0, 81.0, 38.5, 58.0),
            BodyMeasurement(2, now - oneWeek, 78.3, 103.5, 81.8, 38.0, 57.8),
            BodyMeasurement(3, now - (oneWeek * 2), 78.9, 103.0, 82.5, 37.8, 57.5),
            BodyMeasurement(4, now - (oneWeek * 3), 79.5, 102.5, 83.2, 37.5, 57.0)
        )
    }

    val defaultPersonalRecords: List<PersonalRecord> = listOf(
        PersonalRecord(1, "Barbell Bench Press", 100.0, "kg", System.currentTimeMillis() - 86400000L * 3),
        PersonalRecord(2, "Barbell Squat", 135.0, "kg", System.currentTimeMillis() - 86400000L * 10),
        PersonalRecord(3, "Conventional Deadlift", 160.0, "kg", System.currentTimeMillis() - 86400000L * 14),
        PersonalRecord(4, "Overhead Shoulder Press", 65.0, "kg", System.currentTimeMillis() - 86400000L * 20),
        PersonalRecord(5, "Pull-Ups (Max Reps)", 18.0, "reps", System.currentTimeMillis() - 86400000L * 5),
        PersonalRecord(6, "Forearm Plank", 180.0, "sec", System.currentTimeMillis() - 86400000L * 8)
    )
}
