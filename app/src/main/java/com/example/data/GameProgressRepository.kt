package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "kheli_shikhi_progress")

class GameProgressRepository(private val context: Context) {

    // Core Mode & Extended Mode Separate Keys (Never mixed!)
    private fun keyCoreLevel(gameId: String) = intPreferencesKey("${gameId}_level")
    private fun keyCoreCompletedLevels(gameId: String) = stringSetPreferencesKey("${gameId}_core_completed_levels")
    private fun keyExtendedLevel(gameId: String) = intPreferencesKey("${gameId}_ext_level")
    private fun keyExtendedCompletedLevels(gameId: String) = stringSetPreferencesKey("${gameId}_completed_levels")

    private fun keyStars(gameId: String) = intPreferencesKey("${gameId}_stars")
    private fun keyPlayed(gameId: String) = intPreferencesKey("${gameId}_played")
    private fun keySuccess(gameId: String) = intPreferencesKey("${gameId}_success")
    private fun keyHints(gameId: String) = intPreferencesKey("${gameId}_hints")
    private fun keyPhysical(gameId: String) = booleanPreferencesKey("${gameId}_physical")

    // Mode, Continue Learning & Audio Settings Keys
    private val keyLearningMode = stringPreferencesKey("active_learning_mode")
    private val keyLastPlayedGame = stringPreferencesKey("last_played_game_id")
    private val keyVoiceEnabled = booleanPreferencesKey("audio_voice_enabled")
    private val keySfxEnabled = booleanPreferencesKey("audio_sfx_enabled")
    private val keyBgmEnabled = booleanPreferencesKey("audio_bgm_enabled")
    private val keyReducedMotion = booleanPreferencesKey("ui_reduced_motion")

    // Skill Mastery Keys
    private fun keyColorMastery(colorId: String) = intPreferencesKey("skill_color_$colorId")
    private fun keyShapeMastery(shapeId: String) = intPreferencesKey("skill_shape_$shapeId")

    val progressFlow: Flow<Map<GameType, GameProgressData>> = context.dataStore.data.map { prefs ->
        GameType.entries.associateWith { type ->
            val coreLvl = (prefs[keyCoreLevel(type.id)] ?: 1).coerceIn(1, 5)
            val extLvl = (prefs[keyExtendedLevel(type.id)] ?: 1).coerceIn(1, 25)
            val coreCompSet = (prefs[keyCoreCompletedLevels(type.id)] ?: emptySet())
                .mapNotNull { it.toIntOrNull() }.toSet()
            val extCompSet = (prefs[keyExtendedCompletedLevels(type.id)] ?: emptySet())
                .mapNotNull { it.toIntOrNull() }.toSet()

            GameProgressData(
                gameType = type,
                unlockedLevel = coreLvl,
                coreCompletedLevels = coreCompSet,
                extendedUnlockedLevel = extLvl,
                completedLevels = extCompSet,
                starsEarned = prefs[keyStars(type.id)] ?: 0,
                totalPlayed = prefs[keyPlayed(type.id)] ?: 0,
                independentSuccesses = prefs[keySuccess(type.id)] ?: 0,
                hintsUsed = prefs[keyHints(type.id)] ?: 0,
                physicalCompleted = prefs[keyPhysical(type.id)] ?: false
            )
        }
    }

    val learningModeFlow: Flow<LearningMode> = context.dataStore.data.map { prefs ->
        val id = prefs[keyLearningMode] ?: LearningMode.CORE_CURRICULUM.id
        LearningMode.entries.firstOrNull { it.id == id } ?: LearningMode.CORE_CURRICULUM
    }

    val lastPlayedGameFlow: Flow<GameType> = context.dataStore.data.map { prefs ->
        val id = prefs[keyLastPlayedGame] ?: GameType.EMOJI_MATCH.id
        GameType.entries.firstOrNull { it.id == id } ?: GameType.EMOJI_MATCH
    }

    val audioSettingsFlow: Flow<AudioSettingsState> = context.dataStore.data.map { prefs ->
        AudioSettingsState(
            voiceEnabled = prefs[keyVoiceEnabled] ?: true,
            soundEffectsEnabled = prefs[keySfxEnabled] ?: true,
            backgroundMusicEnabled = prefs[keyBgmEnabled] ?: false,
            reducedMotion = prefs[keyReducedMotion] ?: false
        )
    }

    val skillMasteryFlow: Flow<SkillMasterySnapshot> = context.dataStore.data.map { prefs ->
        val colors = KidColor.entries.associate { c ->
            c.id to (prefs[keyColorMastery(c.id)] ?: 0)
        }
        val shapes = KidShape.entries.associate { s ->
            s.id to (prefs[keyShapeMastery(s.id)] ?: 0)
        }
        SkillMasterySnapshot(
            colorAccuracyMap = colors,
            shapeAccuracyMap = shapes
        )
    }

