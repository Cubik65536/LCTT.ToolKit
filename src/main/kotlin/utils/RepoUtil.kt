package utils

import config.GitHubConfig
import io.ktor.client.request.*
import io.ktor.http.*

class RepoUtil (private val config: GitHubConfig) {
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
}
