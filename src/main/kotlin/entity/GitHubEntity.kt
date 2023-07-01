package entity

import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import utils.HttpUtil

class GitHubEntity (val config: GitHubConfig) {
    private val GITHUB_API_URL = "https://api.github.com"

    fun verifyUser(): Boolean {
        var result: Boolean
        runBlocking {
            result = config.githubID == getAuthUser()
        }
        return result
    }

    private suspend fun getAuthUser(): String {
        val url = "${GITHUB_API_URL}/user"

        val response = HttpUtil().getJSON(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            io.ktor.http.headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }

        return response.string("login").toString()
    }
}
