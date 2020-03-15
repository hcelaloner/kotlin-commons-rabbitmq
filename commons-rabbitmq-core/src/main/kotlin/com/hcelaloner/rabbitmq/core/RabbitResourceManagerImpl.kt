package com.hcelaloner.rabbitmq.core

import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.rabbitmq.BindingSpecification
import reactor.rabbitmq.ExchangeSpecification
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Sender
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

class RabbitResourceManagerImpl(private val sender: Sender) : RabbitResourceManager {
    private val logger = LoggerFactory.getLogger(RabbitResourceManagerImpl::class.java)

    private val exchangeDefinitions: MutableList<ExchangeDefinition> = LinkedList()
    private val queueDefinitions: MutableList<QueueDefinition> = LinkedList()
    private val queueBindingDefinitions: MutableList<QueueBindingDefinition> = LinkedList()

    // Exchange operations

    override fun declareExchange(
        name: String,
        type: ExchangeType,
        exchangeDefinitionConfigurer: (ExchangeDefinition.() -> Unit)?
    ): RabbitResourceManager {
        val exchangeDefinition = ExchangeDefinition(name, type)
        exchangeDefinitionConfigurer?.invoke(exchangeDefinition)
        exchangeDefinitions.add(exchangeDefinition)
        return this
    }

    override fun deleteExchange(name: String, ifUnused: Boolean): RabbitResourceManager {
        TODO("Not yet implemented")
    }

    // Queue operations

    override fun declareQueue(
        name: String,
        queueDefinitionConfigurer: (QueueDefinition.() -> Unit)?
    ): RabbitResourceManager {
        val queueDefinition = QueueDefinition(name)
        queueDefinitionConfigurer?.invoke(queueDefinition)
        queueDefinitions.add(queueDefinition)
        return this
    }

    override fun deleteQueue(name: String, ifUnused: Boolean, ifEmpty: Boolean): RabbitResourceManager {
        TODO("Not yet implemented")
    }

    override fun initialize(): Mono<Void> {
        return declareQueueBindings().delayUntil {
            declareQueues().delayUntil {
                declareExchanges()
            }
        }
    }

    private fun declareExchanges(): Mono<Void> {
        if (exchangeDefinitions.isEmpty() && logger.isDebugEnabled) {
            logger.debug("No exchange found to declare.")
        }

        return Flux.fromIterable(exchangeDefinitions)
            .distinct()
            .parallel()
            .runOn(Schedulers.parallel())
            .doOnNext { exchangeDefinition ->
                if (logger.isDebugEnabled) {
                    logger.debug("Will declare an exchange based on exchangeDefinition:$exchangeDefinition")
                }
            }
            .map { exchangeDefinition ->
                sender.declareExchange(toExchangeSpecification(exchangeDefinition))
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                    .doOnNext { logger.debug("Declared an exchange based on exchangeDefinition:$exchangeDefinition") }
            }
            .then()
    }

    private fun declareQueues(): Mono<Void> {
        if (queueDefinitions.isEmpty() && logger.isDebugEnabled) {
            logger.debug("No queue found to declare.")
        }

        return Flux.fromIterable(queueDefinitions)
            .distinct()
            .parallel()
            .runOn(Schedulers.parallel())
            .doOnNext { queueDefinition ->
                if (logger.isDebugEnabled) {
                    logger.debug("Will declare a queue based on queueDefinition:$queueDefinition")
                }
            }
            .map { queueDefinition ->
                sender.declareQueue(toQueueSpecification(queueDefinition))
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                    .doOnNext { logger.debug("Declared a queue based on queueDefinition:$queueDefinition") }
            }
            .then()
    }

    private fun declareQueueBindings(): Mono<Void> {
        if (queueBindingDefinitions.isEmpty() && logger.isDebugEnabled) {
            logger.debug("No queue bindings found to declare.")
        }

        return Flux.fromIterable(queueBindingDefinitions)
            .distinct()
            .parallel()
            .runOn(Schedulers.parallel())
            .doOnNext { queueBindingDefinition ->
                if (logger.isDebugEnabled) {
                    logger.debug("Will declare a queue binding based on queueBindingDefinition:$queueBindingDefinition")
                }
            }
            .map { queueBindingDefinition ->
                sender.bindQueue(toBindingDefinition(queueBindingDefinition))
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                    .doOnNext { logger.debug("Declared a queue binding based on queueBindingDefinition:$queueBindingDefinition") }
            }
            .then()
    }

    private fun toExchangeSpecification(exchangeDefinition: ExchangeDefinition): ExchangeSpecification {
        return ExchangeSpecification.exchange(exchangeDefinition.name)
            .type(exchangeDefinition.type.type)
            .durable(exchangeDefinition.durable)
            .autoDelete(exchangeDefinition.autoDelete)
            .passive(exchangeDefinition.passive)
            .arguments(exchangeDefinition.arguments)
    }

    private fun toQueueSpecification(queueDefinition: QueueDefinition): QueueSpecification {
        return QueueSpecification.queue(queueDefinition.name)
            .durable(queueDefinition.durable)
            .passive(queueDefinition.passive)
            .exclusive(queueDefinition.exclusive)
            .autoDelete(queueDefinition.autoDelete)
            .arguments(queueDefinition.arguments)
    }

    private fun toBindingDefinition(queueBindingDefinition: QueueBindingDefinition): BindingSpecification {
        return BindingSpecification.queueBinding(
            queueBindingDefinition.exchange,
            queueBindingDefinition.queue,
            queueBindingDefinition.routingKey
        )
    }
}