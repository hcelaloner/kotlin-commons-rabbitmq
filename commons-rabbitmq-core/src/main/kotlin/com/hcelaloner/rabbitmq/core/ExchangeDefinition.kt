package com.hcelaloner.rabbitmq.core

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

/**
 * Represents an exchange definition and its properties.
 *
 * @author Hüseyin Celal Öner
 * @since 1.0.0
 */
class ExchangeDefinition internal constructor(
    val name: String,
    val type: ExchangeType,
    durable: Boolean = true,
    autoDelete: Boolean = false,
    passive: Boolean = false,
    val arguments: MutableMap<String, Any> = mutableMapOf()
) {
    var durable = durable
        private set

    var autoDelete = autoDelete
        private set

    var passive = passive
        private set

    init {
        require(name.isNotBlank()) {
            "Exchange name must not be blank"
        }
    }

    /**
     * Sets the durable flag.
     *
     * If set when creating a new exchange, the exchange will be marked as durable. Durable exchanges remain active
     * when a server restarts. Non-durable exchanges (transient exchanges) are purged if/when a server restarts.
     *
     * @param durable the durable flag
     * @return the exchange definition
     */
    fun durable(durable: Boolean): ExchangeDefinition {
        this.durable = durable
        return this
    }

    /**
     * Sets the autoDelete flag.
     *
     * If set, the exchange is deleted when all queues have finished using it.
     *
     * @param autoDelete the autoDelete flag
     * @return the exchange definition
     */
    fun autoDelete(autoDelete: Boolean): ExchangeDefinition {
        this.autoDelete = autoDelete
        return this
    }

    /**
     * Sets the passive flag.
     *
     * If set, the server will reply with Declare-Ok if the exchange already exists with the same name, and raise
     * an error if not. The client can use this to check whether an exchange exists without modifying the server state.
     *
     * @param passive the passive flag
     * @return the exchange definition
     */
    fun passive(passive: Boolean): ExchangeDefinition {
        this.passive = passive
        return this
    }

    /**
     * Adds the specified argument to exchange arguments
     *
     * @param key the argument key
     * @param value the argument value
     * @return the exchange definition
     */
    fun argument(key: String, value: Any): ExchangeDefinition {
        this.arguments[key] = value
        return this
    }

    /**
     * Adds all specified arguments to exchange arguments
     *
     * @param arguments the arguments map
     * @return the exchange definition
     */
    fun arguments(arguments: Map<String, Any>): ExchangeDefinition {
        this.arguments.putAll(arguments)
        return this
    }

    /**
     * Defines the alternate exchange for this exchange definition
     *
     * @see <a href="https://www.rabbitmq.com/ae.html">Alternate Exchanges</a>
     * @param exchange the name of alternate exchange
     * @return the exchange definition
     */
    fun alternate(exchange: String): ExchangeDefinition {
        return argument("alternate-exchange", exchange)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is ExchangeDefinition) {
            return false
        }

        return EqualsBuilder()
            .append(name, other.name)
            .append(type, other.type)
            .append(durable, other.durable)
            .append(autoDelete, other.autoDelete)
            .append(passive, other.passive)
            .append(arguments, other.arguments)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(name)
            .append(type)
            .append(durable)
            .append(autoDelete)
            .append(passive)
            .append(arguments)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("name", name)
            .append("type", type)
            .append("durable", durable)
            .append("autoDelete", autoDelete)
            .append("passive", passive)
            .append("arguments", arguments)
            .toString()
    }
}