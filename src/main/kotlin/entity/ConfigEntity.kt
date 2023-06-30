package entity

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.InputStreamPropertySource
import config.*
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess


class ConfigEntity {
    private val logger = LoggerFactory.getLogger(javaClass)

    data class Config (
        val version: String,
        val github: GithubConfig,
        val editor: String,
        val category: List<String>,
        val upstream: UpstreamConfig,
        val git: GitConfig
    )

    fun loadConfig(): Config {
        val pwd = File(this::class.java.protectionDomain.codeSource.location.toURI()).parentFile.parentFile.path
        val defaultConfig = this::class.java.getResourceAsStream("/conf/config.yaml")
        val configFilePath = "$pwd/conf/config.yaml"
        val configFile = File(configFilePath)

        logger.info("Loading config from $configFilePath...")

        if (!configFile.exists()) {
            logger.error("Config file not found!")
            logger.info("Creating config file...")
            configFile.parentFile.mkdirs()
            defaultConfig!!.copyTo(configFile.outputStream())
            logger.info("New config file created! Please edit it and run the program again.")
            exitProcess(1)
        }

        val default = ConfigLoaderBuilder.default()
                      .addPropertySource(InputStreamPropertySource(defaultConfig!!, "yml", "default"))
                      .build()
                      .loadConfigOrThrow<Config>()
        val user = ConfigLoader().loadConfigOrThrow<Config>(configFilePath)

        if (default.version != user.version) {
            logger.error("Config version mismatch!")
            logger.info("Please backup your config file and delete it," +
                        "then run the program again to generate a new config file.")
            exitProcess(1)
        }

        return user
    }
}
