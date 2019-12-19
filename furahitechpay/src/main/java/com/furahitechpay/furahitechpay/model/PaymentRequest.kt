package com.furahitechpay.furahitechpay.model

data class PaymentRequest (var orderId: String = "",var remarks: String = "subscription",
                           var duration: Int = 0, var productId: String ="0",
                           var basePlan: String = "Month", var paymentForWhat: String = "",
                           var paymentSummary: String = "", var amount: Int = 0,
                           var currency: String = "TZS", var paymentPlans: MutableMap<String, Map<Int, Int>> = mutableMapOf(),
                           var preSelectedPlan: Int = 0, var processingFee: Double = 0.0)