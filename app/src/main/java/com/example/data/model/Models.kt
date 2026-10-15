package com.example.data.model

enum class UserCategory(val label: String, val badgeTitle: String) {
    SCHOOL("School Student", "School"),
    COLLEGE("College Student", "College"),
    WORKER("Worker / Professional", "Professional")
}

enum class PostType(val title: String) {
    KNOWLEDGE_BYTE("Knowledge Byte"),
    CAREER_JOURNEY("Career & Life Experience"),
    QUESTION("Ask Community"),
    PROJECT_SHOWCASE("Project Showcase")
}

data class UserProfile(
    val id: String,
    val name: String,
    val handle: String,
    val category: UserCategory,
    val fieldOfKnowledge: String,
    val schoolOrCompany: String,
    val reputationPoints: Int,
    val badges: List<String>,
    val isGuardianModeEnabled: Boolean = true,
    val avatarColorHex: Long = 0xFF3F51B5,
    val bio: String = "",
    val helpfulVotesReceived: Int = 12
)

data class CommunityPost(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorCategory: UserCategory,
    val authorField: String,
    val title: String,
    val content: String,
    val tags: List<String>,
    val postType: PostType,
    val upvotes: Int = 0,
    val helpfulVotes: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isUpvotedByMe: Boolean = false,
    val isHelpfulVotedByMe: Boolean = false,
    val isAnonymousSafePost: Boolean = false
)

data class PostComment(
    val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorCategory: UserCategory,
    val text: String,
    val timestamp: Long,
    val isHelpfulAnswer: Boolean = false
)

data class StudyGroup(
    val id: String,
    val name: String,
    val targetAudiences: List<UserCategory>,
    val subjectField: String,
    val description: String,
    val memberCount: Int,
    val activeSprintTopic: String,
    val resourcesCount: Int,
    val isJoined: Boolean = false,
    val studyGoal: String = "Complete weekly practice & shared notes"
)

data class GroupMessage(
    val id: String,
    val groupId: String,
    val senderId: String,
    val senderName: String,
    val senderCategory: UserCategory,
    val message: String,
    val timestamp: Long,
    val isResourceLink: Boolean = false
)

data class DirectConversation(
    val id: String,
    val peerId: String,
    val peerName: String,
    val peerRole: String,
    val peerCategory: UserCategory,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int = 0,
    val isBlocked: Boolean = false,
    val isVerifiedMentor: Boolean = false
)

data class DirectMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean
)

data class SafetyReport(
    val id: String,
    val isAnonymous: Boolean,
    val reportedUserName: String,
    val incidentType: String,
    val details: String,
    val timestamp: Long,
    val status: String, // "Under Review", "Shield Activated", "Investigated & Action Taken"
    val evidenceSnippet: String? = null
)

data class ReputationBadge(
    val id: String,
    val name: String,
    val description: String,
    val pointsRequired: Int,
    val isUnlocked: Boolean,
    val tier: String // "Bronze", "Silver", "Gold", "Platinum"
)

data class SafetySettings(
    val guardianMode: Boolean = true,
    val shieldDMsFromStrangers: Boolean = true,
    val redactToxicKeywords: Boolean = true,
    val hideOnlineStatus: Boolean = true,
    val anonymousPostDefault: Boolean = false,
    val allowVerifiedMentorsOnly: Boolean = false
)
