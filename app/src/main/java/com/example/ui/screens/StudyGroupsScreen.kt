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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Timer
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
import com.example.data.model.GroupMessage
import com.example.data.model.StudyGroup
import com.example.data.model.UserCategory
import com.example.ui.components.CategoryBadge
import com.example.ui.theme.CollegeTeal
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.OmniSphereViewModel

@Composable
fun StudyGroupsScreen(
    viewModel: OmniSphereViewModel,
    modifier: Modifier = Modifier
) {
    val studyGroups by viewModel.studyGroups.collectAsState()
    val activeGroup by viewModel.activeStudyGroup.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf<UserCategory?>(null) }

    val filteredGroups = remember(studyGroups, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) studyGroups
        else studyGroups.filter { it.targetAudiences.contains(selectedCategoryFilter) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("study_groups_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Collaborative Study Groups",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Cross-level cohorts for peer tutoring, shared resource vaults & sprint sessions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Filter
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All Cohorts") }
                    )
                }
                items(UserCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = {
                            selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                        },
                        label = { Text(cat.label) }
                    )
                }
            }
        }

        // Study Groups List
        items(filteredGroups, key = { it.id }) { group ->
            StudyGroupCard(
                group = group,
                onOpen = { viewModel.openStudyGroup(group) },
                onToggleJoin = { viewModel.toggleJoinGroup(group.id) }
            )
        }
    }

    // Active Study Group Hub Dialog
    if (activeGroup != null) {
        StudyGroupHubDialog(
            group = activeGroup!!,
            viewModel = viewModel,
            onDismiss = { viewModel.openStudyGroup(null) }
        )
    }
}

@Composable
fun StudyGroupCard(
    group: StudyGroup,
    onOpen: () -> Unit,
    onToggleJoin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("group_card_${group.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Target Audiences row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    group.targetAudiences.forEach { cat ->
                        CategoryBadge(category = cat, compact = true)
                    }
                }

                Surface(
                    color = if (group.isJoined) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (group.isJoined) "✓ Joined Member" else "Open Cohort",
                        color = if (group.isJoined) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = group.subjectField,
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryIndigo,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sprint topic card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "Active Sprint",
                        tint = CollegeTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active Sprint: ${group.activeSprintTopic}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats and Action Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "👥 ${group.memberCount} members",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "📚 ${group.resourcesCount} resources",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onToggleJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (group.isJoined) MaterialTheme.colorScheme.surfaceVariant
                        else PrimaryIndigo,
                        contentColor = if (group.isJoined) MaterialTheme.colorScheme.onSurfaceVariant
                        else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (group.isJoined) "Leave" else "Join Cohort", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGroupHubDialog(
    group: StudyGroup,
    viewModel: OmniSphereViewModel,
    onDismiss: () -> Unit
) {
    val messagesMap by viewModel.groupMessages.collectAsState()
    val messages = messagesMap[group.id] ?: emptyList()
    val timerState by viewModel.sprintTimer.collectAsState()

    var messageText by remember { mutableStateOf("") }
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Chat, 1: Focus Timer, 2: Resources

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("group_hub_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${group.memberCount} Active Collaborators • ${group.subjectField}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Sub-Tabs: Discussion Chat | Focus Sprint Timer | Shared Vault
                TabRow(selectedTabIndex = selectedSubTab) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = { Text("Group Chat") },
                        icon = { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = { Text("Study Sprint") },
                        icon = { Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { selectedSubTab = 2 },
                        text = { Text("Resources (${group.resourcesCount})") },
                        icon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                // Tab Content
                when (selectedSubTab) {
                    0 -> {
                        // Discussion Chat
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (msg.isResourceLink) Color(0xFFE8EAF6)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = msg.senderName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                CategoryBadge(category = msg.senderCategory, compact = true)
                                            }
                                            if (msg.isResourceLink) {
                                                Text(
                                                    text = "📁 Resource Vault Link",
                                                    fontSize = 10.sp,
                                                    color = PrimaryIndigo,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.message,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Message Input
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    placeholder = { Text("Collaborate or share study link...") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("group_chat_input"),
                                    shape = RoundedCornerShape(20.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (messageText.isNotBlank()) {
                                            viewModel.sendGroupMessage(group.id, messageText, isResource = messageText.contains("http") || messageText.contains("guide") || messageText.contains("notes"))
                                            messageText = ""
                                        }
                                    },
                                    modifier = Modifier.testTag("send_group_chat_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = PrimaryIndigo)
                                }
                            }
                        }
                    }

                    1 -> {
                        // Study Sprint Focus Timer (Pomodoro)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Surface(
                                color = PrimaryIndigo.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "COLLABORATIVE SPRINT TOPIC",
                                    fontSize = 11.sp,
                                    color = PrimaryIndigo,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = group.activeSprintTopic,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Large Timer Display
                            val minutes = timerState.remainingSeconds / 60
                            val seconds = timerState.remainingSeconds % 60
                            val timeStr = "%02d:%02d".format(minutes, seconds)

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = timeStr,
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (timerState.isRunning) CollegeTeal else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (timerState.isRunning) "Focusing together..." else "Ready to sprint",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(
                                    onClick = { viewModel.toggleSprintTimer() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (timerState.isRunning) Color(0xFFE53935) else CollegeTeal
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (timerState.isRunning) "Pause Sprint" else "Start Sprint")
                                }

                                OutlinedButton(onClick = { viewModel.resetSprintTimer(25) }) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reset (25m)")
                                }
                            }
                        }
                    }

                    2 -> {
                        // Shared Resource Vault
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "SHARED NOTES & CHEATSHEETS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            val sampleResources = listOf(
                                "📘 Comprehensive Roadmap & Formula Cheatsheet.pdf",
                                "🔗 Curated Open-Source GitHub Repositories for Beginners",
                                "📝 Mock Technical & Admission Interview Rubric",
                                "💡 Sensor Calibration & Noise Filtering Code Notebook"
                            )
                            items(sampleResources) { itemTitle ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.BookmarkBorder,
                                            contentDescription = null,
                                            tint = PrimaryIndigo
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = itemTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {}) {
                                            Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
