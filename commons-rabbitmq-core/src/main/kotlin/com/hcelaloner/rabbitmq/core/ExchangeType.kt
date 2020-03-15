package com.hcelaloner.rabbitmq.core

/**
 * Represents exchange types in RabbitMQ
 *
 * @author Hüseyin Celal Öner
 * @since 1.0.0
 */
enum class ExchangeType(val type: String) {
    DIRECT("direct"),
    TOPIC("topic"),
    FANOUT("fanout"),
    HEADERS("headers"),
}