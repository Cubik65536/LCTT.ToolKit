package utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipFile

private const val BUFFER_SIZE = 4096

object FileUtil {
    private val logger = LoggerFactory.getLogger(javaClass)

    // credit for unzip code: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    fun unzip(zipFilePath: String, destDirectory: String) {
        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }

        ZipFile(File(zipFilePath)).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val filePath = destDirectory + File.separator + entry.name
                    if (!entry.isDirectory) {
                        extractFile(input, filePath)
                    } else {
                        val dir = File(filePath)
                        dir.mkdir()
                    }
                }
            }
        }
    }

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
