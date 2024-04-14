package edu.akolomiets.bot

import edu.akolomiets.bot.config.BotConfig
import edu.akolomiets.bot.request.ImageRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.ByteArrayInputStream


@Component
class TelegramBot constructor(
    private val botConfig: BotConfig
) : TelegramLongPollingBot() {

    override fun getBotUsername(): String {
        return botConfig.botName!!
    }

    override fun getBotToken(): String {
        return botConfig.botToken!!
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val messageText = update.message.text
            val chatId = update.message.chatId
            when (messageText) {
                "/start" -> startCommandReceived(chatId, update.message.chat.firstName)
                else -> {
                    generateCommandReceived(chatId, messageText)
                }
            }
        }
    }

    private fun generateCommandReceived(chatId: Long, description: String) {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${botConfig.huggingFaceToken}")
        val request = HttpEntity(ImageRequest(description), headers)

        val imageBytes = REST_TEMPLATE.postForObject(HUGGING_FACE_URL, request, ByteArray::class.java)
        val imageName = "${description.replace(" ", "_")}.jpg"

        val inputStream = ByteArrayInputStream(imageBytes)

        val message = SendPhoto()
        message.chatId = chatId.toString()
        message.caption = "Here is your image for '$description'"
        message.photo = InputFile(inputStream, imageName)

        this.execute(message)
    }

    private fun startCommandReceived(chatId: Long, name: String) {
        val answer = """
            Hi, $name, nice to meet you!
            """.trimIndent()

        val sendMessage = SendMessage()
        sendMessage.chatId = chatId.toString()
        sendMessage.text = answer

        execute(sendMessage)
    }

    companion object {
        private val REST_TEMPLATE = RestTemplate()

        private const val HUGGING_FACE_URL =
            "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-xl-base-1.0"
    }
}