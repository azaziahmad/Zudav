package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.OmniSphereRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainNavigationTab(val title: String) {
    FEED("Feed"),
    GROUPS("Study Groups"),
    MESSAGES("Messages"),
    REPUTATION("Reputation"),
    SAFETY("Safety & Shield")
}

data class SprintTimerState(
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60
)

class OmniSphereViewModel(
    private val repository: OmniSphereRepository = OmniSphereRepository()
) : ViewModel() {

    val currentProfile: StateFlow<UserProfile> = repository.currentProfile
    val posts: StateFlow<List<CommunityPost>> = repository.posts
    val comments: StateFlow<Map<String, List<PostComment>>> = repository.comments
    val studyGroups: StateFlow<List<StudyGroup>> = repository.studyGroups
    val groupMessages: StateFlow<Map<String, List<GroupMessage>>> = repository.groupMessages
    val conversations: StateFlow<List<DirectConversation>> = repository.conversations
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = repository.directMessages
    val safetySettings: StateFlow<SafetySettings> = repository.safetySettings
    val safetyReports: StateFlow<List<SafetyReport>> = repository.safetyReports
    val badges: StateFlow<List<ReputationBadge>> = repository.badges
    val demoPersonas: List<UserProfile> = repository.demoPersonas

    // UI state
    private val _currentTab = MutableStateFlow(MainNavigationTab.FEED)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<UserCategory?>(null)
    val selectedCategoryFilter: StateFlow<UserCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPost = MutableStateFlow<CommunityPost?>(null)
    val selectedPost: StateFlow<CommunityPost?> = _selectedPost.asStateFlow()

    private val _showCreatePostDialog = MutableStateFlow(false)
    val showCreatePostDialog: StateFlow<Boolean> = _showCreatePostDialog.asStateFlow()

    private val _activeConversation = MutableStateFlow<DirectConversation?>(null)
    val activeConversation: StateFlow<DirectConversation?> = _activeConversation.asStateFlow()

    private val _activeStudyGroup = MutableStateFlow<StudyGroup?>(null)
    val activeStudyGroup: StateFlow<StudyGroup?> = _activeStudyGroup.asStateFlow()

    private val _showAnonymousReportDialog = MutableStateFlow(false)
    val showAnonymousReportDialog: StateFlow<Boolean> = _showAnonymousReportDialog.asStateFlow()

    private val _prefillReportedUser = MutableStateFlow("")
    val prefillReportedUser: StateFlow<String> = _prefillReportedUser.asStateFlow()

    // Study Sprint Timer
    private val _sprintTimer = MutableStateFlow(SprintTimerState())
    val sprintTimer: StateFlow<SprintTimerState> = _sprintTimer.asStateFlow()
    private var timerJob: Job? = null

    // Filtered Posts
    val filteredPosts: StateFlow<List<CommunityPost>> = combine(
        posts,
        _selectedCategoryFilter,
        _searchQuery
    ) { postList, category, query ->
        postList.filter { post ->
            val matchesCategory = category == null || post.authorCategory == category
            val matchesQuery = query.isBlank() ||
                    post.title.contains(query, ignoreCase = true) ||
                    post.content.contains(query, ignoreCase = true) ||
                    post.authorField.contains(query, ignoreCase = true) ||
                    post.tags.any { it.contains(query, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun setCategoryFilter(category: UserCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectPost(post: CommunityPost?) {
        _selectedPost.value = post
    }

    fun openCreatePost(show: Boolean) {
        _showCreatePostDialog.value = show
    }

    fun switchPersona(profileId: String) {
        repository.switchProfile(profileId)
    }

    fun createPost(
        title: String,
        content: String,
        category: UserCategory,
        field: String,
        tags: List<String>,
        postType: PostType,
        isAnonymous: Boolean
    ) {
        repository.createPost(title, content, category, field, tags, postType, isAnonymous)
        _showCreatePostDialog.value = false
    }

    fun toggleUpvote(postId: String) {
        repository.toggleUpvote(postId)
        // Refresh selected post if open
        if (_selectedPost.value?.id == postId) {
            _selectedPost.value = posts.value.find { it.id == postId }
        }
    }

    fun toggleHelpfulEndorsement(postId: String) {
        repository.toggleHelpfulEndorsement(postId)
        if (_selectedPost.value?.id == postId) {
            _selectedPost.value = posts.value.find { it.id == postId }
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val filteredText = sanitizeContent(text)
        repository.addComment(postId, filteredText)
    }

    fun markCommentAsHelpful(postId: String, commentId: String) {
        repository.markCommentAsHelpful(postId, commentId)
    }

    // Study Groups
    fun openStudyGroup(group: StudyGroup?) {
        _activeStudyGroup.value = group
    }

    fun toggleJoinGroup(groupId: String) {
        repository.toggleJoinGroup(groupId)
        if (_activeStudyGroup.value?.id == groupId) {
            _activeStudyGroup.value = studyGroups.value.find { it.id == groupId }
        }
    }

    fun sendGroupMessage(groupId: String, text: String, isResource: Boolean = false) {
        if (text.isBlank()) return
        val sanitized = sanitizeContent(text)
        repository.sendGroupMessage(groupId, sanitized, isResource)
    }

    // Study Sprint Timer
    fun toggleSprintTimer() {
        val current = _sprintTimer.value
        if (current.isRunning) {
            timerJob?.cancel()
            _sprintTimer.value = current.copy(isRunning = false)
        } else {
            _sprintTimer.value = current.copy(isRunning = true)
            timerJob = viewModelScope.launch {
                while (_sprintTimer.value.remainingSeconds > 0 && _sprintTimer.value.isRunning) {
                    delay(1000)
                    _sprintTimer.update {
                        val next = it.remainingSeconds - 1
                        if (next <= 0) {
                            it.copy(isRunning = false, remainingSeconds = 0)
                        } else {
                            it.copy(remainingSeconds = next)
                        }
                    }
                }
            }
        }
    }

    fun resetSprintTimer(minutes: Int = 25) {
        timerJob?.cancel()
        _sprintTimer.value = SprintTimerState(
            isRunning = false,
            remainingSeconds = minutes * 60,
            totalSeconds = minutes * 60
        )
    }

    // Direct Messages
    fun openConversation(conversation: DirectConversation?) {
        _activeConversation.value = conversation
    }

    fun startConversationWithUser(
        peerId: String,
        peerName: String,
        peerRole: String,
        peerCategory: UserCategory
    ) {
        val existing = conversations.value.find { it.peerId == peerId }
        if (existing != null) {
            _activeConversation.value = existing
        } else {
            val newConv = DirectConversation(
                id = "conv_${System.currentTimeMillis()}",
                peerId = peerId,
                peerName = peerName,
                peerRole = peerRole,
                peerCategory = peerCategory,
                lastMessage = "Started direct message conversation",
                lastTimestamp = System.currentTimeMillis()
            )
            _activeConversation.value = newConv
        }
        _currentTab.value = MainNavigationTab.MESSAGES
    }

    fun sendDirectMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val sanitized = sanitizeContent(text)
        repository.sendDirectMessage(conversationId, sanitized)

        // Simulate friendly mentor reply after brief pause if conversation with mentor
        val conv = conversations.value.find { it.id == conversationId }
        if (conv != null && !conv.isBlocked) {
            viewModelScope.launch {
                delay(1200)
                val reply = when (conv.peerCategory) {
                    UserCategory.WORKER -> "Thanks for reaching out! Happy to share advice on that. Keep breaking problems into smaller steps and let me know how it progresses."
                    UserCategory.COLLEGE -> "Awesome! I've been studying that exact topic in our college lab. Feel free to join our study group sprint tomorrow."
                    UserCategory.SCHOOL -> "That makes sense! We're testing that in our school club too. Let's compare results!"
                }
                repository.sendDirectMessage(conversationId, reply)
            }
        }
    }

    fun toggleBlockUser(conversationId: String) {
        repository.toggleBlockUser(conversationId)
        if (_activeConversation.value?.id == conversationId) {
            _activeConversation.value = conversations.value.find { it.id == conversationId }
        }
    }

    // Safety & Moderation
    fun openAnonymousReport(show: Boolean, reportedUser: String = "") {
        _prefillReportedUser.value = reportedUser
        _showAnonymousReportDialog.value = show
    }

    fun submitAnonymousReport(reportedUser: String, incidentType: String, details: String, evidence: String?) {
        repository.submitAnonymousReport(reportedUser, incidentType, details, evidence)
        _showAnonymousReportDialog.value = false
    }

    fun toggleGuardianMode(enabled: Boolean) {
        val cur = safetySettings.value
        repository.updateSafetySettings(cur.copy(guardianMode = enabled))
    }

    fun updateSafetySetting(
        shieldDMs: Boolean? = null,
        redactKeywords: Boolean? = null,
        hideOnline: Boolean? = null,
        allowVerifiedOnly: Boolean? = null
    ) {
        val cur = safetySettings.value
        repository.updateSafetySettings(
            cur.copy(
                shieldDMsFromStrangers = shieldDMs ?: cur.shieldDMsFromStrangers,
                redactToxicKeywords = redactKeywords ?: cur.redactToxicKeywords,
                hideOnlineStatus = hideOnline ?: cur.hideOnlineStatus,
                allowVerifiedMentorsOnly = allowVerifiedOnly ?: cur.allowVerifiedMentorsOnly
            )
        )
    }

    private fun sanitizeContent(text: String): String {
        if (!safetySettings.value.redactToxicKeywords) return text
        val toxicWords = listOf("idiot", "hate", "harass", "send pics", "creep", "stupid", "scam")
        var result = text
        for (word in toxicWords) {
            val regex = "(?i)\\b$word\\b".toRegex()
            result = result.replace(regex, "[shielded]")
        }
        return result
    }
}
