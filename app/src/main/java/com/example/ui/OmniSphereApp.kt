package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PersonaSwitchDialog
import com.example.ui.components.ReputationPill
import com.example.ui.screens.*
import com.example.ui.theme.GuardianRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.OmniSphereViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniSphereApp(
    viewModel: OmniSphereViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val safetySettings by viewModel.safetySettings.collectAsState()
    val selectedPost by viewModel.selectedPost.collectAsState()
    val showCreatePost by viewModel.showCreatePostDialog.collectAsState()
    val activeGroup by viewModel.activeStudyGroup.collectAsState()
    val activeConversation by viewModel.activeConversation.collectAsState()

    var showPersonaDialog by remember { mutableStateOf(false) }

    // Predictive Back Handling
    BackHandler(
        enabled = selectedPost != null || activeGroup != null || activeConversation != null || currentTab != MainNavigationTab.FEED
    ) {
        when {
            selectedPost != null -> viewModel.selectPost(null)
            activeGroup != null -> viewModel.openStudyGroup(null)
            activeConversation != null -> viewModel.openConversation(null)
            currentTab != MainNavigationTab.FEED -> viewModel.selectTab(MainNavigationTab.FEED)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("omnisphere_root_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryIndigo,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OmniSphere",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Knowledge & Safety Hub",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Persona Switcher & XP Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { showPersonaDialog = true }
                            .padding(end = 8.dp)
                            .testTag("persona_switcher_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentProfile.avatarColorHex))
                            ) {
                                Text(
                                    text = currentProfile.name.take(1),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            CategoryBadge(category = currentProfile.category, compact = true)
                            Spacer(modifier = Modifier.width(4.dp))
                            ReputationPill(points = currentProfile.reputationPoints)
                        }
                    }

                    // Guardian Shield Quick Action
                    IconButton(
                        onClick = { viewModel.selectTab(MainNavigationTab.SAFETY) },
                        modifier = Modifier.testTag("topbar_safety_button")
                    ) {
                        Icon(
                            imageVector = if (safetySettings.guardianMode) Icons.Filled.Shield else Icons.Outlined.Shield,
                            contentDescription = "Guardian Mode",
                            tint = if (safetySettings.guardianMode) GuardianRose else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                windowInsets = WindowInsets.navigationBars
            ) {
                // 1. Feed
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.FEED,
                    onClick = { viewModel.selectTab(MainNavigationTab.FEED) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.FEED) Icons.Filled.Public else Icons.Outlined.Public,
                            contentDescription = "Feed"
                        )
                    },
                    label = { Text("Feed") },
                    modifier = Modifier.testTag("nav_tab_feed")
                )

                // 2. Study Groups
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.GROUPS,
                    onClick = { viewModel.selectTab(MainNavigationTab.GROUPS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.GROUPS) Icons.Filled.Groups else Icons.Outlined.Groups,
                            contentDescription = "Study Groups"
                        )
                    },
                    label = { Text("Groups") },
                    modifier = Modifier.testTag("nav_tab_groups")
                )

                // 3. Direct Messages
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.MESSAGES,
                    onClick = { viewModel.selectTab(MainNavigationTab.MESSAGES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.MESSAGES) Icons.Filled.Forum else Icons.Outlined.Forum,
                            contentDescription = "Messages"
                        )
                    },
                    label = { Text("Messages") },
                    modifier = Modifier.testTag("nav_tab_messages")
                )

                // 4. Reputation & Badges
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.REPUTATION,
                    onClick = { viewModel.selectTab(MainNavigationTab.REPUTATION) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.REPUTATION) Icons.Filled.Stars else Icons.Outlined.Stars,
                            contentDescription = "Reputation"
                        )
                    },
                    label = { Text("Reputation") },
                    modifier = Modifier.testTag("nav_tab_reputation")
                )

                // 5. Safety & Moderation
                NavigationBarItem(
                    selected = currentTab == MainNavigationTab.SAFETY,
                    onClick = { viewModel.selectTab(MainNavigationTab.SAFETY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavigationTab.SAFETY) Icons.Filled.Shield else Icons.Outlined.Shield,
                            contentDescription = "Safety"
                        )
                    },
                    label = { Text("Safety") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GuardianRose,
                        selectedTextColor = GuardianRose,
                        indicatorColor = GuardianRose.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_safety")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavigationTab.FEED -> FeedScreen(viewModel = viewModel)
                MainNavigationTab.GROUPS -> StudyGroupsScreen(viewModel = viewModel)
                MainNavigationTab.MESSAGES -> DirectMessagesScreen(viewModel = viewModel)
                MainNavigationTab.REPUTATION -> ReputationLeaderboardScreen(viewModel = viewModel)
                MainNavigationTab.SAFETY -> SafetyVaultScreen(viewModel = viewModel)
            }
        }
    }

    // Post Detail Dialog
    if (selectedPost != null) {
        PostDetailDialog(
            post = selectedPost!!,
            viewModel = viewModel,
            onDismiss = { viewModel.selectPost(null) }
        )
    }

    // Create Post Dialog
    if (showCreatePost) {
        CreatePostDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openCreatePost(false) }
        )
    }

    // Persona Switch Dialog
    if (showPersonaDialog) {
        PersonaSwitchDialog(
            personas = viewModel.demoPersonas,
            currentProfile = currentProfile,
            onSelect = { viewModel.switchPersona(it) },
            onDismiss = { showPersonaDialog = false }
        )
    }
}
