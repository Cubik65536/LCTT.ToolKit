package config

import com.sksamuel.hoplite.ConfigAlias

data class GithubConfig (
    @ConfigAlias("github_id") val githubID: String,
    @ConfigAlias("github_token") val githubToken: String,
    @ConfigAlias("repo_path") val repoPath: String
)
