package utils

import com.beust.klaxon.Klaxon
import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

const val GITHUB_API_URL = "https://api.github.com"

class RepoUtil (private val config: GitHubConfig) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun syncUpstream() {
        logger.info("Syncing upstream...")

        val url = "${GITHUB_API_URL}/repos/${config.githubID}/${config.repoName}/merge-upstream"
        val response = HttpUtil.PostRequests.request(url) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
            contentType(ContentType.Application.Json)
            setBody(
                Klaxon().toJsonString(
                    mapOf(
                        "branch" to "master"
                    )
                )
            )
        }

        when (response.status.value) {
            200 -> logger.info("Successfully synced with the upstream repository.")
            409 -> logger.error("Could not be sync with upstream repository because of a merge conflict.")
            else -> {
                logger.error(
                    "Could not be sync with upstream repository for unknown reason." +
                    " (status code: ${response.status.value})"
                )
            }
        }
    }

    fun downloadArchive() {
        val url = "https://github.com/${config.githubID}/${config.repoName}/zipball/master"
        val path = "tmp/${config.repoName}.zip"
        FileUtil.downloadFile(url, path) {
            accept(ContentType("application", "vnd.github+json"))
            bearerAuth(config.githubToken)
            headers {
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }
    }

    fun unzipArchive() {
        downloadArchive()
        val path = "tmp/${config.repoName}.zip"
        val dest = "tmp/${config.repoName}"
        FileUtil.unzip(path, dest)
    }
}
