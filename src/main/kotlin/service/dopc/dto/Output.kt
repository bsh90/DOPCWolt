package service.dopc.dto

data class Output (var total_price: Int,
                   var small_order_surcharge: Int,
                   var cart_value: Int,
                   var delivery: Delivery)