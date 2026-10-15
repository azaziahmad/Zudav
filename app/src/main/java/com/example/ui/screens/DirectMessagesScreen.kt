package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DirectConversation
import com.example.data.model.DirectMessage
import com.example.ui.components.CategoryBadge
import com.example.ui.theme.GuardianRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.OmniSphereViewModel

@Composable
fun DirectMessagesScreen(
    viewModel: OmniSphereViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()
    val safetySettings by viewModel.safetySettings.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("direct_messages_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Direct Messages & Mentorship",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "1-on-1 private conversations protected by Guardian Mode shielding.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Safety Status Banner
        if (safetySettings.guardianMode) {
            item {
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
                            imageVector = Icons.Outlined.Security,
                            contentDescription = "Safe DMs",
                            tint = GuardianRose,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Guardian Mode Active: Unsolicited contacts are screened. Toxic phrases are redacted.",
                            fontSize = 12.sp,
                            color = GuardianRose,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Conversations List
        items(conversations, key = { it.id }) { conv ->
            ConversationRow(
                conversation = conv,
                onClick = { viewModel.openConversation(conv) }
            )
        }
    }

    // Active Chat Dialog
    if (activeConversation != null) {
        ActiveChatDialog(
            conversation = activeConversation!!,
            viewModel = viewModel,
            onDismiss = { viewModel.openConversation(null) }
        )
    }
}

@Composable
fun ConversationRow(
    conversation: DirectConversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conversation_row_${conversation.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (conversation.isBlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo)
            ) {
                Text(
                    text = conversation.peerName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.peerName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (conversation.isVerifiedMentor) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified Mentor",
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (conversation.isBlocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "[Blocked]",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = conversation.peerRole,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                CategoryBadge(category = conversation.peerCategory, compact = true)
                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Badge {
                        Text("${conversation.unreadCount}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveChatDialog(
    conversation: DirectConversation,
    viewModel: OmniSphereViewModel,
    onDismiss: () -> Unit
) {
    val messagesMap by viewModel.directMessages.collectAsState()
    val messages = messagesMap[conversation.id] ?: emptyList()
    var inputMessage by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "Can you share tips for balancing studies?",
        "What skills should I build for my portfolio?",
        "Any advice for dealing with exam stress?"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("active_chat_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header with Peer Name & Moderation Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo)
                        ) {
                            Text(
                                text = conversation.peerName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conversation.peerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                CategoryBadge(category = conversation.peerCategory, compact = true)
                            }
                            Text(
                                text = conversation.peerRole,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Moderation tools: Block & Anonymous Report
                    Row {
                        IconButton(
                            onClick = {
                                viewModel.openAnonymousReport(true, reportedUser = conversation.peerName)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Flag,
                                contentDescription = "Report User",
                                tint = GuardianRose
                            )
                        }
                        IconButton(onClick = { viewModel.toggleBlockUser(conversation.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.Block,
                                contentDescription = "Block / Unblock",
                                tint = if (conversation.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                HorizontalDivider()

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        Row(
                            horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isFromMe) 16.dp else 2.dp,
                                    bottomEnd = if (msg.isFromMe) 2.dp else 16.dp
                                ),
                                color = if (msg.isFromMe) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = if (msg.isFromMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Quick Prompt Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickQuestions) { question ->
                        SuggestionChip(
                            onClick = {
                                viewModel.sendDirectMessage(conversation.id, question)
                            },
                            label = { Text(question, fontSize = 11.sp) }
                        )
                    }
                }

                // Chat Input
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = { Text("Write a message...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dm_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            enabled = !conversation.isBlocked
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilledIconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.sendDirectMessage(conversation.id, inputMessage)
                                    inputMessage = ""
                                }
                            },
                            enabled = inputMessage.isNotBlank() && !conversation.isBlocked,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryIndigo),
                            modifier = Modifier.testTag("send_dm_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }
    }
}
