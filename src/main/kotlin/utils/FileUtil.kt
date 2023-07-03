package utils

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File

class FileUtil {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun downloadFile(urlString: String, pathname: String, downloadDescription: String) {
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
            val httpResponse: HttpResponse = httpClient.get(urlString) {
                onDownload { bytesSentTotal, contentLength ->
                    logger.info("$downloadDescription: $bytesSentTotal/$contentLength")
                }
            }
            val responseBody: ByteArray = httpResponse.body()
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.writeBytes(responseBody)
        }
    }
}
