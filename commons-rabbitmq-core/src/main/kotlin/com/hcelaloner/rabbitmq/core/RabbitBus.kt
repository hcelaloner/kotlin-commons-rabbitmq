package com.hcelaloner.rabbitmq.core

import com.rabbitmq.client.AMQP
import reactor.core.publisher.Mono

interface RabbitBus {

    // Resource manager
    fun resourceManager(): RabbitResourceManager

    // Publish operations
    @JvmDefault
    fun publish(messageContent: Any): Mono<Void> {
        return publish("", messageContent)
    }

    @JvmDefault
    fun publish(routingKey: String, messageContent: Any): Mono<Void> {
        return publish("", "", messageContent)
    }

    fun publish(exchange: String, routingKey: String, messageContent: Any, properties: AMQP.BasicProperties? = null): Mono<Void>

    @JvmDefault
    fun publishWithConfirms(messageContent: Any): Mono<Boolean> {
        return publishWithConfirms("", messageContent)
    }

    @JvmDefault
    fun publishWithConfirms(routingKey: String, messageContent: Any): Mono<Boolean> {
        return publishWithConfirms("", "", messageContent)
    }

    fun publishWithConfirms(
        exchange: String,
        routingKey: String,
        messageContent: Any,
        properties: AMQP.BasicProperties? = null
    ): Mono<Boolean>
}