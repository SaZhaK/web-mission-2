package edu.akolomiets.bot.config

import lombok.Data
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@Data
@PropertySource("application.properties")
class BotConfig {

    @Value("\${bot.name}")
    var botName: String? = null

    @Value("\${bot.token}")
    var botToken: String? = null

    @Value("\${hugging_face.token}")
    var huggingFaceToken: String? = null
}