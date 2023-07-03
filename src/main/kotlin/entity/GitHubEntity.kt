package entity

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import config.GitHubConfig
import config.UpstreamConfig
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory
import utils.HttpUtil
import kotlin.system.exitProcess

const val GITHUB_API_URL = "https://api.github.com"

class GitHubEntity (private val config: GitHubConfig) {
    private val logger = LoggerFactory.getLogger(javaClass)

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

    fun verifyUser(): Boolean {
        return config.githubID == getAuthUser()
    }

    private fun getOwnFork(url: String): List<JsonObject> {
        val response = HttpUtil().GetRequests().getBody(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }

        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(response)
        @Suppress("UNCHECKED_CAST")
        val repos = parser.parse(stringBuilder) as JsonArray<JsonObject>
        return repos.filter {
            it.obj("owner")?.string("login") == config.githubID
        }
    }

    fun createFork(upstream: UpstreamConfig) {
        val url = "${GITHUB_API_URL}/repos/${upstream.name}/forks"

        val existedFork = getOwnFork(url)
        if (existedFork.isNotEmpty()) {
            logger.info("You have already forked ${upstream.name}." +
                    "Name of the fork: \"${existedFork[0].string("name")}\". " +
                    "Please change the repo_name in configuration file to the name of the fork.")
            exitProcess(1)
        }

        logger.info("Creating fork of ${upstream.name}...")

        val response = HttpUtil().PostRequests().request(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
            contentType(ContentType.Application.Json)
            setBody(
                Klaxon().toJsonString(
                    mapOf(
                        "name" to config.repoName,
                        "default_branch_only" to true
                    )
                )
            )
        }

        logger.debug(response.toString())

        if (response.status.value != 202 || !checkRepo(upstream)) {
            logger.error("Failed to create fork.")
            logger.info("Please check your GitHub token and repo configurations or create a fork of ${upstream.name} manually.")
            exitProcess(1)
        }

        logger.info("Fork created.")
    }

    fun checkRepo(upstream: UpstreamConfig): Boolean {
        val url = "${GITHUB_API_URL}/repos/${config.githubID}/${config.repoName}"

        val response = HttpUtil().GetRequests().getBodyJSON(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }

        if (response.string("name").toString() != config.repoName) {
            logger.error("Repository ${config.repoName} does not exist.")
            return false
        } else {
            logger.trace("Repository ${config.repoName} exists.")
        }

        if (response.boolean("fork") != true) {
            logger.error("Repository ${config.repoName} is not a fork.")
            return false
        } else {
            logger.trace("Repository ${config.repoName} is a fork.")
        }

        if (response.obj("parent")!!.string("full_name").toString() != upstream.name) {
            logger.error("Repository ${config.repoName} is not a fork of ${upstream.name}.")
            return false
        } else {
            logger.trace("Repository ${config.repoName} is a fork of ${upstream.name}.")
        }

        return true
    }
}
