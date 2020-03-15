package com.hcelaloner.rabbitmq.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ExchangeDefinitionTest {

    @ParameterizedTest
    @EnumSource(ExchangeType::class)
    internal fun `it should create a non-autodelete, durable exchange with no extra arguments when called with default params`(
        type: ExchangeType
    ) {
        val exchangeDefinition = ExchangeDefinition("name", type)

        assertThat(exchangeDefinition.name).isEqualTo("name")
        assertThat(exchangeDefinition.type).isEqualTo(type)
        assertThat(exchangeDefinition.durable).isEqualTo(true)
        assertThat(exchangeDefinition.autoDelete).isEqualTo(false)
        assertThat(exchangeDefinition.passive).isEqualTo(false)
        assertThat(exchangeDefinition.arguments).isEmpty()
    }
}