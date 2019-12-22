package com.furahitechpay.furahitechpay.model

data class PaymentRequest(
    var orderId: String = "",
    var remarks: String = "subscription",
    var duration: Int = 0, var productId: String ="0",
    var basePlan: String = "Month", var paymentForWhat: MutableMap<String,String> = mutableMapOf(),
    var paymentSummary: MutableMap<String,String> = mutableMapOf(), var amount: Int = 0,
    var currency: String = "TZS", var paymentPlans: MutableMap<String, MutableMap<String, MutableMap<Int, Int>>> = mutableMapOf(),
    var preSelectedPlan: Int = 0, var processingFee: Double = 0.0)