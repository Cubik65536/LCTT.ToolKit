package utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.http.HttpClient

class HttpUtil {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val httpClient = HttpClient(Java) {
        engine {
            // this: JavaHttpConfig
            threadsCount = 8
            pipelining = true
            protocolVersion = HttpClient.Version.HTTP_2
        }
    }

    inner class GetRequests {
        fun getBody(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): String {
            logger.debug("GET $url")
            val httpResponseBody: String
            runBlocking {
                httpResponseBody = httpClient.get(url, requestBuilder).bodyAsText()
            }
            logger.debug("Response: $httpResponseBody")
            return httpResponseBody
        }

        fun getBodyJSON(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): JsonObject {
            val parser: Parser = Parser.default()
            val stringBuilder: StringBuilder = StringBuilder(getBody(url, requestBuilder))
            return parser.parse(stringBuilder) as JsonObject
        }
    }

    inner class PostRequests {
        fun postBody(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): String {
            logger.debug("POST $url")
            val httpResponseBody: String
            runBlocking {
                httpResponseBody = httpClient.post(url, requestBuilder).bodyAsText()
            }
            logger.debug("Response: $httpResponseBody")
            return httpResponseBody
        }

        fun postBodyJSON(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): JsonObject {
            val parser: Parser = Parser.default()
            val stringBuilder: StringBuilder = StringBuilder(postBody(url, requestBuilder))
            return parser.parse(stringBuilder) as JsonObject
        }
    }
}
