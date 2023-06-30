package config

import com.sksamuel.hoplite.ConfigAlias

data class ActionConfig (
    val collect: String,
    val apply: String,
    val translate: String,
    val fix: String,
)

data class StatusConfig (
    val collect: String,
    val apply: String,
    val translate: String,
    val fix: String
)

data class BranchConfig (
    val name: String,
    val action: ActionConfig
)

data class CommitConfig (
    val message: String,
    val action: ActionConfig
)

data class PullRequestConfig (
    val title: String,
    val body: String,
    val action: ActionConfig,
    val status: StatusConfig
)

data class GitConfig(
    val branch: BranchConfig,
    val commit: CommitConfig,
    @ConfigAlias("pull_request") val pullRequest: PullRequestConfig
)
