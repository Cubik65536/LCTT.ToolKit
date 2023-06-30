import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import entity.ConfigEntity
import org.slf4j.LoggerFactory

class LCTTToolKit: CliktCommand() {
    override fun run() = Unit
    override fun aliases(): Map<String, List<String>> = mapOf(
            "i" to listOf("init"),
    )
}

class Init: CliktCommand(name = "init", help = "Initialize the LCTT ToolKit") {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun run() {
        val config = ConfigEntity().loadConfig()
        logger.info("Initializing the LCTT ToolKit...")
    }
}

fun main(args: Array<String>) = LCTTToolKit()
    .subcommands(Init())
    .main(args)
