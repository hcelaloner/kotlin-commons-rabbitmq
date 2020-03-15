# Kotlin Commons RabbitMQ

This project aims to provide RabbitMQ abstraction for kotlin applications.
Currently, under active development.

## Usage Examples (Subject to Change)

### Connecting (Creating a Bus)

```kotlin
val bus = RabbitBusFactory.create(
    host = "localhost",
    port = 5672,
    username = "guest",
    password = "guest",
    vHost = "/",
    heartbeatTimeout = Duration.ofSeconds(10),
    channelPoolSize = max(8, Runtime.getRuntime().availableProcessors() * 2)
)
```

### Managing Resources

```kotlin
bus.resourceManager()
    .declareExchange("product.events", ExchangeType.TOPIC) {
        durable(true)
        autoDelete(false)
        passive(false)
    }.declareExchange("campaign.events", ExchangeType.TOPIC) {
        durable(true)
        autoDelete(false)
        passive(false)
    }.declareQueue("product.created") {
        durable(true)
        autoDelete(false)
        passive(false)
        messageTTL(Duration.ofDays(1))
        deadLetterExchange("")
        deadLetterRoutingKey("product.created.deadLetter")
    }
    .initialize()
```

### Publishing a message

```kotlin
data class Sample(val text: String)
```

```kotlin
val message = Sample("Hello, world!")
bus.publish(obj = message)
```

```kotlin
val message = Sample("Hello, world!")
bus.publish(routingKey = "routingKey", obj = message )
```

```kotlin
val message = Sample("Hello, world!")
bus.publish(exchange = "exchange", routingKey = "routingKey", obj = message )
```

```kotlin
val message = Sample("Hello, world!")
bus.publish(exchange = "exchange", routingKey = "routingKey", obj = message, properties = AMQP.BasicProperties())
```

### Publishing a message with confirms

```kotlin
data class Sample(val text: String)
```

```kotlin
val message = Sample("Hello, world!")
bus.publishWithConfirms(obj = message)
```

```kotlin
val message = Sample("Hello, world!")
bus.publishWithConfirms(routingKey = "routingKey", obj = message )
```

```kotlin
val message = Sample("Hello, world!")
bus.publishWithConfirms(exchange = "exchange", routingKey = "routingKey", obj = message )
```

```kotlin
val message = Sample("Hello, world!")
bus.publishWithConfirms(exchange = "exchange", routingKey = "routingKey", obj = message, properties = AMQP.BasicProperties())
```


## Inspiration

Developed based on some ideas/codes of [@oguzhaneren](https://github.com/oguzhaneren) used in his .net codes at my company.