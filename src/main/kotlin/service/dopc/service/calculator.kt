package service.dopc.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.HttpStatusCodeException
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
               user_lon: Double): Output {

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
        throw HttpServerErrorException(HttpStatus.BAD_REQUEST)
    }
    val delivery_fee =  base_price + distance_range[0].a + distance_range[0].b * (delivery_distance / 10)
    val total_price = cart_value + small_order_surcharge + delivery_fee

    return Output(total_price, small_order_surcharge, cart_value, Delivery(delivery_fee, delivery_distance))
}

private fun getStaticJson(venue_slug: String,
                  restTemplate: RestTemplate): String? {
    val staticUrl = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/static"
    return restTemplate.getForObject(staticUrl, String::class.java)
}
private fun getDynamicJson(venue_slug: String,
                   restTemplate: RestTemplate): String? {
    val dynamicUrl = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/dynamic"
    return restTemplate.getForObject(dynamicUrl, String::class.java)
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
    val distanceRangesNode: JsonNode = deliverySpecs.get("delivery_pricing").get("distance_ranges")
    return mapper.convertValue(distanceRangesNode, object : TypeReference<List<DistanceRange>>() {})
}