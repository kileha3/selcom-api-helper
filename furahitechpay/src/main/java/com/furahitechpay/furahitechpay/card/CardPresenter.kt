package com.furahitechpay.furahitechpay.card

import androidx.core.content.ContextCompat
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.Countries
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BasePresenter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardPresenter(view: CardView): BasePresenter<CardView>(view) {

    private val countryMap = HashMap<String, String>()

    private var currentCountryCode = "TZ"

    override fun onCreate() {
        super.onCreate()
        updateBtnState()
        Gson().fromJson<List<Countries>>(view.getBaseContext().assets.open("countries.json")
            .bufferedReader().use {it.readText()}, object : TypeToken<List<Countries?>?>() {}.type).forEach {
            countryMap[it.name] = it.code
        }
        view.setCountries(countryMap)
    }

    fun updateBtnState(){
        view.checkEmptyFields()
    }

    fun handleSelectedCountry(countryName: String){
         currentCountryCode = countryMap[countryName]!!
    }

    fun handleButtonEnabling(enabled : Boolean){
        view.setButtonEnabled(enabled)
        val colorBg = ContextCompat.getColor(view.getBaseContext(), if (enabled) R.color.colorAccent
        else R.color.colorTextSecondary
        )
        val colorTxt = ContextCompat.getColor(
            view.getBaseContext(), if (enabled) android.R.color.white  else android.R.color.black
        )
        view.setButtonProps(colorBg, colorTxt)
    }

    fun handleGetRedirection(paymentRequest: PaymentRequest, billingInfo: BillingInfo, token: String, payCallback: PayCallback){
        handleButtonEnabling(false)
        view.showProgressVisible(true)
        val amount = paymentRequest.amount + (paymentRequest.processingFee * paymentRequest.amount)
        var paymentData = listOf(
            "payment_duration" to paymentRequest.duration,"payment_email" to billingInfo.userEmail,
            "payment_amount" to amount, "payment_phone" to billingInfo.userPhone,
            "payment_product" to paymentRequest.productId,
            "payment_remarks" to paymentRequest.remarks, "payment_country" to currentCountryCode,
            "payment_address" to billingInfo.userAddress, "payment_city" to billingInfo.userCity,
            "payment_state" to billingInfo.userState, "payment_street" to billingInfo.userStreet,
            "payment_postcode" to billingInfo.postalCode,
            "payment_name" to  "${billingInfo.userFirstName} ${billingInfo.userLastName}")
        if(paymentRequest.orderId.isNotEmpty()){
            paymentData = paymentData.plus("order_id" to paymentRequest.orderId)
        }
        GlobalScope.launch{
            val (_, _, result) = Fuel
                .post("https://apis.furahitech.co.tz/core/v1/payment/auth/card", paymentData)
                .header(mapOf("x-api-key" to token))
                .awaitObjectResponseResult(TokenResponse.TokenResponseSerializer())
            result.fold(
                { data -> view.runOnUiThread(Runnable {
                    handleButtonEnabling(true)
                    view.showPaymentResponse(data)
                    view.showProgressVisible(false)
                    payCallback.onSuccess(data)
                })},
                { error -> launch(Dispatchers.Main) {payCallback.onFailre(error.message!!)  }}
            )
        }
    }
}