package com.furahitechpay.selcom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.PAYMENT_ALL
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.PAYMENT_CARD
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.PAYMENT_MOBILE
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val furahitechPay = FurahitechPay.instance
        val request = PaymentRequest( basePlan = "week",
            amount = 4500,
            remarks = "subscription", paymentSummary = "Malipo kwa ajili ya ",
            paymentForWhat = "Ondoa matangazo na pata ruhusa kwenye vilivyo fungwa",
            paymentPlans = mutableMapOf(
                "One Month" to mutableMapOf(1 to 3000),
                "Two Months" to mutableMapOf(1 to 6000)
            ))

        val billing = BillingInfo(
            userEmail = "lkileha@gmail.com",
            userFirstName = "Lukundo",
            userLastName = "Kileha"
        )

        furahitechPay.activity = this
        furahitechPay.isEnglish = true
        furahitechPay.paymentType = PAYMENT_ALL
        furahitechPay.paymentRequest = request
        furahitechPay.paymentBilling = billing
        furahitechPay.authToken = "Dummy"
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
