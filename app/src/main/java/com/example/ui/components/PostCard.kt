package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityPost
import com.example.data.model.PostType
import com.example.ui.theme.GuardianRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.ReputationGold
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onUpvote: () -> Unit,
    onHelpfulEndorse: () -> Unit,
    onCommentClick: () -> Unit,
    onMessageAuthor: () -> Unit,
    onReportPost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Author Avatar + Name + Category + Report
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Author Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (post.isAnonymousSafePost) Color(0xFF607D8B)
                            else PrimaryIndigo
                        )
                ) {
                    if (post.isAnonymousSafePost) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Anonymous Post",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.isAnonymousSafePost) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "🛡️ Safe Mode",
                                fontSize = 11.sp,
                                color = GuardianRose,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Text(
                        text = post.authorField,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                CategoryBadge(
                    category = post.authorCategory,
                    compact = true
                )

                IconButton(
                    onClick = onReportPost,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("report_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = "Anonymous Report",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Type Badge + Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = when (post.postType) {
                        PostType.KNOWLEDGE_BYTE -> Color(0xFFE8EAF6)
                        PostType.CAREER_JOURNEY -> Color(0xFFFFF3E0)
                        PostType.QUESTION -> Color(0xFFFBE9E7)
                        PostType.PROJECT_SHOWCASE -> Color(0xFFE0F2F1)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = post.postType.title,
                        color = when (post.postType) {
                            PostType.KNOWLEDGE_BYTE -> PrimaryIndigo
                            PostType.CAREER_JOURNEY -> Color(0xFFE65100)
                            PostType.QUESTION -> Color(0xFFD84315)
                            PostType.PROJECT_SHOWCASE -> Color(0xFF00695C)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = formatTimeAgo(post.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Post Snippet
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                post.tags.take(3).forEach { tag ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Bar: Upvote, Helpful Endorsement, Comments, Message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Upvote
                FilledTonalButton(
                    onClick = onUpvote,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (post.isUpvotedByMe) PrimaryIndigo.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("upvote_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isUpvotedByMe) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (post.isUpvotedByMe) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.upvotes}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (post.isUpvotedByMe) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Helpful Endorsement (Gamified +XP for author!)
                FilledTonalButton(
                    onClick = onHelpfulEndorse,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (post.isHelpfulVotedByMe) Color(0xFFFFF8E1)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("helpful_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = "Helpful Endorsement",
                        tint = if (post.isHelpfulVotedByMe) ReputationGold else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Helpful ${post.helpfulVotes}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (post.isHelpfulVotedByMe) Color(0xFFF57F17) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Comments
                TextButton(
                    onClick = onCommentClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("comments_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.commentsCount}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Direct Message Author
                IconButton(
                    onClick = onMessageAuthor,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("dm_author_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Message Author",
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimeAgo(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        else -> "${days}d ago"
    }
}