    suspend fun setLearningMode(mode: LearningMode) {
        context.dataStore.edit { prefs ->
            prefs[keyLearningMode] = mode.id
        }
    }

    suspend fun setLastPlayedGame(gameType: GameType) {
        context.dataStore.edit { prefs ->
            prefs[keyLastPlayedGame] = gameType.id
        }
    }

    suspend fun updateAudioSettings(settings: AudioSettingsState) {
        context.dataStore.edit { prefs ->
            prefs[keyVoiceEnabled] = settings.voiceEnabled
            prefs[keySfxEnabled] = settings.soundEffectsEnabled
            prefs[keyBgmEnabled] = settings.backgroundMusicEnabled
            prefs[keyReducedMotion] = settings.reducedMotion
        }
    }

    /**
     * Records level completion and strictly unlocks only the next sequential level
     * while preserving all previously unlocked/completed levels for replay.
     * Core Mode and Extended Mode progress are saved separately.
     */
    suspend fun recordAttempt(
        gameType: GameType,
        isSuccess: Boolean,
        isIndependent: Boolean,
        shouldAdvanceLevel: Boolean,
        completedLevelNumber: Int = 1,
        learningMode: LearningMode = LearningMode.CORE_CURRICULUM
    ) {
        context.dataStore.edit { prefs ->
            prefs[keyLastPlayedGame] = gameType.id
            val playedKey = keyPlayed(gameType.id)
            prefs[playedKey] = (prefs[playedKey] ?: 0) + 1

            if (isSuccess) {
                val starsKey = keyStars(gameType.id)
                val earnedBonus = if (isIndependent) 2 else 1
                prefs[starsKey] = (prefs[starsKey] ?: 0) + earnedBonus

                if (isIndependent) {
                    val succKey = keySuccess(gameType.id)
                    prefs[succKey] = (prefs[succKey] ?: 0) + 1
                }

                if (learningMode == LearningMode.CORE_CURRICULUM) {
                    val compKey = keyCoreCompletedLevels(gameType.id)
                    val currentComp = prefs[compKey] ?: emptySet()
                    prefs[compKey] = currentComp + completedLevelNumber.toString()

                    if (shouldAdvanceLevel) {
                        val levelKey = keyCoreLevel(gameType.id)
                        val currentCore = prefs[levelKey] ?: 1
                        val nextCore = (completedLevelNumber + 1).coerceAtMost(5)
                        if (nextCore > currentCore) {
                            prefs[levelKey] = nextCore
                        }
                    }
                } else {
                    val compKey = keyExtendedCompletedLevels(gameType.id)
                    val currentComp = prefs[compKey] ?: emptySet()
                    prefs[compKey] = currentComp + completedLevelNumber.toString()

                    if (shouldAdvanceLevel) {
                        val extKey = keyExtendedLevel(gameType.id)
                        val currentExt = prefs[extKey] ?: 1
                        val nextExt = (completedLevelNumber + 1).coerceAtMost(25)
                        if (nextExt > currentExt) {
                            prefs[extKey] = nextExt
                        }
                    }
                }
            }
        }
    }

    suspend fun recordSkillMastery(colorId: String? = null, shapeId: String? = null) {
        context.dataStore.edit { prefs ->
            if (colorId != null) {
                val k = keyColorMastery(colorId)
                prefs[k] = (prefs[k] ?: 0) + 1
            }
            if (shapeId != null) {
                val k = keyShapeMastery(shapeId)
                prefs[k] = (prefs[k] ?: 0) + 1
            }
        }
    }

    suspend fun recordHintUsed(gameType: GameType) {
        context.dataStore.edit { prefs ->
            val hintsKey = keyHints(gameType.id)
            prefs[hintsKey] = (prefs[hintsKey] ?: 0) + 1
        }
    }

    suspend fun setPhysicalPracticeCompleted(gameType: GameType, completed: Boolean) {
        context.dataStore.edit { prefs ->
            val physicalKey = keyPhysical(gameType.id)
            prefs[physicalKey] = completed
        }
    }

    suspend fun unlockAllLevelsForAllGames() {
        context.dataStore.edit { prefs ->
            GameType.entries.forEach { g ->
                prefs[keyCoreLevel(g.id)] = 5
                prefs[keyExtendedLevel(g.id)] = 25
            }
        }
    }

    suspend fun resetAllProgress() {
        context.dataStore.edit { prefs ->
            val voice = prefs[keyVoiceEnabled] ?: true
            val sfx = prefs[keySfxEnabled] ?: true
            val bgm = prefs[keyBgmEnabled] ?: false
            val reduced = prefs[keyReducedMotion] ?: false
            prefs.clear()
            prefs[keyVoiceEnabled] = voice
            prefs[keySfxEnabled] = sfx
            prefs[keyBgmEnabled] = bgm
            prefs[keyReducedMotion] = reduced
        }
    }
}
