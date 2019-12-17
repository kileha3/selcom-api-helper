package com.furahitechpay.furahitechpay.card

import androidx.core.content.ContextCompat
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BasePresenter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardPresenter(view: CardView): BasePresenter<CardView>(view) {

    override fun onCreate() {
        super.onCreate()
        updateBtnState()
    }

    fun updateBtnState(){
        view.checkEmptyFields()
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
        val amount = paymentRequest.amount + (0.03 * paymentRequest.amount)
        var paymentData = listOf(
            "payment_duration" to paymentRequest.duration,
            "payment_email" to billingInfo.userEmail, "payment_amount" to amount,
            "payment_phone" to billingInfo.userPhone, "payment_product" to paymentRequest.productId,
            "payment_remarks" to paymentRequest.remarks, "payment_country" to "TZ",
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