package nu.westlin.eshop.inventory.internal

import nu.westlin.eshop.common.OrderPlacedEvent
import nu.westlin.eshop.common.logger
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Service

@Service
class FooInventoryService {
    private val logger = logger()

    @ApplicationModuleListener
    fun logOrderPlacedEvent(orderPlacedEvent: OrderPlacedEvent) {
        logger.info("Order placed: $orderPlacedEvent")
    }
}