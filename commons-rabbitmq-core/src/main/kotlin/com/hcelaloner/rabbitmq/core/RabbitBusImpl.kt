package com.hcelaloner.rabbitmq.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import reactor.core.publisher.Mono
import reactor.rabbitmq.*
import java.time.Duration
import kotlin.math.max

internal class RabbitBusImpl(
    host: String = "localhost",
    port: Int = 5672,
    username: String = "guest",
    password: String = "guest",
    virtualHost: String = "/",
    heartbeatTimeout: Duration = Duration.ofSeconds(10),
    channelPoolSize: Int = max(8, Runtime.getRuntime().availableProcessors() * 2)
) : RabbitBus {
    private val publisherConnection: Connection
    private val publisherChannelPool: ChannelPool
    private val consumerConnection: Connection

    private val sender: Sender
    private val receiver: Receiver
    private val objectMapper = ObjectMapper() // TODO: Move to constructor params

    init {
        val connectionFactory = createConnectionFactory(host, port, username, password, virtualHost, heartbeatTimeout)
        publisherConnection = connectionFactory.newConnection("publisher-connection")
        publisherChannelPool = ChannelPoolFactory.createChannelPool(
            Mono.just(publisherConnection),
            ChannelPoolOptions().maxCacheSize(channelPoolSize)
        )
        sender = RabbitFlux.createSender(
            SenderOptions().connectionMono(Mono.just(publisherConnection)).channelPool(publisherChannelPool)
        )

        consumerConnection = connectionFactory.newConnection("consumer-connection")
        receiver = RabbitFlux.createReceiver(
            ReceiverOptions().connectionMono(Mono.just(consumerConnection))
        )
    }

    private fun createConnectionFactory(
        host: String,
        port: Int,
        username: String,
        password: String,
        virtualHost: String,
        heartbeatTimeout: Duration
    ): ConnectionFactory {
        val cf = ConnectionFactory()
        cf.isAutomaticRecoveryEnabled = true
        cf.host = host
        cf.port = port
        cf.username = username
        cf.password = password
        cf.virtualHost = virtualHost
        cf.requestedHeartbeat = heartbeatTimeout.toMillis().toInt()
        cf.isTopologyRecoveryEnabled = true
        cf.useNio()
        return cf
    }

    override fun resourceManager(): RabbitResourceManager {
        return RabbitResourceManagerImpl(sender)
    }

    override fun publish(
        exchange: String,
        routingKey: String,
        messageContent: Any,
        properties: AMQP.BasicProperties?
    ): Mono<Void> {
        val outboundMessagePublisher = Mono.fromCallable { createMessage(exchange, routingKey, messageContent, properties) }
        return sender.send(outboundMessagePublisher)
    }

    override fun publishWithConfirms(
        exchange: String,
        routingKey: String,
        messageContent: Any,
        properties: AMQP.BasicProperties?
    ): Mono<Boolean> {
        val outboundMessagePublisher = Mono.fromCallable { createMessage(exchange, routingKey, messageContent, properties) }
        val sendOptions = SendOptions().channelPool(publisherChannelPool)
        return sender.sendWithPublishConfirms(outboundMessagePublisher, sendOptions)
            .map { it.isAck }
            .singleOrEmpty()
    }

    private fun createMessage(
        exchange: String,
        routingKey: String,
        messageContent: Any,
        properties: AMQP.BasicProperties? = null
    ): OutboundMessage {
        return OutboundMessage(exchange, routingKey, properties, objectMapper.writeValueAsBytes(messageContent))
    }
}