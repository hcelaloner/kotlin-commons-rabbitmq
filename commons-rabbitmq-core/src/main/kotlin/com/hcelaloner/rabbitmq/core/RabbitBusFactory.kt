package com.hcelaloner.rabbitmq.core

import java.time.Duration
import kotlin.math.max

class RabbitBusFactory {
    companion object {
        fun create(
            host: String = "localhost",
            port: Int = 5672,
            username: String = "guest",
            password: String = "guest",
            vHost: String = "/",
            heartbeatTimeout: Duration = Duration.ofSeconds(10),
            channelPoolSize: Int = max(8, Runtime.getRuntime().availableProcessors() * 2)
        ): RabbitBus {
            return RabbitBusImpl(
                host,
                port,
                username,
                password,
                vHost,
                heartbeatTimeout,
                channelPoolSize
            )
        }
    }
}