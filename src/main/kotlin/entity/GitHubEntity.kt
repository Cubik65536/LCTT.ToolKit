package entity

import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory
import utils.HttpUtil
import kotlin.system.exitProcess

const val GITHUB_API_URL = "https://api.github.com"

class GitHubEntity (private val config: GitHubConfig) {
    private val logger = LoggerFactory.getLogger(javaClass)

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

    fun checkRepo() {
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
            // TODO: Create a fork of LCTT/TranslateProject with the same name as config.repoName
            return
        } else {
            logger.trace("Repository ${config.repoName} exists.")
        }

        if (response.boolean("fork") != true) {
            logger.error("Repository ${config.repoName} is not a fork.")
            logger.info("Please check your repository name configuration or fork the LCTT/TranslateProject.")
            exitProcess(1)
        } else {
            logger.trace("Repository ${config.repoName} is a fork.")
        }

        if (response.obj("parent")!!.string("full_name").toString() != "LCTT/TranslateProject") {
            logger.error("Repository ${config.repoName} is not a fork of LCTT/TranslateProject.")
            logger.info("Please check your repository name configuration or fork the LCTT/TranslateProject.")
            exitProcess(1)
        } else {
            logger.trace("Repository ${config.repoName} is a fork of LCTT/TranslateProject.")
        }

        logger.info("Repository configuration verified.")
    }
}
