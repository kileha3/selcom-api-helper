package com.furahitechpay.selcom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val furahitechPay = FurahitechPay.instance
        val request = PaymentRequest(
            remarks = "subscription", paymentSummary = "Malipo kwa ajili ya ",
            paymentForWhat = "Ondoa matangazo na pata ruhusa kwenye vilivyo fungwa",
            paymentPlans = mutableMapOf(
                "Mwezi Mmoja" to mapOf(1 to 500),
                "Miezi miwili" to mapOf(2 to 2000))
        )

        val billing = BillingInfo(
            userEmail = "lkileha@gmail.com",
            userFirstName = "Lukundo",
            userLastName = "Kileha",
            userAdress = "Miyuji, Proper",
            userCity = "Dodoma",
            userRegion = "Dodoma",
            userCountry = "Tanzania"
        )

        furahitechPay.activity = this
        furahitechPay.isEnglish = false
        furahitechPay.isCardPayment = false
        furahitechPay.paymentRequest = request
        furahitechPay.paymentBilling = billing
        furahitechPay.authToken = "d14f3a80-3f0d-4053-832d-cac163daaf17"
        furahitechPay.payNow()
    }
}
