package com.example.yumi.companion

import android.content.Context
import android.util.Log
import android.view.View // Required for ParticleSystem placeholder
import com.airbnb.lottie.LottieAnimationView
import com.example.yumi.R // For placeholder R.raw.dream_animation
import com.example.yumi.models.enums.TimeOfDay // Assuming this path
import com.example.yumi.models.enums.UserAction // Assuming this path
import kotlin.random.Random

class YumiCompanion(private val context: Context) {

    enum class Mood {
        HAPPY, EXCITED, SLEEPY, ENCOURAGING, CELEBRATING, THINKING, LOVING
    }

    enum class CompanionType {
        TOTORO_INSPIRED,  // Fluffy forest spirit
        SKY_WHALE,        // Flying whale
        STAR_CAT,         // Celestial cat
        CLOUD_BUNNY,      // Fluffy cloud rabbit
        LEAF_SPRITE       // Nature fairy
    }

    private var currentMood = Mood.HAPPY
    private var affectionLevel = 0
    // This mascotView is intended to be added to a layout by MainActivity
    val mascotView: LottieAnimationView by lazy {
        LottieAnimationView(context).apply {
            // Placeholder animation, actual animation depends on getAnimationForMood
            setAnimation(getAnimationForMood(currentMood))
            loop(true)
            playAnimation()
        }
    }

    fun appearWithGreeting(): String {
        val greetings = when (getTimeOfDay()) {
            TimeOfDay.MORNING -> listOf(
                "Good morning! What dreams shall we chase today? ✨",
                "Rise and shine! Your wishes are waiting! 🌅",
                "A beautiful day for making dreams come true! 🌻"
            )
            TimeOfDay.AFTERNOON -> listOf(
                "Hello sunshine! Ready for an adventure? ☀️",
                "What wonderful wishes shall we explore? 🌈",
                "The perfect time for dreaming big! 💫"
            )
            TimeOfDay.EVENING -> listOf(
                "Good evening, dreamer! 🌙",
                "Let's capture today's beautiful moments! ✨",
                "Time to add some magical wishes! 🌟"
            )
            TimeOfDay.NIGHT -> listOf(
                "Sweet dreams are made of wishes! 🌙",
                "The stars are perfect for wishing tonight! ⭐",
                "Quiet moments for beautiful dreams! 🌌"
            )
        }
        animateEntrance()
        return greetings.random()
    }

    fun reactToUserAction(action: UserAction) {
        when (action) {
            UserAction.ADD_WISH -> {
                currentMood = Mood.EXCITED
                showReaction("Yay! A new dream! 🎉")
                giveStarDust(10)
            }
            UserAction.COMPLETE_WISH -> {
                currentMood = Mood.CELEBRATING
                showReaction("Amazing! You did it! 🎊")
                giveStarDust(50)
                triggerCelebration()
            }
            UserAction.SHARE_WISH -> {
                currentMood = Mood.LOVING
                showReaction("Sharing dreams makes them stronger! 💝")
            }
            UserAction.DAILY_VISIT -> {
                affectionLevel++
                showReaction(getDailyMessage())
            }
            // TODO: Add other UserAction cases if any
        }
        updateAnimation()
    }

    private fun triggerCelebration() {
        // Particle explosion, confetti, magical effects
        Log.d("YumiCompanion", "Triggering celebration")
        ParticleSystem.createMagicalExplosion(mascotView) // Placeholder call
    }

    // --- Stub/Placeholder Methods & Classes ---

    private fun getTimeOfDay(): TimeOfDay {
        // TODO: Implement actual logic to determine time of day
        val calendar = java.util.Calendar.getInstance()
        return when (calendar.get(java.util.Calendar.HOUR_OF_DAY)) {
            in 6..11 -> TimeOfDay.MORNING
            in 12..17 -> TimeOfDay.AFTERNOON
            in 18..21 -> TimeOfDay.EVENING
            else -> TimeOfDay.NIGHT
        }
    }

    private fun getAnimationForMood(mood: Mood): Int {
        Log.d("YumiCompanion", "getAnimationForMood called for $mood. Needs actual Lottie files.")
        // TODO: Return actual Lottie resource ID based on mood
        // Example: when(mood) { Mood.HAPPY -> R.raw.companion_happy ... }
        return R.raw.dream_animation // Placeholder
    }

    private fun animateEntrance() {
        Log.d("YumiCompanion", "animateEntrance called. Needs implementation.")
        // TODO: Implement entrance animation for the mascotView
        mascotView.alpha = 0f
        mascotView.animate().alpha(1f).setDuration(500).start()
    }

    private fun showReaction(message: String) {
        Log.d("YumiCompanion", "showReaction: $message. Needs to update speech bubble UI in MainActivity.")
        // This method would ideally trigger an event/callback to MainActivity to show the speech bubble
    }

    private fun giveStarDust(amount: Int) {
        Log.d("YumiCompanion", "giveStarDust: $amount. Needs to interact with AchievementManager.")
        // TODO: This should interact with an AchievementManager or similar system
    }

    private fun getDailyMessage(): String {
        Log.d("YumiCompanion", "getDailyMessage. Affection: $affectionLevel")
        // TODO: Implement varied daily messages based on affection level or other factors
        return "Welcome back! I missed you! 💕 (Affection: $affectionLevel)"
    }

    private fun updateAnimation() {
        Log.d("YumiCompanion", "updateAnimation to mood $currentMood. Needs actual Lottie files.")
        mascotView.setAnimation(getAnimationForMood(currentMood))
        mascotView.playAnimation()
    }
}

/**
 * Placeholder for ParticleSystem.
 * Actual particle effect implementation is needed.
 */
object ParticleSystem {
    fun createMagicalExplosion(targetView: View) {
        Log.d("ParticleSystem", "createMagicalExplosion called on view. Needs implementation.")
        // TODO: Implement particle effect (e.g., using a library or custom drawing)
    }
}
