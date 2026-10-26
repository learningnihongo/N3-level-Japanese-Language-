package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.quiz.QuizGameplayContent
import com.example.ui.screens.quiz.QuizHubContent
import com.example.ui.screens.quiz.QuizResultContent
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.VocabViewModel

@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel,
    vocabViewModel: VocabViewModel? = null,
    onBackClick: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onQuizFinished: (score: Long, total: Long, xpEarned: Long) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val quizState by quizViewModel.quizState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            quizState.isQuizActive -> {
                QuizGameplayContent(
                    quizState = quizState,
                    quizViewModel = quizViewModel
                )
            }
            quizState.isFinished -> {
                QuizResultContent(
                    quizState = quizState,
                    quizViewModel = quizViewModel,
                    onBackToHub = {
                        quizViewModel.exitQuiz()
                    },
                    onNavigateToHistory = onNavigateToHistory
                )
            }
            else -> {
                QuizHubContent(
                    quizViewModel = quizViewModel,
                    vocabViewModel = vocabViewModel,
                    onNavigateToHistory = onNavigateToHistory
                )
            }
        }
    }
}
