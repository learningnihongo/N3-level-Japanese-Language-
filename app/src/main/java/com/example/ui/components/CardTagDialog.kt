package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VocabCard

val DEFAULT_SUGGESTED_TAGS = listOf(
    "JLPT N3",
    "မေးခွန်းဟောင်း",
    "ဆင်တူရိုးမှား",
    "Grammar",
    "Work",
    "Daily",
    "Weak",
    "Exam"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardTagDialog(
    card: VocabCard,
    availableTags: List<String>,
    onDismiss: () -> Unit,
    onSaveTags: (cardId: Long, updatedTags: List<String>) -> Unit
) {
    val currentTags = remember(card) {
        mutableStateListOf<String>().apply { addAll(card.tagList) }
    }
    var newTagInput by remember { mutableStateOf("") }

    val allSuggested = remember(availableTags) {
        (DEFAULT_SUGGESTED_TAGS + availableTags).distinct()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("card_tag_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Manage Tags: ${card.kanji}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Add custom labels or categories to organize this word.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                // Current Tags Section
                Text(
                    text = "Active Tags:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (currentTags.isEmpty()) {
                    Text(
                        text = "No tags added yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentTags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { currentTags.remove(tag) },
                                label = { Text("#$tag") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove tag"
                                    )
                                }
                            )
                        }
                    }
                }

                // Add Custom Tag Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTagInput,
                        onValueChange = { newTagInput = it },
                        placeholder = { Text("New tag name...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_new_tag")
                    )

                    IconButton(
                        onClick = {
                            val trimmed = newTagInput.trim()
                            if (trimmed.isNotEmpty() && !currentTags.contains(trimmed)) {
                                currentTags.add(trimmed)
                                newTagInput = ""
                            }
                        },
                        enabled = newTagInput.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add tag")
                    }
                }

                // Suggested Tags
                Text(
                    text = "Suggested Tags:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allSuggested.forEach { tag ->
                        val isAdded = currentTags.contains(tag)
                        FilterChip(
                            selected = isAdded,
                            onClick = {
                                if (isAdded) {
                                    currentTags.remove(tag)
                                } else {
                                    currentTags.add(tag)
                                }
                            },
                            label = { Text("#$tag") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSaveTags(card.id, currentTags.toList())
                        },
                        modifier = Modifier.testTag("save_tags_btn")
                    ) {
                        Text("Save Tags")
                    }
                }
            }
        }
    }
}
