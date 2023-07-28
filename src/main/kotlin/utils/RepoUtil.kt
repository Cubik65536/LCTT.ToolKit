package utils

import com.beust.klaxon.Klaxon
import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import kotlin.io.use
import kotlin.system.exitProcess

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
        logger.debug("Downloading Archive...")
        val url = "https://github.com/${config.githubID}/${config.repoName}/zipball/master"
        val path = "tmp/${config.repoName}.zip"
        try {
            FileUtil.downloadFile(url, path) {
                accept(ContentType("application", "vnd.github+json"))
                bearerAuth(config.githubToken)
                headers {
                    append("X-GitHub-Api-Version", "2022-11-28")
                }
            }
        } catch (ioException: IOException) {
            logger.error("Error downloading archive (IOException): $url")
            logger.error(ioException.message)
            exitProcess(1)
        } catch (eofException: EOFException) {
            logger.error("Error downloading archive (EOFException): $url")
            logger.error(eofException.message)
            exitProcess(1)
        }
        logger.debug("Archive Downloaded")
    }

    fun unzipArchive() {
        logger.debug("Unziping Archive...")
        val path = "tmp/${config.repoName}.zip"
        val dest = "tmp/${config.repoName}"
        FileUtil.unzip(path, dest)
        logger.debug("Archive Unziped")
    }

    fun checkArchive() {
        logger.debug("Checking Archive...")
        val path = "tmp/${config.repoName}"
        FileUtil.getAllFilesInDirectory(path).forEach { file ->
            try {
                BufferedReader(FileReader(file)).use { reader ->
                    logger.trace("Checking file: ${file.absolutePath}")
                    reader.lines().forEach { line ->
                        if (line.startsWith("via:")) {
                            logger.trace("Found via: in ${file.absolutePath}")
                            logger.trace("Line: ${line.substring(4, line.length).trim()}")
                        }
                    }
                }
            } catch (ioException: IOException) {
                logger.error("Error reading file: ${file.absolutePath}")
                logger.error(ioException.message)
            }
        }
        logger.debug("Archive Checked")
    }
}
