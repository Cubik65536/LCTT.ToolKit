package config

data class UpstreamConfig (
    val name: String,
    val remote: String,
    val branch: String
)
