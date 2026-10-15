package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReputationBadge
import com.example.data.model.UserCategory
import com.example.ui.components.CategoryBadge
import com.example.ui.components.ReputationPill
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.ReputationGold
import com.example.ui.viewmodel.OmniSphereViewModel

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val category: UserCategory,
    val role: String,
    val points: Int,
    val badgeTitle: String
)

@Composable
fun ReputationLeaderboardScreen(
    viewModel: OmniSphereViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val badges by viewModel.badges.collectAsState()
    var selectedLeaderboardFilter by remember { mutableStateOf<UserCategory?>(null) }

    val nextTierPoints = when {
        currentProfile.reputationPoints < 300 -> 300
        currentProfile.reputationPoints < 750 -> 750
        currentProfile.reputationPoints < 1200 -> 1200
        else -> 2000
    }
    val currentTierName = when {
        currentProfile.reputationPoints < 300 -> "Bronze Explorer"
        currentProfile.reputationPoints < 750 -> "Silver Scholar"
        currentProfile.reputationPoints < 1200 -> "Gold Mentor"
        else -> "Platinum Luminary"
    }

    val progress = (currentProfile.reputationPoints.toFloat() / nextTierPoints).coerceIn(0f, 1f)

    val allLeaderboardUsers = listOf(
        LeaderboardUser(1, "Sarah Al-Mansoor", UserCategory.WORKER, "Staff Systems Architect", 1120, "Master Luminary"),
        LeaderboardUser(2, "Dr. Elena Rostova", UserCategory.WORKER, "Biomedical Lead", 890, "Gold Mentor"),
        LeaderboardUser(3, "Devon Brooks", UserCategory.COLLEGE, "CS Junior & Peer Tutor", 530, "Silver Scholar"),
        LeaderboardUser(4, "Ananya Sharma", UserCategory.COLLEGE, "HCI Design Senior", 460, "Silver Scholar"),
        LeaderboardUser(5, "Maya Lin", UserCategory.SCHOOL, "High School STEM & Robotics", 240, "Bronze Explorer"),
        LeaderboardUser(6, "Kevin Zhao", UserCategory.SCHOOL, "Math Olympiad Finalist", 215, "Bronze Explorer")
    )

    val filteredLeaderboard = remember(selectedLeaderboardFilter) {
        if (selectedLeaderboardFilter == null) allLeaderboardUsers
        else allLeaderboardUsers.filter { it.category == selectedLeaderboardFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reputation_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Gamified Reputation & Badges",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Earn verified XP by sharing knowledge bytes, mentoring, and solving peer questions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Current Profile Tier Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "CURRENT REPUTATION TIER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentTierName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryIndigo
                            )
                        }

                        ReputationPill(points = currentProfile.reputationPoints)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ReputationGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${currentProfile.reputationPoints} XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                        Text(
                            text = "Next Tier at $nextTierPoints XP",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // How Points Work
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "HOW KNOWLEDGE XP IS EARNED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        XpRuleItem(icon = "💡", label = "Share Post", pts = "+20 XP")
                        XpRuleItem(icon = "⭐", label = "Helpful Endorse", pts = "+5 XP")
                        XpRuleItem(icon = "✓", label = "Verified Solution", pts = "+25 XP")
                        XpRuleItem(icon = "💬", label = "Discussion", pts = "+10 XP")
                    }
                }
            }
        }

        // Badges Collection Grid
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "REPUTATION BADGES & ACCOMPLISHMENTS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(badges.chunked(2)) { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                pair.forEach { badge ->
                    BadgeCard(
                        badge = badge,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Leaderboard Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "COMMUNITY LEADERBOARD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = ReputationGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedLeaderboardFilter == null,
                            onClick = { selectedLeaderboardFilter = null },
                            label = { Text("Top All") }
                        )
                    }
                    items(UserCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedLeaderboardFilter == cat,
                            onClick = {
                                selectedLeaderboardFilter = if (selectedLeaderboardFilter == cat) null else cat
                            },
                            label = { Text(cat.label) }
                        )
                    }
                }
            }
        }

        // Leaderboard Users List
        items(filteredLeaderboard) { user ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (user.name == currentProfile.name) PrimaryIndigo.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Rank Medal
                    Text(
                        text = "#${user.rank}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = when (user.rank) {
                            1 -> ReputationGold
                            2 -> Color(0xFF90A4AE)
                            3 -> Color(0xFFB08968)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.width(32.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CategoryBadge(category = user.category, compact = true)
                        }
                        Text(
                            text = "${user.role} • ${user.badgeTitle}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${user.points} XP",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = PrimaryIndigo
                    )
                }
            }
        }
    }
}

@Composable
fun XpRuleItem(icon: String, label: String, pts: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Text(text = pts, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFF57F17))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BadgeCard(badge: ReputationBadge, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) Icons.Filled.Stars else Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) ReputationGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Surface(
                    color = if (badge.isUnlocked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (badge.isUnlocked) "UNLOCKED" else "${badge.pointsRequired} XP",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp,
                maxLines = 2
            )
        }
    }
}
