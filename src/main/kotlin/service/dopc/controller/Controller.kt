package service.dopc.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import service.dopc.dto.Output
import service.dopc.service.calculator

@RestController
class Controller {

    @GetMapping("/api/v1/delivery-order-price")
    fun getDeliveryOrderPrice(@RequestParam("venue_slug") venue_slug: String,
                              @RequestParam("cart_value") cart_value: Int,
                              @RequestParam("user_lat") user_lat: Double,
                              @RequestParam("user_lon") user_lon: Double): Output {
        return calculator(venue_slug, cart_value, user_lat, user_lon)
    }
}