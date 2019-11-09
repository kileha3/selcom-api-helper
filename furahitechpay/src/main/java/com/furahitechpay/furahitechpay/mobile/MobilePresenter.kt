package com.furahitechpay.furahitechpay.mobile

import androidx.core.content.ContextCompat
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BasePresenter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObjectResponseResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MobilePresenter(view: MobileView) : BasePresenter<MobileView>(view) {

    class MobileOperator(var icon: Int, var index: Int)

    private val operatorsList = listOf(
        MobileOperator(R.drawable.icon_tigo,2), MobileOperator(R.drawable.icon_voda,1),
        MobileOperator(R.drawable.icon_halotel, 3), MobileOperator(R.drawable.icon_airtel, 4),
        MobileOperator(R.drawable.icon_zantel,5), MobileOperator(R.drawable.icon_ttcl, 6),
        MobileOperator(R.drawable.icon_unknown,7)
    )

   override fun onCreate(){
       super.onCreate()
       handleButtonEnabling(false)
   }

    fun handleOnNumberTyping(phone: String, length: Int){

        if(length in 2..3){
            var operatorIndex = 6
            if(phone.startsWith("71") || phone.startsWith("65") || phone.startsWith("67")){
                operatorIndex = 0
            }else if(phone.startsWith("74") || phone.startsWith("75") || phone.startsWith("76")){
                operatorIndex = 1
            }else if(phone.startsWith("77")){
                operatorIndex = 4
            }else if(phone.startsWith("73")){
                operatorIndex = 5
            }else if(phone.startsWith("62")){
                operatorIndex = 2
            }else if(phone.startsWith("68") || phone.startsWith("78")){
                operatorIndex = 3
            }
            view.setMobileOperatorIcon(operatorsList[operatorIndex])
        }

        handleButtonEnabling(length >= 9)
    }

    private fun handleButtonEnabling(enabled : Boolean){
        view.setButtonEnabled(enabled)
        val colorBg = ContextCompat.getColor(view.getBaseContext(), if (enabled) R.color.colorAccent
            else R.color.colorTextSecondary
        )
        val colorTxt = ContextCompat.getColor(
            view.getBaseContext(), if (enabled) android.R.color.white  else android.R.color.black
        )
        view.setButtonProps(colorBg, colorTxt)
    }

    fun handleCreatingToken(payCallback: PayCallback,paymentRequest: PaymentRequest, billingInfo: BillingInfo, mno: Int, token: String){
        handleButtonEnabling(false)
        view.showProgressVisible(true)
        var paymentData = listOf(
            "payment_duration" to paymentRequest.duration,
            "payment_email" to billingInfo.userEmail, "payment_amount" to paymentRequest.amount,
            "payment_phone" to billingInfo.userPhone, "payment_product" to paymentRequest.productId,
            "payment_remarks" to paymentRequest.remarks, "mno" to mno)
        if(paymentRequest.orderId.isNotEmpty()){
            paymentData = paymentData.plus("order_id" to paymentRequest.orderId)
        }

        GlobalScope.launch {
            val (_, _, result) = Fuel
                .post("https://apis.furahitech.co.tz/core/v1/payment/auth/mobile", paymentData)
                .header(mapOf("x-api-key" to token))
                .awaitObjectResponseResult(TokenResponse.TokenResponseSerializer())
            result.fold(
                { data -> view.runOnUiThread(Runnable {
                    handleButtonEnabling(true)
                    view.showPaymentResponse(data)
                    data.instructions = ""
                    payCallback.onSuccess(data)
                })},
                { error -> payCallback.onFailre(error.message!!) }
            )
        }
    }


    companion object{
        internal const val LABEL_PAYMENT_INFO = "label_payment_info"
        internal const val LABEL_EXTRA_INFO = "label_extra_info"
        internal const val LABEL_CONTACT_INFO = "label_contact_info"
        internal const val LABEL_BUTTON_INFO = "label_button_info"
        internal const val LABEL_BUTTON_PAY = "label_button_pay"
        internal const val LABEL_HOWTO_PAY = "label_how_topay"
    }
}