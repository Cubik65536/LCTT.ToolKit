package config

import com.sksamuel.hoplite.ConfigAlias

data class GitHubConfig (
    @ConfigAlias("github_id") val githubID: String,
    @ConfigAlias("github_token") val githubToken: String,
    @ConfigAlias("repo_name") val repoName: String
)
