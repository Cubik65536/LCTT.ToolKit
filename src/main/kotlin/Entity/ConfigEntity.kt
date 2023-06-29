package Entity

import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess


class ConfigEntity {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun loadConfig() {
        val pwd = File(this::class.java.protectionDomain.codeSource.location.toURI()).path
        val confFile = File("${pwd.substring(0, pwd.lastIndexOf("/"))}/../conf/config.yaml")
        logger.info("Loading config from $confFile...")
        if (!confFile.exists()) {
            logger.error("Config file not found!")
            logger.info("Creating config file...")
            val defaultConfig = this::class.java.getResourceAsStream("/conf/config.yaml")
            confFile.parentFile.mkdirs()
            confFile.createNewFile()
            defaultConfig.copyTo(confFile.outputStream())
            logger.info("New config file created! Please edit it and run the program again.")
            exitProcess(1)
        }
    }
}
