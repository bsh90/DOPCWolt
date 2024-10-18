package service.dopc.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.web.client.RestTemplate
import service.dopc.dto.Delivery
import service.dopc.dto.Output

class CalculatorKtTest {

    @Test
    fun happyPath_calculator() {
        val venue_slug : String = "home-assignment-venue-berlin"
        val cart_value : Int = 20
        val user_lat : Double = 30.50
        val user_lon : Double = 40.50

        val result : Output = calculator(venue_slug, cart_value, user_lat, user_lon)

        assertEquals(Output(1190, 980, 20, Delivery(190, 34)), result)
    }

}