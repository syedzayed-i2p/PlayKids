package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.BanglaAudioEngine
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.AppBackground
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: BanglaAudioEngine
    private lateinit var repository: GameProgressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioEngine = BanglaAudioEngine(this)
        repository = GameProgressRepository(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    val progressMap by repository.progressFlow
                        .collectAsStateWithLifecycle(
                            initialValue = GameType.entries.associateWith { GameProgressData(it) }
                        )

                    val learningMode by repository.learningModeFlow
                        .collectAsStateWithLifecycle(initialValue = LearningMode.CORE_CURRICULUM)

                    val lastPlayedGame by repository.lastPlayedGameFlow
                        .collectAsStateWithLifecycle(initialValue = GameType.EMOJI_MATCH)

                    val audioSettings by repository.audioSettingsFlow
                        .collectAsStateWithLifecycle(initialValue = AudioSettingsState())

                    val skillMastery by repository.skillMasteryFlow
                        .collectAsStateWithLifecycle(initialValue = SkillMasterySnapshot())

                    LaunchedEffect(audioSettings) {
                        audioEngine.updateAudioSettings(audioSettings)
                    }

                    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
                    var activeGameForMap by remember { mutableStateOf(GameType.EMOJI_MATCH) }
                    var selectedLevelOverride by remember { mutableStateOf<Int?>(null) }
                    var physicalInitialGame by remember { mutableStateOf(GameType.EMOJI_MATCH) }
                    val coroutineScope = rememberCoroutineScope()

                    fun navigateToGame(gameType: GameType, levelOverride: Int? = null) {
                        audioEngine.stopAndClearQueue()
                        selectedLevelOverride = levelOverride
                        coroutineScope.launch { repository.setLastPlayedGame(gameType) }
                        currentScreen = when (gameType) {
                            GameType.EMOJI_MATCH -> Screen.GAME_EMOJI
                            GameType.SHAPE_MATCH -> Screen.GAME_SHAPE
                            GameType.COLOR_SHAPE_CHALLENGE -> Screen.GAME_COLOR_SHAPE
                            GameType.COLOR_PAPER_MATCH -> Screen.GAME_COLOR_PAPER
                        }
                    }

                    fun handleBottomTabNavigation(target: Screen) {
                        audioEngine.stopAndClearQueue()
                        currentScreen = target
                    }

                    when (currentScreen) {
                        Screen.SPLASH -> {
                            PlayoraSplashScreen(
                                reducedMotion = audioSettings.reducedMotion,
                                onSplashFinished = {
                                    currentScreen = Screen.WELCOME
                                }
                            )
                        }

                        Screen.WELCOME -> {
                            BackHandler {
                                audioEngine.stopAndClearQueue()
                                currentScreen = Screen.HOME
                            }
                            PlayoraWelcomeScreen(
                                audio = audioEngine,
                                reducedMotion = audioSettings.reducedMotion,
                                onStartExploring = {
                                    currentScreen = Screen.HOME
                                }
                            )
                        }

                        Screen.HOME -> {
                            HomeScreen(
                                allProgress = progressMap,
                                learningMode = learningMode,
                                lastPlayedGame = lastPlayedGame,
                                audio = audioEngine,
                                onSelectMode = { mode ->
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onSelectGame = { gameType ->
                                    navigateToGame(gameType, null)
                                },
                                onContinueLearning = { gameType, resumeLevel ->
                                    navigateToGame(gameType, resumeLevel)
                                },
                                onOpenLevelMap = { gameType ->
                                    audioEngine.stopAndClearQueue()
                                    activeGameForMap = gameType
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onOpenMyProgress = {
                                    audioEngine.stopAndClearQueue()
                                    currentScreen = Screen.MY_PROGRESS
                                },
                                onOpenPhysicalPractice = {
                                    audioEngine.stopAndClearQueue()
                                    physicalInitialGame = GameType.EMOJI_MATCH
                                    currentScreen = Screen.PHYSICAL_PRACTICE
                                },
                                onOpenParentDashboard = {
                                    audioEngine.stopAndClearQueue()
                                    currentScreen = Screen.PARENT_DASHBOARD
                                },
                                onToggleVoiceMute = {
                                    val updated = audioSettings.copy(voiceEnabled = !audioSettings.voiceEnabled)
                                    audioEngine.updateAudioSettings(updated)
                                    coroutineScope.launch { repository.updateAudioSettings(updated) }
                                },
                                onUpdateAudioSettings = { newSettings ->
                                    coroutineScope.launch { repository.updateAudioSettings(newSettings) }
                                }
                            )
                        }

                        Screen.MY_PROGRESS -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            MyProgressScreen(
                                allProgress = progressMap,
                                learningMode = learningMode,
                                audio = audioEngine,
                                onSelectGame = { gameType ->
                                    navigateToGame(gameType, null)
                                },
                                onOpenLevelMap = { gameType ->
                                    activeGameForMap = gameType
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onNavigateTab = { target ->
                                    handleBottomTabNavigation(target)
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                }
                            )
                        }

                        Screen.LEVEL_MAP -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            LevelSelectionScreen(
                                gameType = activeGameForMap,
                                progress = progressMap[activeGameForMap] ?: GameProgressData(activeGameForMap),
                                learningMode = learningMode,
                                audio = audioEngine,
                                onSelectMode = { mode ->
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onSelectLevel = { chosenLevel ->
                                    navigateToGame(activeGameForMap, chosenLevel)
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                }
                            )
                        }

                        Screen.GAME_EMOJI -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            EmojiMatchingScreen(
                                audio = audioEngine,
                                progress = progressMap[GameType.EMOJI_MATCH] ?: GameProgressData(GameType.EMOJI_MATCH),
                                learningMode = learningMode,
                                initialLevelOverride = selectedLevelOverride,
                                onSelectMode = { mode ->
                                    selectedLevelOverride = null
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onOpenLevelMap = {
                                    activeGameForMap = GameType.EMOJI_MATCH
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onRecordAttempt = { isSuccess, isIndependent, shouldAdvance, completedLvl ->
                                    coroutineScope.launch {
                                        repository.recordAttempt(
                                            GameType.EMOJI_MATCH,
                                            isSuccess,
                                            isIndependent,
                                            shouldAdvance,
                                            completedLvl,
                                            learningMode
                                        )
                                    }
                                },
                                onRecordHint = {
                                    coroutineScope.launch {
                                        repository.recordHintUsed(GameType.EMOJI_MATCH)
                                    }
                                },
                                onGoPhysicalPractice = {
                                    audioEngine.stop()
                                    physicalInitialGame = GameType.EMOJI_MATCH
                                    currentScreen = Screen.PHYSICAL_PRACTICE
                                }
                            )
                        }

                        Screen.GAME_SHAPE -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            ShapeMatchingScreen(
                                audio = audioEngine,
                                progress = progressMap[GameType.SHAPE_MATCH] ?: GameProgressData(GameType.SHAPE_MATCH),
                                learningMode = learningMode,
                                initialLevelOverride = selectedLevelOverride,
                                onSelectMode = { mode ->
                                    selectedLevelOverride = null
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onOpenLevelMap = {
                                    activeGameForMap = GameType.SHAPE_MATCH
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onRecordAttempt = { isSuccess, isIndependent, shouldAdvance, completedLvl ->
                                    coroutineScope.launch {
                                        repository.recordAttempt(
                                            GameType.SHAPE_MATCH,
                                            isSuccess,
                                            isIndependent,
                                            shouldAdvance,
                                            completedLvl,
                                            learningMode
                                        )
                                    }
                                },
                                onRecordSkillMastery = { shapeId ->
                                    coroutineScope.launch {
                                        repository.recordSkillMastery(shapeId = shapeId)
                                    }
                                },
                                onRecordHint = {
                                    coroutineScope.launch {
                                        repository.recordHintUsed(GameType.SHAPE_MATCH)
                                    }
                                },
                                onGoPhysicalPractice = {
                                    audioEngine.stop()
                                    physicalInitialGame = GameType.SHAPE_MATCH
                                    currentScreen = Screen.PHYSICAL_PRACTICE
                                }
                            )
                        }

                        Screen.GAME_COLOR_SHAPE -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            ColorShapeChallengeScreen(
                                audio = audioEngine,
                                progress = progressMap[GameType.COLOR_SHAPE_CHALLENGE] ?: GameProgressData(GameType.COLOR_SHAPE_CHALLENGE),
                                learningMode = learningMode,
                                initialLevelOverride = selectedLevelOverride,
                                onSelectMode = { mode ->
                                    selectedLevelOverride = null
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onOpenLevelMap = {
                                    activeGameForMap = GameType.COLOR_SHAPE_CHALLENGE
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onRecordAttempt = { isSuccess, isIndependent, shouldAdvance, completedLvl ->
                                    coroutineScope.launch {
                                        repository.recordAttempt(
                                            GameType.COLOR_SHAPE_CHALLENGE,
                                            isSuccess,
                                            isIndependent,
                                            shouldAdvance,
                                            completedLvl,
                                            learningMode
                                        )
                                    }
                                },
                                onRecordSkillMastery = { colorId, shapeId ->
                                    coroutineScope.launch {
                                        repository.recordSkillMastery(colorId = colorId, shapeId = shapeId)
                                    }
                                },
                                onRecordHint = {
                                    coroutineScope.launch {
                                        repository.recordHintUsed(GameType.COLOR_SHAPE_CHALLENGE)
                                    }
                                },
                                onGoPhysicalPractice = {
                                    audioEngine.stop()
                                    physicalInitialGame = GameType.COLOR_SHAPE_CHALLENGE
                                    currentScreen = Screen.PHYSICAL_PRACTICE
                                }
                            )
                        }

                        Screen.GAME_COLOR_PAPER -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            ColorPaperMatchingScreen(
                                audio = audioEngine,
                                progress = progressMap[GameType.COLOR_PAPER_MATCH] ?: GameProgressData(GameType.COLOR_PAPER_MATCH),
                                learningMode = learningMode,
                                initialLevelOverride = selectedLevelOverride,
                                onSelectMode = { mode ->
                                    selectedLevelOverride = null
                                    coroutineScope.launch { repository.setLearningMode(mode) }
                                },
                                onOpenLevelMap = {
                                    activeGameForMap = GameType.COLOR_PAPER_MATCH
                                    currentScreen = Screen.LEVEL_MAP
                                },
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onRecordAttempt = { isSuccess, isIndependent, shouldAdvance, completedLvl ->
                                    coroutineScope.launch {
                                        repository.recordAttempt(
                                            GameType.COLOR_PAPER_MATCH,
                                            isSuccess,
                                            isIndependent,
                                            shouldAdvance,
                                            completedLvl,
                                            learningMode
                                        )
                                    }
                                },
                                onRecordSkillMastery = { colorId ->
                                    coroutineScope.launch {
                                        repository.recordSkillMastery(colorId = colorId)
                                    }
                                },
                                onRecordHint = {
                                    coroutineScope.launch {
                                        repository.recordHintUsed(GameType.COLOR_PAPER_MATCH)
                                    }
                                },
                                onGoPhysicalPractice = {
                                    audioEngine.stop()
                                    physicalInitialGame = GameType.COLOR_PAPER_MATCH
                                    currentScreen = Screen.PHYSICAL_PRACTICE
                                }
                            )
                        }

                        Screen.PHYSICAL_PRACTICE -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            PhysicalPracticeScreen(
                                initialGameType = physicalInitialGame,
                                allProgress = progressMap,
                                audio = audioEngine,
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onTogglePhysicalDone = { gType, done ->
                                    coroutineScope.launch {
                                        repository.setPhysicalPracticeCompleted(gType, done)
                                    }
                                },
                                onNavigateTab = { target ->
                                    handleBottomTabNavigation(target)
                                }
                            )
                        }

                        Screen.PARENT_DASHBOARD -> {
                            BackHandler {
                                audioEngine.stop()
                                currentScreen = Screen.HOME
                            }
                            ParentDashboardScreen(
                                allProgress = progressMap,
                                skillMastery = skillMastery,
                                audio = audioEngine,
                                onBack = {
                                    audioEngine.stop()
                                    currentScreen = Screen.HOME
                                },
                                onUpdateAudioSettings = { newSettings ->
                                    coroutineScope.launch {
                                        repository.updateAudioSettings(newSettings)
                                    }
                                },
                                onUnlockAllLevels = {
                                    coroutineScope.launch {
                                        repository.unlockAllLevelsForAllGames()
                                    }
                                },
                                onResetAllProgress = {
                                    coroutineScope.launch {
                                        repository.resetAllProgress()
                                    }
                                },
                                onNavigateTab = { target ->
                                    handleBottomTabNavigation(target)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::audioEngine.isInitialized) {
            audioEngine.stopAndClearQueue()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::audioEngine.isInitialized) {
            audioEngine.stopAndClearQueue()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioEngine.isInitialized) {
            audioEngine.release()
        }
    }
}
