package service.dopc.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.client.RestTemplate
import service.dopc.dto.Delivery
import service.dopc.dto.DistanceRange
import service.dopc.dto.Output

fun calculator(venue_slug: String,
               cart_value: Int,
               user_lat: Float,
               user_lon: Float): Output {

    val cartValue = cart_value

    val restTemplate = RestTemplate()
    val mapper = ObjectMapper()

    val coordinates: JsonNode = getCoordinates(mapper, venue_slug, restTemplate)
    val longitude: Double = coordinates.get(0).asDouble()
    val latitude: Double = coordinates.get(1).asDouble()

    val deliverySpecs: JsonNode = getDeliverySpecs(mapper, venue_slug, restTemplate)
    val orderMinimumNoSurcharge: Int = deliverySpecs.get("order_minimum_no_surcharge").asInt()
    val basePrice: Int = deliverySpecs.get("delivery_pricing").get("base_price").asInt()
    val distanceRanges: List<DistanceRange> = getDistanceRanges(deliverySpecs, mapper)

    return Output(0, orderMinimumNoSurcharge, cartValue, Delivery(0, 0))
}

fun getStaticJson(venue_slug: String,
                  restTemplate: RestTemplate): String? {
    val staticUrl = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/static"
    return restTemplate.getForObject(staticUrl, String::class.java)
}
fun getDynamicJson(venue_slug: String,
                   restTemplate: RestTemplate): String? {
    val dynamicUrl = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/" +
            venue_slug + "/dynamic"
    return restTemplate.getForObject(dynamicUrl, String::class.java)
}

fun getCoordinates(mapper: ObjectMapper,
                   venue_slug: String,
                   restTemplate: RestTemplate): JsonNode {
    return mapper.readTree(getStaticJson(venue_slug, restTemplate)).get("venue_raw").get("location").get("coordinates")
}

fun getDeliverySpecs(mapper: ObjectMapper,
                     venue_slug: String,
                     restTemplate: RestTemplate): JsonNode {
    return mapper.readTree(getDynamicJson(venue_slug, restTemplate)).get("venue_raw").get("delivery_specs")
}

fun getDistanceRanges(deliverySpecs: JsonNode,
                      mapper: ObjectMapper): List<DistanceRange> {
    val distanceRangesNode: JsonNode = deliverySpecs.get("delivery_pricing").get("distance_ranges")
    return mapper.convertValue(distanceRangesNode, object : TypeReference<List<DistanceRange>>() {})
}