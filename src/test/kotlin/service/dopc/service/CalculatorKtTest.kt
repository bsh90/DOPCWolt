package service.dopc.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import service.dopc.dto.Delivery
import service.dopc.dto.Output

class CalculatorKtTest {

    @Test
    fun happyPath_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = 20
        val user_lat : Double = 30.50
        val user_lon : Double = 40.50

        val output : Output = Output(1190, 980, 20, Delivery(190, 34))
        val expected : ResponseEntity<out Any> = ResponseEntity(output, HttpStatus.OK)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }

    @Test
    fun distance_range_error1_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = 20
        val user_lat : Double = 2100.0
        val user_lon : Double = 40.50

        val expected : ResponseEntity<out Any> = ResponseEntity("Distance range result is empty. Please change user_lat or user_lon.", HttpStatus.BAD_REQUEST)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }

    @Test
    fun distance_range_error2_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = 20
        val user_lat : Double = 30.50
        val user_lon : Double = 2100.0

        val expected : ResponseEntity<out Any> = ResponseEntity("Distance range result is empty. Please change user_lat or user_lon.", HttpStatus.BAD_REQUEST)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }

    @Test
    fun distance_range_error3_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = 20
        val user_lat : Double = -2100.0
        val user_lon : Double = 40.50

        val expected : ResponseEntity<out Any> = ResponseEntity("Distance range result is empty. Please change user_lat or user_lon.", HttpStatus.BAD_REQUEST)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }

    @Test
    fun cart_value_error_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = -20
        val user_lat : Double = 30.50
        val user_lon : Double = 40.50

        val expected : ResponseEntity<out Any> = ResponseEntity("cart value can not be negative value.",
                HttpStatus.BAD_REQUEST)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }

    @Test
    fun venue_slug_error_calculator() {
        val venue_slug : String = "berlin"
        val cart_value : Int = 20
        val user_lat : Double = 30.50
        val user_lon : Double = 40.50

        val expected : ResponseEntity<out Any> = ResponseEntity("Venue slug can just be from the three available options.",
                HttpStatus.BAD_REQUEST)

        val actual : ResponseEntity<out Any> = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(expected, actual)
    }
}