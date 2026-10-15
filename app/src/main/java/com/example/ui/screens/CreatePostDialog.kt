package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PostType
import com.example.data.model.UserCategory
import com.example.ui.components.CategoryBadge
import com.example.ui.theme.GuardianRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.OmniSphereViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    viewModel: OmniSphereViewModel,
    onDismiss: () -> Unit
) {
    val currentProfile by viewModel.currentProfile.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(currentProfile.category) }
    var fieldOfKnowledge by remember { mutableStateOf(currentProfile.fieldOfKnowledge) }
    var selectedType by remember { mutableStateOf(PostType.KNOWLEDGE_BYTE) }
    var tagsInput by remember { mutableStateOf("Tech, Learning, Career") }
    var isAnonymousSafe by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .testTag("create_post_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Share Field of Knowledge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Earn +20 Reputation XP upon sharing",
                            fontSize = 12.sp,
                            color = Color(0xFFF57F17),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category Selection
                    Text(
                        text = "YOUR COMMUNITY PERSPECTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserCategory.values().forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedCategory == cat) PrimaryIndigo.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (selectedCategory == cat) ButtonDefaults.outlinedButtonBorder(true) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCategory = cat }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    CategoryBadge(category = cat, compact = true)
                                }
                            }
                        }
                    }

                    // Field of Knowledge
                    OutlinedTextField(
                        value = fieldOfKnowledge,
                        onValueChange = { fieldOfKnowledge = it },
                        label = { Text("Field / Discipline / Subject") },
                        placeholder = { Text("e.g. Computer Science, Medicine, AP Physics, UI/UX") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Post Type Chips
                    Text(
                        text = "POST TYPE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PostType.values().forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.title, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Post Title") },
                        placeholder = { Text("Summarize your knowledge byte or career experience...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_title_input"),
                        singleLine = false,
                        maxLines = 2
                    )

                    // Content
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Insights & Realities") },
                        placeholder = { Text("Share practical steps, lessons learned, concepts, or ask questions for guidance...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("post_content_input"),
                        maxLines = 8
                    )

                    // Tags
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Safety Shield Mode for Posting
                    Surface(
                        color = GuardianRose.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Safety Shield",
                                tint = GuardianRose,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Safe Anonymous Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GuardianRose
                                )
                                Text(
                                    text = "Post without showing your profile name or identity. Safeguards against unsolicited DMs.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = isAnonymousSafe,
                                onCheckedChange = { isAnonymousSafe = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GuardianRose,
                                    checkedTrackColor = GuardianRose.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Actions: Cancel & Submit
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                viewModel.createPost(
                                    title = title,
                                    content = content,
                                    category = selectedCategory,
                                    field = fieldOfKnowledge.ifBlank { "General Field" },
                                    tags = if (tags.isEmpty()) listOf("Knowledge") else tags,
                                    postType = selectedType,
                                    isAnonymous = isAnonymousSafe
                                )
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.testTag("publish_post_button")
                    ) {
                        Text("Publish & Earn +20 XP")
                    }
                }
            }
        }
    }
}
