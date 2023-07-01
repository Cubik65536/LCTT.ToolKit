package entity

import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*
import utils.HttpUtil

class GitHubEntity (private val config: GitHubConfig) {
    private val GITHUB_API_URL = "https://api.github.com"

    fun verifyUser(): Boolean {
        return config.githubID == getAuthUser()
    }

    private fun getAuthUser(): String {
        val url = "${GITHUB_API_URL}/user"

        val response = HttpUtil().GetRequests().getBodyJSON(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }

        return response.string("login").toString()
    }
}
