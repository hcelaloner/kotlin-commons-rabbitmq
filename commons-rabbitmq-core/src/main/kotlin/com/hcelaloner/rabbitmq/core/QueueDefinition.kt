package com.hcelaloner.rabbitmq.core

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.time.Duration

class QueueDefinition internal constructor(
    val name: String,
    durable: Boolean = true,
    passive: Boolean = false,
    exclusive: Boolean = false,
    autoDelete: Boolean = false,
    val arguments: MutableMap<String, Any> = mutableMapOf()
) {
    var autoDelete = autoDelete
        private set

    var exclusive = exclusive
        private set

    var passive = passive
        private set

    var durable = durable
        private set

    init {
        require(name.isNotBlank()) {
            "Queue name must not be blank"
        }
    }

    /**
     * Sets the passive flag.
     *
     * If set, the server will reply with Declare-Ok if the queue already exists with the same name, and raise an
     * error if not. The client can use this to check whether a queue exists without modifying the server state.
     *
     * @param passive the passive flag
     * @return the queue definition
     */
    fun passive(passive: Boolean): QueueDefinition {
        this.passive = passive
        return this
    }

    /**
     * Sets the durable flag.
     *
     * If set when creating a new queue, the queue will be marked as durable. Durable queues remain active when a
     * server restarts. Non-durable queues (transient queues) are purged if/when a server restarts. Note that
     * durable queues do not necessarily hold persistent messages, although it does not make sense to send
     * persistent messages to a transient queue.
     *
     * @param durable the durable flag
     * @return the queue definition
     */
    fun durable(durable: Boolean): QueueDefinition {
        this.durable = durable
        return this
    }

    /**
     * Sets the exclusive flag.
     *
     *  Exclusive queues may only be accessed by the current connection, and are deleted when that connection closes.
     *  Passive declaration of an exclusive queue by other connections are not allowed.
     *
     * @param exclusive the exclusive flag
     * @return the queue definition
     */
    fun exclusive(exclusive: Boolean): QueueDefinition {
        this.exclusive = exclusive
        return this
    }

    /**
     * Sets the autoDelete flag.
     *
     * If set, the queue is deleted when all consumers have finished using it. The last consumer can be cancelled
     * either explicitly or because its channel is closed. If there was no consumer ever on the queue, it won't be
     * deleted.
     *
     * @param autoDelete the autoDelete flag
     * @return the queue definition
     */
    fun autoDelete(autoDelete: Boolean): QueueDefinition {
        this.autoDelete = autoDelete
        return this
    }

    /**
     * Adds the specified argument to queue arguments
     *
     * @param key the argument key
     * @param value the argument value
     * @return the queue definition
     */
    fun argument(key: String, value: Any): QueueDefinition {
        this.arguments[key] = value
        return this
    }

    /**
     * Adds all specified arguments to queue arguments
     *
     * @param arguments the arguments map
     * @return the queue definition
     */
    fun arguments(arguments: Map<String, Any>): QueueDefinition {
        this.arguments.putAll(arguments)
        return this
    }

    /**
     * Defines time-to-live for the messages in the queue
     *
     * @see <a href="https://www.rabbitmq.com/ttl.html#message-ttl-using-x-args">Define Message TTL for Queues Using x-arguments During Declaration</a>
     * @param duration the message ttl duration (a positive duration, excluding zero)
     * @return the queue definition
     */
    fun messageTTL(duration: Duration): QueueDefinition {
        require(!duration.isNegative) {
            "Message ttl duration must not be negative"
        }
        require(!duration.isZero) {
            "Message ttl duration must not be zero"
        }

        argument("x-message-ttl", duration.toMillis())
        return this
    }

    /**
     * Defines time-to-live for the queue
     *
     * @see <a href="https://www.rabbitmq.com/ttl.html#queue-ttl-using-x-args">Define Queue TTL for Queues Using x-arguments During Declaration</a>
     * @param duration the queue ttl duration (a positive duration, excluding zero)
     * @return the queue definition
     */
    fun queueTTL(duration: Duration): QueueDefinition {
        require(!duration.isNegative) {
            "Message ttl duration must not be negative"
        }
        require(!duration.isZero) {
            "Message ttl duration must not be zero"
        }

        argument("x-expires", duration.toMillis())
        return this
    }

    /**
     * Limits the maximum length of a queue to a set number of messages
     *
     * @see <a href="https://www.rabbitmq.com/maxlength.html#definition-using-x-args">Define Max Queue Length Using x-arguments During Declaration</a>
     * @param maximumNumberOfMessages maximum number of messages allowed (a non-negative integer value)
     * @return the queue definition
     */
    fun maximumLengthOfQueueByMessageCounts(maximumNumberOfMessages: Int): QueueDefinition {
        require(maximumNumberOfMessages > 0) {
            "Maximum number of messages must be a non-negative integer value"
        }

        argument("x-max-length", maximumNumberOfMessages)
        return this
    }

    /**
     * Limits the maximum length of a queue to a set number of bytes (the total of all message body lengths,
     * ignoring message properties and any overheads)
     *
     * @see <a href="https://www.rabbitmq.com/maxlength.html#definition-using-x-args">Define Max Queue Length Using x-arguments During Declaration</a>
     * @param maximumNumberOfBytes maximum number of bytes allowed (a non-negative integer value)
     * @return the queue definition
     */
    fun maximumLengthOfQueueByMessageSizes(maximumNumberOfBytes: Int): QueueDefinition {
        require(maximumNumberOfBytes > 0) {
            "Maximum number of bytes must be a non-negative integer value"
        }

        argument("x-max-length-bytes", maximumNumberOfBytes)
        return this
    }

    /**
     * Sets the dead letter exchange for the queue.
     *
     * Note that the exchange does not have to be declared when the queue is declared, but it should exist by the
     * time messages need to be dead-lettered; if it is missing then, the messages will be silently dropped.
     *
     * @see <a href="https://www.rabbitmq.com/dlx.html#overview">Dead Letter Exchanges</a>
     * @see <a href="https://www.rabbitmq.com/dlx.html#using-optional-queue-arguments">Configuration Using Optional Queue Arguments</a>
     * @param deadLetterExchange the dead letter exchange
     * @return the queue definition
     */
    fun deadLetterExchange(deadLetterExchange: String): QueueDefinition {
        argument("x-dead-letter-exchange", deadLetterExchange)
        return this
    }

    /**
     * Sets the routing key to be used when dead-lettering messages.
     *
     * If this is not set, the message's own routing keys will be used.
     *
     * @see <a href="https://www.rabbitmq.com/dlx.html#overview">Dead Letter Exchanges</a>
     * @see <a href="https://www.rabbitmq.com/dlx.html#using-optional-queue-arguments">Configuration Using Optional Queue Arguments</a>
     * @param deadLetterRoutingKey the dead letter routing key
     * @return the queue definition
     */
    fun deadLetterRoutingKey(deadLetterRoutingKey: String): QueueDefinition {
        argument("x-dead-letter-routing-key", deadLetterRoutingKey)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is QueueDefinition) {
            return false
        }

        return EqualsBuilder()
            .append(name, other.name)
            .append(durable, other.durable)
            .append(passive, other.passive)
            .append(exclusive, other.exclusive)
            .append(autoDelete, other.autoDelete)
            .append(arguments, other.arguments)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(name)
            .append(durable)
            .append(passive)
            .append(exclusive)
            .append(autoDelete)
            .append(arguments)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("name", name)
            .append("durable", durable)
            .append("passive", passive)
            .append("exclusive", exclusive)
            .append("autoDelete", autoDelete)
            .append("arguments", arguments)
            .toString()
    }
}
