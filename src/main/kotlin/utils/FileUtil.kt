package utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File

class FileUtil {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun downloadFile(
        urlString: String,
        pathname: String,
        requestBuilder: HttpRequestBuilder.() -> Unit = {}
    ) {
        logger.info("Downloading $urlString to $pathname")

        val httpClient = HttpClient(Java) {
            engine {
                // this: JavaHttpConfig
                threadsCount = 8
                pipelining = true
                protocolVersion = java.net.http.HttpClient.Version.HTTP_2
            }
        }

        val file = File(pathname)

        runBlocking {
            val httpResponse: HttpResponse = httpClient.get(urlString, requestBuilder)
            val responseBody: ByteArray = httpResponse.body()
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.writeBytes(responseBody)
        }
    }
}
