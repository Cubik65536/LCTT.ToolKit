import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import entity.ConfigEntity
import entity.GitHubEntity
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

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
        logger.info("Verifying the GitHub account...")
        val gitHubEntity = GitHubEntity(config.github)
        if (gitHubEntity.verifyUser()) {
            logger.info("GitHub account verified.")
        } else {
            logger.error("GitHub account verification failed.")
            logger.error("Please check your GitHub ID and token.")
            exitProcess(1)
        }
        gitHubEntity.checkRepo()
    }
}

fun main(args: Array<String>) = LCTTToolKit()
    .subcommands(Init())
    .main(args)
