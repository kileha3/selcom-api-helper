package com.furahitechpay.selcom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.PAYMENT_ALL
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val furahitechPay = FurahitechPay.instance
        val request = PaymentRequest(basePlan = "days",
            amount = 4500,
            remarks = "subscription", paymentSummary = mutableMapOf(FurahitechPay.LANGUAGE_SWAHILI to  "Malipo kwa ajili ya ",
                FurahitechPay.LANGUAGE_ENGLISH to "Payment for "),
            paymentForWhat =  mutableMapOf(FurahitechPay.LANGUAGE_SWAHILI to  "Ondoa matangazo na pata ruhusa kwenye vilivyo fungwa",
                FurahitechPay.LANGUAGE_ENGLISH to "Remove all ads to the app"),
            paymentPlans = mutableMapOf(FurahitechPay.LANGUAGE_ENGLISH to mutableMapOf(
                "One Month" to mutableMapOf(1 to 200),
                "Two Months" to mutableMapOf(1 to 200)
            ),FurahitechPay.LANGUAGE_SWAHILI to mutableMapOf(
                "Mwezi Mmoja" to mutableMapOf(1 to 200),
                "Miezi Miwili" to mutableMapOf(1 to 200)
            ))
        )

        val billing = BillingInfo(
            userEmail = "lkileha@gmail.com",
            userFirstName = "Lukundo",
            userLastName = "Kileha"
        )

        furahitechPay.defaultPhoneForCard = "255712000000"
        furahitechPay.activity = this
        furahitechPay.isCardEnglish = true
        furahitechPay.isMobileEnglish = false
        furahitechPay.paymentType = PAYMENT_ALL
        furahitechPay.paymentRequest = request
        furahitechPay.paymentBilling = billing
        furahitechPay.authToken = "dummy"
        furahitechPay.payNow(object : PayCallback{
            override fun onFailre(message: String) {
                println("Failure message =$message")
            }

            override fun onSuccess(tokenResponse: TokenResponse) {
                println("Success message =$tokenResponse")
            }

        })
    }
}
