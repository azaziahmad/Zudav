package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class OmniSphereRepository {

    // Available personas to test cross-category interactions
    val demoPersonas = listOf(
        UserProfile(
            id = "user_maya",
            name = "Maya Lin",
            handle = "@mayascience",
            category = UserCategory.SCHOOL,
            fieldOfKnowledge = "High School STEM & Robotics",
            schoolOrCompany = "Lincoln High School (Senior)",
            reputationPoints = 240,
            badges = listOf("Rising Scholar", "Physics Solver", "STEM Enthusiast"),
            isGuardianModeEnabled = true,
            avatarColorHex = 0xFF7E57C2,
            bio = "High school senior loving AP Physics, Arduino robotics & math puzzles. Looking for college mentor advice!",
            helpfulVotesReceived = 18
        ),
        UserProfile(
            id = "user_devon",
            name = "Devon Brooks",
            handle = "@devon_codes",
            category = UserCategory.COLLEGE,
            fieldOfKnowledge = "Computer Science & Data",
            schoolOrCompany = "State Polytechnic University (Junior)",
            reputationPoints = 530,
            badges = listOf("Knowledge Pioneer", "Peer Mentor", "Algorithm Sage"),
            isGuardianModeEnabled = false,
            avatarColorHex = 0xFF00897B,
            bio = "CS 3rd year. Passionate about distributed systems, Kotlin & preparing for summer software engineering internships.",
            helpfulVotesReceived = 42
        ),
        UserProfile(
            id = "user_sarah",
            name = "Sarah Al-Mansoor",
            handle = "@sarah_eng",
            category = UserCategory.WORKER,
            fieldOfKnowledge = "Systems Architecture & Cloud",
            schoolOrCompany = "Apex Distributed Tech (Staff Engineer)",
            reputationPoints = 1120,
            badges = listOf("Master Luminary", "Verified Mentor", "Industry Guide", "Guardian Ally"),
            isGuardianModeEnabled = true,
            avatarColorHex = 0xFF3949AB,
            bio = "Staff Software Engineer with 8 years in tech. Volunteering to guide school and college students into engineering careers.",
            helpfulVotesReceived = 115
        )
    )

    private val _currentProfile = MutableStateFlow(demoPersonas[0])
    val currentProfile: StateFlow<UserProfile> = _currentProfile.asStateFlow()

    private val _posts = MutableStateFlow(initialPosts())
    val posts: StateFlow<List<CommunityPost>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow(initialComments())
    val comments: StateFlow<Map<String, List<PostComment>>> = _comments.asStateFlow()

    private val _studyGroups = MutableStateFlow(initialStudyGroups())
    val studyGroups: StateFlow<List<StudyGroup>> = _studyGroups.asStateFlow()

    private val _groupMessages = MutableStateFlow(initialGroupMessages())
    val groupMessages: StateFlow<Map<String, List<GroupMessage>>> = _groupMessages.asStateFlow()

    private val _conversations = MutableStateFlow(initialConversations())
    val conversations: StateFlow<List<DirectConversation>> = _conversations.asStateFlow()

    private val _directMessages = MutableStateFlow(initialDirectMessages())
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = _directMessages.asStateFlow()

    private val _safetySettings = MutableStateFlow(SafetySettings())
    val safetySettings: StateFlow<SafetySettings> = _safetySettings.asStateFlow()

    private val _safetyReports = MutableStateFlow(initialSafetyReports())
    val safetyReports: StateFlow<List<SafetyReport>> = _safetyReports.asStateFlow()

    private val _badges = MutableStateFlow(initialBadges())
    val badges: StateFlow<List<ReputationBadge>> = _badges.asStateFlow()

    fun switchProfile(profileId: String) {
        val found = demoPersonas.find { it.id == profileId } ?: return
        _currentProfile.value = found
    }

    // Community Posts Actions
    fun createPost(
        title: String,
        content: String,
        category: UserCategory,
        field: String,
        tags: List<String>,
        postType: PostType,
        isAnonymous: Boolean
    ) {
        val user = _currentProfile.value
        val newPost = CommunityPost(
            id = UUID.randomUUID().toString(),
            authorId = user.id,
            authorName = if (isAnonymous) "Anonymous Scholar" else user.name,
            authorCategory = category,
            authorField = field,
            title = title,
            content = content,
            tags = tags,
            postType = postType,
            upvotes = 1,
            helpfulVotes = 0,
            commentsCount = 0,
            timestamp = System.currentTimeMillis(),
            isUpvotedByMe = true,
            isAnonymousSafePost = isAnonymous
        )
        _posts.update { listOf(newPost) + it }
        // Award XP for contributing knowledge!
        addReputationPoints(20)
    }

    fun toggleUpvote(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newIsUpvoted = !post.isUpvotedByMe
                    val countDiff = if (newIsUpvoted) 1 else -1
                    post.copy(
                        isUpvotedByMe = newIsUpvoted,
                        upvotes = (post.upvotes + countDiff).coerceAtLeast(0)
                    )
                } else post
            }
        }
    }

    fun toggleHelpfulEndorsement(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newHelpful = !post.isHelpfulVotedByMe
                    val countDiff = if (newHelpful) 1 else -1
                    // Award extra points to author
                    post.copy(
                        isHelpfulVotedByMe = newHelpful,
                        helpfulVotes = (post.helpfulVotes + countDiff).coerceAtLeast(0)
                    )
                } else post
            }
        }
        addReputationPoints(5)
    }

    fun addComment(postId: String, text: String) {
        val user = _currentProfile.value
        val comment = PostComment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            authorId = user.id,
            authorName = user.name,
            authorCategory = user.category,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _comments.update { map ->
            val curList = map[postId] ?: emptyList()
            map + (postId to (curList + comment))
        }
        _posts.update { list ->
            list.map { if (it.id == postId) it.copy(commentsCount = it.commentsCount + 1) else it }
        }
        addReputationPoints(10)
    }

    fun markCommentAsHelpful(postId: String, commentId: String) {
        _comments.update { map ->
            val list = map[postId] ?: return@update map
            map + (postId to list.map {
                if (it.id == commentId) it.copy(isHelpfulAnswer = !it.isHelpfulAnswer) else it
            })
        }
        addReputationPoints(25)
    }

    // Study Groups
    fun toggleJoinGroup(groupId: String) {
        _studyGroups.update { list ->
            list.map { group ->
                if (group.id == groupId) {
                    val willJoin = !group.isJoined
                    group.copy(
                        isJoined = willJoin,
                        memberCount = group.memberCount + if (willJoin) 1 else -1
                    )
                } else group
            }
        }
    }

    fun sendGroupMessage(groupId: String, text: String, isResource: Boolean = false) {
        val user = _currentProfile.value
        val newMsg = GroupMessage(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            senderId = user.id,
            senderName = user.name,
            senderCategory = user.category,
            message = text,
            timestamp = System.currentTimeMillis(),
            isResourceLink = isResource
        )
        _groupMessages.update { map ->
            val cur = map[groupId] ?: emptyList()
            map + (groupId to (cur + newMsg))
        }
    }

    // Direct Messaging
    fun sendDirectMessage(conversationId: String, text: String) {
        val user = _currentProfile.value
        val newMsg = DirectMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = user.id,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        _directMessages.update { map ->
            val cur = map[conversationId] ?: emptyList()
            map + (conversationId to (cur + newMsg))
        }
        _conversations.update { list ->
            list.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = text,
                        lastTimestamp = System.currentTimeMillis()
                    )
                } else conv
            }
        }
    }

    fun toggleBlockUser(conversationId: String) {
        _conversations.update { list ->
            list.map {
                if (it.id == conversationId) it.copy(isBlocked = !it.isBlocked) else it
            }
        }
    }

    // Safety & Moderation
    fun updateSafetySettings(settings: SafetySettings) {
        _safetySettings.value = settings
    }

    fun submitAnonymousReport(
        reportedUser: String,
        incidentType: String,
        details: String,
        evidence: String?
    ) {
        val report = SafetyReport(
            id = "REP-${System.currentTimeMillis().toString().takeLast(6)}",
            isAnonymous = true,
            reportedUserName = reportedUser,
            incidentType = incidentType,
            details = details,
            timestamp = System.currentTimeMillis(),
            status = "Shield Activated (Under Review)",
            evidenceSnippet = evidence
        )
        _safetyReports.update { listOf(report) + it }
    }

    private fun addReputationPoints(points: Int) {
        _currentProfile.update { it.copy(reputationPoints = it.reputationPoints + points) }
        checkBadgeUnlocks()
    }

    private fun checkBadgeUnlocks() {
        val pts = _currentProfile.value.reputationPoints
        _badges.update { list ->
            list.map { badge ->
                if (pts >= badge.pointsRequired) badge.copy(isUnlocked = true) else badge
            }
        }
    }

    // Seed Data
    private fun initialPosts(): List<CommunityPost> = listOf(
        CommunityPost(
            id = "post_1",
            authorId = "user_sarah",
            authorName = "Sarah Al-Mansoor",
            authorCategory = UserCategory.WORKER,
            authorField = "Staff Cloud Architect",
            title = "What I wish I knew before choosing Tech vs Academia: A 8-year retrospective",
            content = "For high schoolers and undergrads deciding between industry software engineering and academic research: In the workplace, continuous collaboration and clear written communication matter just as much as coding speed. Here are 3 non-intuitive lessons from real production systems...\n\n1. Real systems fail gracefully; understand failure modes early.\n2. Cross-disciplinary mentors (math, physics, design) give you unfair advantages.\n3. Keep an 'achievement brag doc' to avoid imposter syndrome.",
            tags = listOf("CareerAdvice", "SoftwareEngineering", "Mentorship", "IndustryRealities"),
            postType = PostType.CAREER_JOURNEY,
            upvotes = 89,
            helpfulVotes = 34,
            commentsCount = 14,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3
        ),
        CommunityPost(
            id = "post_2",
            authorId = "user_devon",
            authorName = "Devon Brooks",
            authorCategory = UserCategory.COLLEGE,
            authorField = "Computer Science Junior",
            title = "Visual Guide to Graph Algorithms & Dynamic Programming without getting overwhelmed",
            content = "I struggled with graph traversals (BFS, DFS, Dijkstra) and DP memoization until I started mapping them to real-world subway maps and dependency resolution trees. I compiled our college study group's visual cheatsheet covering: \n- State recurrence equations\n- Space optimization tricks\n- Common edge cases to look out for in coding interviews.",
            tags = listOf("DataStructures", "Algorithms", "CollegeStudyHacks", "TechInterview"),
            postType = PostType.KNOWLEDGE_BYTE,
            upvotes = 112,
            helpfulVotes = 48,
            commentsCount = 19,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 7
        ),
        CommunityPost(
            id = "post_3",
            authorId = "user_maya",
            authorName = "Maya Lin",
            authorCategory = UserCategory.SCHOOL,
            authorField = "High School Grade 12 STEM",
            title = "Built an autonomous obstacle-avoidance robot using Arduino & Ultrasonic Sensors!",
            content = "For our high school state science fair, our team designed a 3D-printed rover with ultrasonic distance sensors and PID motor speed control. Any college or working robotics engineers here? We noticed occasional ultrasonic ghost-reflections near metallic surfaces and would love advice on sensor smoothing filters (Kalman vs EMA).",
            tags = listOf("Robotics", "Arduino", "HighSchoolSTEM", "Sensors", "HardwareProject"),
            postType = PostType.PROJECT_SHOWCASE,
            upvotes = 74,
            helpfulVotes = 21,
            commentsCount = 11,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 12
        ),
        CommunityPost(
            id = "post_4",
            authorId = "user_elena",
            authorName = "Dr. Elena Rostova",
            authorCategory = UserCategory.WORKER,
            authorField = "Biomedical Engineer",
            title = "Bridging High School Biology to Cutting-Edge Genetic Algorithms",
            content = "Many students ask whether biological systems connect to computational intelligence. In biomedical research, gene sequence alignment uses dynamic programming (Needleman-Wunsch algorithm) exactly like text diff engines! If you're a student interested in Bio-Informatics, feel free to drop questions here.",
            tags = listOf("BioInformatics", "Biology", "Interdisciplinary", "Medicine"),
            postType = PostType.KNOWLEDGE_BYTE,
            upvotes = 63,
            helpfulVotes = 19,
            commentsCount = 8,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24
        ),
        CommunityPost(
            id = "post_5",
            authorId = "user_ananya",
            authorName = "Ananya Sharma",
            authorCategory = UserCategory.COLLEGE,
            authorField = "Graphic Design & HCI",
            title = "How we created an accessible color contrast design system for college campus portals",
            content = "Sharing our UX case study on rebuilding our college student portal for WCAG AAA accessibility. We tested screen readers, colorblind simulation palettes, and haptic feedback. Workers in UX: what design QA tools do you use in industry sprints?",
            tags = listOf("UIUX", "Accessibility", "DesignThinking", "CollegeProject"),
            postType = PostType.QUESTION,
            upvotes = 56,
            helpfulVotes = 17,
            commentsCount = 9,
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 36
        )
    )

    private fun initialComments(): Map<String, List<PostComment>> = mapOf(
        "post_1" to listOf(
            PostComment(
                id = "c1",
                postId = "post_1",
                authorId = "user_devon",
                authorName = "Devon Brooks",
                authorCategory = UserCategory.COLLEGE,
                text = "The 'brag doc' point is huge! As an undergrad juggling coursework and side projects, keeping a weekly log has helped me prep for internship interviews ten times better.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                isHelpfulAnswer = true
            ),
            PostComment(
                id = "c2",
                postId = "post_1",
                authorId = "user_maya",
                authorName = "Maya Lin",
                authorCategory = UserCategory.SCHOOL,
                text = "Do companies value robotics hardware clubs or pure software more for high school seniors applying to college engineering tracks?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 90
            ),
            PostComment(
                id = "c3",
                postId = "post_1",
                authorId = "user_sarah",
                authorName = "Sarah Al-Mansoor",
                authorCategory = UserCategory.WORKER,
                text = "@Maya Hardware + embedded software shows end-to-end grit! Admissions and recruiters love seeing tactile projects where code interacts with real physical physics.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                isHelpfulAnswer = true
            )
        ),
        "post_3" to listOf(
            PostComment(
                id = "c4",
                postId = "post_3",
                authorId = "user_sarah",
                authorName = "Sarah Al-Mansoor",
                authorCategory = UserCategory.WORKER,
                text = "For ultrasonic bounce on metal: Start with an Exponential Moving Average (EMA) with alpha=0.3. If metallic multi-path echoes cause sporadic spikes, add a median filter window of 5 samples first!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 200,
                isHelpfulAnswer = true
            )
        )
    )

    private fun initialStudyGroups(): List<StudyGroup> = listOf(
        StudyGroup(
            id = "grp_1",
            name = "Girls in STEM & Tech Mentorship Guild",
            targetAudiences = listOf(UserCategory.SCHOOL, UserCategory.COLLEGE, UserCategory.WORKER),
            subjectField = "STEM, Leadership & Mentorship",
            description = "A supportive, moderated circle where women in industry mentor high school and college students navigating engineering, science, and career paths.",
            memberCount = 124,
            activeSprintTopic = "Preparing for Fall Hackathons & Research Proposals",
            resourcesCount = 38,
            isJoined = true,
            studyGoal = "Weekly virtual check-ins & resume teardowns"
        ),
        StudyGroup(
            id = "grp_2",
            name = "Calculus, Physics & Olympiad Sprints",
            targetAudiences = listOf(UserCategory.SCHOOL, UserCategory.COLLEGE),
            subjectField = "Mathematics & Physics",
            description = "Dedicated problem-solving sessions for AP Calculus, Mechanics, and competitive Olympiads with college peer tutors.",
            memberCount = 86,
            activeSprintTopic = "Integration by parts & Rotational Dynamics",
            resourcesCount = 24,
            isJoined = true,
            studyGoal = "Solve 15 Olympiad-level problems weekly"
        ),
        StudyGroup(
            id = "grp_3",
            name = "Distributed Systems & Cloud Architecture",
            targetAudiences = listOf(UserCategory.COLLEGE, UserCategory.WORKER),
            subjectField = "Cloud Computing & Backend",
            description = "College seniors and practicing engineers reading classic whitepapers (Raft, MapReduce, DynamoDB) and reviewing system design.",
            memberCount = 67,
            activeSprintTopic = "Designing Resilient Event-Driven Architectures",
            resourcesCount = 42,
            isJoined = false,
            studyGoal = "Monthly architecture blueprint breakdown"
        ),
        StudyGroup(
            id = "grp_4",
            name = "BioMed & Health Tech Explorers",
            targetAudiences = listOf(UserCategory.SCHOOL, UserCategory.COLLEGE, UserCategory.WORKER),
            subjectField = "Biomedical, Pre-Med & Biotech",
            description = "Exploring medical diagnostics, prosthetic engineering, and clinical data science across academic tiers.",
            memberCount = 53,
            activeSprintTopic = "Neural interface sensors and EEG signal analysis",
            resourcesCount = 19,
            isJoined = false,
            studyGoal = "Bi-weekly journal club discussion"
        )
    )

    private fun initialGroupMessages(): Map<String, List<GroupMessage>> = mapOf(
        "grp_1" to listOf(
            GroupMessage(
                id = "gm_1",
                groupId = "grp_1",
                senderId = "user_sarah",
                senderName = "Sarah Al-Mansoor",
                senderCategory = UserCategory.WORKER,
                message = "Welcome everyone! I just uploaded our new guide on negotiating summer research stipends to the group vault.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 300,
                isResourceLink = true
            ),
            GroupMessage(
                id = "gm_2",
                groupId = "grp_1",
                senderId = "user_maya",
                senderName = "Maya Lin",
                senderCategory = UserCategory.SCHOOL,
                message = "Thank you Sarah! I've been reading through the robotics section and it clarified so much about college lab applications.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180
            ),
            GroupMessage(
                id = "gm_3",
                groupId = "grp_1",
                senderId = "user_priya",
                senderName = "Priya Nair",
                senderCategory = UserCategory.COLLEGE,
                message = "Who is joining tomorrow's mock technical interview study sprint at 5 PM EST? We'll practice two system design prompts together!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60
            )
        ),
        "grp_2" to listOf(
            GroupMessage(
                id = "gm_4",
                groupId = "grp_2",
                senderId = "user_devon",
                senderName = "Devon Brooks",
                senderCategory = UserCategory.COLLEGE,
                message = "Shared a quick cheatsheet on differential equation transforms in the group notes tab.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 150,
                isResourceLink = true
            )
        )
    )

    private fun initialConversations(): List<DirectConversation> = listOf(
        DirectConversation(
            id = "conv_1",
            peerId = "user_sarah",
            peerName = "Sarah Al-Mansoor",
            peerRole = "Staff Systems Engineer & Industry Mentor",
            peerCategory = UserCategory.WORKER,
            lastMessage = "Great job on your robotics filter test! Let me know if you need any book recommendations.",
            lastTimestamp = System.currentTimeMillis() - 1000 * 60 * 25,
            unreadCount = 1,
            isVerifiedMentor = true
        ),
        DirectConversation(
            id = "conv_2",
            peerId = "user_devon",
            peerName = "Devon Brooks",
            peerRole = "CS Junior & Peer Tutor",
            peerCategory = UserCategory.COLLEGE,
            lastMessage = "Here's the link to the open source project repo we discussed in the study group.",
            lastTimestamp = System.currentTimeMillis() - 1000 * 60 * 180,
            unreadCount = 0,
            isVerifiedMentor = false
        ),
        DirectConversation(
            id = "conv_3",
            peerId = "user_priya",
            peerName = "Priya Nair",
            peerRole = "College Senior & Hackathon Lead",
            peerCategory = UserCategory.COLLEGE,
            lastMessage = "Would you like to partner up for the upcoming student civic tech challenge?",
            lastTimestamp = System.currentTimeMillis() - 1000 * 60 * 720,
            unreadCount = 0,
            isVerifiedMentor = false
        )
    )

    private fun initialDirectMessages(): Map<String, List<DirectMessage>> = mapOf(
        "conv_1" to listOf(
            DirectMessage(
                id = "m1",
                conversationId = "conv_1",
                senderId = "user_sarah",
                text = "Hello Maya! I saw your post on the autonomous rover project in the feed. Really impressive initiative for a high school senior.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 80,
                isFromMe = false
            ),
            DirectMessage(
                id = "m2",
                conversationId = "conv_1",
                senderId = "user_maya",
                text = "Thank you so much Sarah! Your advice on using the median filter before the exponential moving average completely resolved the metallic bounce issue.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 50,
                isFromMe = true
            ),
            DirectMessage(
                id = "m3",
                conversationId = "conv_1",
                senderId = "user_sarah",
                text = "Great job on your robotics filter test! Let me know if you need any book recommendations.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                isFromMe = false
            )
        )
    )

    private fun initialSafetyReports(): List<SafetyReport> = listOf(
        SafetyReport(
            id = "REP-832104",
            isAnonymous = true,
            reportedUserName = "shadow_user92",
            incidentType = "Inappropriate / Unsolicited Direct Message",
            details = "User repeatedly messaged asking for personal location and phone number outside the study topic.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
            status = "Investigated & User Restricted",
            evidenceSnippet = "Filtered transcript preserved in secure moderation log."
        ),
        SafetyReport(
            id = "REP-914227",
            isAnonymous = true,
            reportedUserName = "bot_scammer_3",
            incidentType = "Homework Cheating / Paid Essay Scam",
            details = "Spamming study group chat with malicious payment links.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 18,
            status = "Shield Activated (Under Review)",
            evidenceSnippet = "Automated spam shield flagged duplicate links."
        )
    )

    private fun initialBadges(): List<ReputationBadge> = listOf(
        ReputationBadge(
            id = "b1",
            name = "Knowledge Spark",
            description = "Shared your first knowledge byte or project with the community",
            pointsRequired = 50,
            isUnlocked = true,
            tier = "Bronze"
        ),
        ReputationBadge(
            id = "b2",
            name = "Helpful Guide",
            description = "Received 10+ verified helpful endorsements from peers and workers",
            pointsRequired = 200,
            isUnlocked = true,
            tier = "Silver"
        ),
        ReputationBadge(
            id = "b3",
            name = "Study Squad Anchor",
            description = "Actively participated in and shared resources inside collaborative study groups",
            pointsRequired = 400,
            isUnlocked = false,
            tier = "Silver"
        ),
        ReputationBadge(
            id = "b4",
            name = "Cross-Field Mentor",
            description = "Mentored across different categories (School -> College or College -> Worker)",
            pointsRequired = 750,
            isUnlocked = false,
            tier = "Gold"
        ),
        ReputationBadge(
            id = "b5",
            name = "Guardian Angel",
            description = "Contributed to maintaining safety, respectful discussions & community moderation",
            pointsRequired = 500,
            isUnlocked = false,
            tier = "Gold"
        ),
        ReputationBadge(
            id = "b6",
            name = "Omni Luminary",
            description = "Achieved 1000+ reputation points through stellar verified answers and insights",
            pointsRequired = 1000,
            isUnlocked = false,
            tier = "Platinum"
        )
    )
}
