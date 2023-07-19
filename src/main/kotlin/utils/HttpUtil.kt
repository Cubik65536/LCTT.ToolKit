package utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import entity.ConfigEntity
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.http.HttpClient

object HttpUtil {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val httpClient = HttpClient(Java) {
        engine {
            // this: JavaHttpConfig
            threadsCount = 8
            pipelining = true
            if (ConfigEntity().loadConfig().proxy.enabled) {
                proxy = ProxyBuilder.http(ConfigEntity().loadConfig().proxy.address)
            }
            protocolVersion = HttpClient.Version.HTTP_2
        }
    }

    object GetRequests {
        fun request(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
            logger.debug("GET $url")
            val httpResponse: HttpResponse
            runBlocking {
                httpResponse = httpClient.get(url, requestBuilder)
            }
            logger.debug("Response: {}", httpResponse)
            return httpResponse
        }

        fun getBody(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): String {
            val httpResponseBody: String
            runBlocking {
                httpResponseBody = request(url, requestBuilder).bodyAsText()
            }
            return httpResponseBody
        }

        fun getBodyJSON(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): JsonObject {
            val parser: Parser = Parser.default()
            val stringBuilder: StringBuilder = StringBuilder(getBody(url, requestBuilder))
            return parser.parse(stringBuilder) as JsonObject
        }
    }

    object PostRequests {
        fun request(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
            logger.debug("POST $url")
            val httpResponse: HttpResponse
            runBlocking {
                httpResponse = httpClient.post(url, requestBuilder)
            }
            logger.debug("Response: {}", httpResponse)
            return httpResponse
        }

        fun getBody(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): String {
            val httpResponseBody: String
            runBlocking {
                httpResponseBody = request(url, requestBuilder).bodyAsText()
            }
            return httpResponseBody
        }

        fun getBodyJSON(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): JsonObject {
            val parser: Parser = Parser.default()
            val stringBuilder: StringBuilder = StringBuilder(getBody(url, requestBuilder))
            return parser.parse(stringBuilder) as JsonObject
        }
    }
}
