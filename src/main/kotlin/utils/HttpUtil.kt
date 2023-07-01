package utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.http.HttpClient

class HttpUtil {
    private val httpClient = HttpClient(Java) {
        engine {
            // this: JavaHttpConfig
            threadsCount = 8
            pipelining = true
            protocolVersion = HttpClient.Version.HTTP_2
        }
    }

    suspend fun get(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): String {
        val httpResponse: HttpResponse = httpClient.get(url, requestBuilder)
        return httpResponse.bodyAsText()
    }

    suspend fun getJSON(url: String, requestBuilder: HttpRequestBuilder.() -> Unit = {}): JsonObject {
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(get(url, requestBuilder))
        return parser.parse(stringBuilder) as JsonObject
    }
}
