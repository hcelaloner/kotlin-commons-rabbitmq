package com.hcelaloner.rabbitmq.core

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * Represents a binding definition between an exchange and a queue.
 *
 * @author Hüseyin Celal Öner
 * @since 1.0.0
 */
class QueueBindingDefinition internal constructor(
    val exchange: String,
    val queue: String,
    val routingKey: String = ""
) {

    init {
        require(exchange.isNotBlank()) {
            "Exchange name must not be blank"
        }

        require(queue.isNotBlank()) {
            "Queue name must not be blank"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is QueueBindingDefinition) {
            return false
        }

        return EqualsBuilder()
            .append(exchange, other.exchange)
            .append(routingKey, other.routingKey)
            .append(queue, other.queue)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(exchange)
            .append(routingKey)
            .append(queue)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("exchange", exchange)
            .append("routingKey", routingKey)
            .append("queue", queue)
            .toString()
    }
}