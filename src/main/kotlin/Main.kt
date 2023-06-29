import Entity.ConfigEntity
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main() {
    val config = ConfigEntity().loadConfig()

    val client = HttpClient(Java) {
        engine {
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }
    }
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}
