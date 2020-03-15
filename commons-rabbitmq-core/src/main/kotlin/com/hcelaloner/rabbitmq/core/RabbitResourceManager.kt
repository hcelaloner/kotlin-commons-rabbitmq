package com.hcelaloner.rabbitmq.core

import reactor.core.publisher.Mono

/**
 * Defines an interface to manage resources (exchange, binding, and queue)
 */
interface RabbitResourceManager {

    // Exchange operations

    /**
     * Declares an exchange (Creates an exchange if needed).
     *
     * @param name the name of the exchange.
     * @param type the exchange type.
     * @param exchangeDefinitionConfigurer the callback that allows to modify set of arguments for the exchange.
     * @since 1.0.0
     */
    fun declareExchange(
        name: String,
        type: ExchangeType,
        exchangeDefinitionConfigurer: (ExchangeDefinition.() -> Unit)? = null
    ): RabbitResourceManager

    /**
     * Deletes an exchange.
     *
     * @param name the name of the exchange.
     * @param ifUnused when set to true, the exchange will be deleted if it is unused.
     * @since 1.0.0
     */
    fun deleteExchange(
        name: String,
        ifUnused: Boolean = false
    ): RabbitResourceManager

    // Queue operations

    /**
     * Declares a queue (Creates a queue if needed).
     *
     * @param name the name of the queue.
     * @param queueDefinitionConfigurer the callback that allows to modify set of arguments for the queue.
     * @since 1.0.0
     */
    fun declareQueue(
        name: String,
        queueDefinitionConfigurer: (QueueDefinition.() -> Unit)? = null
    ): RabbitResourceManager

    /**
     * Deletes a queue.
     *
     * @param name the name of the queue.
     * @param ifUnused when set to true, the v will be deleted if it is unused.
     * @param ifEmpty when set to true, the queue will be deleted if it is empty.
     * @since 1.0.0
     */
    fun deleteQueue(
        name: String,
        ifUnused: Boolean = false,
        ifEmpty: Boolean = false
    ): RabbitResourceManager

    fun initialize(): Mono<Void>
}