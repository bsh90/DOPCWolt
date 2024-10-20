package service.dopc.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import service.dopc.dto.Delivery
import service.dopc.dto.DistanceRange
import service.dopc.dto.Output
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun calculator(venue_slug: String,
               cart_value: Int,
               user_lat: Double,
               user_lon: Double): ResponseEntity<out Any> {

    // validation
    if (cart_value < 0) {
        return ResponseEntity("cart value can not be negative value.", HttpStatus.BAD_REQUEST)
    }
    if (!venue_slug.equals("home-assignment-venue-helsinki")
            && !venue_slug.equals("home-assignment-venue-stockholm")
            && !venue_slug.equals("home-assignment-venue-berlin")) {
        return ResponseEntity("Venue slug can just be from the three available options.", HttpStatus.BAD_REQUEST)
    }
    // fetching data
    val rest_template = RestTemplate()
    val mapper = ObjectMapper()

    val coordinates: JsonNode = getCoordinates(mapper, venue_slug, rest_template)
    val longitude: Double = coordinates.get(0).asDouble()
    val latitude: Double = coordinates.get(1).asDouble()

    val delivery_specs: JsonNode = getDeliverySpecs(mapper, venue_slug, rest_template)
    val order_minimum_no_surcharge: Int = delivery_specs.get("order_minimum_no_surcharge").asInt()
    val base_price: Int = delivery_specs.get("delivery_pricing").get("base_price").asInt()
    val distance_ranges: List<DistanceRange> = getDistanceRanges(delivery_specs, mapper)

    // Logic
    val small_order_surcharge : Int = abs(order_minimum_no_surcharge - cart_value)
    val delivery_distance : Int = sqrt((longitude - user_lon).pow(2.0) + (latitude - user_lat).pow(2.0)).toInt()
    val distance_range : List<DistanceRange> = distance_ranges.filter{ range -> delivery_distance >= range.min
                                                                                && delivery_distance < range.max}
    if (distance_range.isEmpty()) {
        return ResponseEntity("Distance range result is empty. Please change user_lat or user_lon.", HttpStatus.BAD_REQUEST)
    }
    val delivery_fee: Int =  base_price + distance_range[0].a + distance_range[0].b * (delivery_distance / 10)
    val total_price: Int = cart_value + small_order_surcharge + delivery_fee

    val output = Output(total_price, small_order_surcharge, cart_value, Delivery(delivery_fee, delivery_distance))
    return ResponseEntity(output, HttpStatus.OK)
}

private fun getStaticJson(venue_slug: String,
                  restTemplate: RestTemplate): String? {
    val static_url = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/static"
    return restTemplate.getForObject(static_url, String::class.java)
}
private fun getDynamicJson(venue_slug: String,
                   restTemplate: RestTemplate): String? {
    val dynamic_url = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/dynamic"
    return restTemplate.getForObject(dynamic_url, String::class.java)
}

private fun getCoordinates(mapper: ObjectMapper,
                   venue_slug: String,
                   restTemplate: RestTemplate): JsonNode {
    return mapper.readTree(getStaticJson(venue_slug, restTemplate)).get("venue_raw").get("location").get("coordinates")
}

private fun getDeliverySpecs(mapper: ObjectMapper,
                     venue_slug: String,
                     restTemplate: RestTemplate): JsonNode {
    return mapper.readTree(getDynamicJson(venue_slug, restTemplate)).get("venue_raw").get("delivery_specs")
}

private fun getDistanceRanges(deliverySpecs: JsonNode,
                      mapper: ObjectMapper): List<DistanceRange> {
    val distance_ranges_node: JsonNode = deliverySpecs.get("delivery_pricing").get("distance_ranges")
    return mapper.convertValue(distance_ranges_node, object : TypeReference<List<DistanceRange>>() {})
}