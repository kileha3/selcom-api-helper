package com.furahitechpay.furahitechpay

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.card.CardFragment
import com.furahitechpay.furahitechpay.mobile.MobileFragment
import com.furahitechpay.furahitechpay.model.BillingInfo
import com.furahitechpay.furahitechpay.model.PaymentRequest

class FurahitechPay {

    var paymentType: Int = 2

    var isEnglish: Boolean = false

    var activity: FragmentActivity? = null

    var defaultPhoneForCard: String? = null

    var paymentRequest: PaymentRequest? = null

    var paymentBilling: BillingInfo? = null

    var authToken: String? = null


    fun payNow(callback: PayCallback){

        if(paymentRequest == null){
            error("Make sure payment request is created before sending an actual request")
        }

        if((paymentType == PAYMENT_CARD || paymentType == PAYMENT_ALL) && paymentBilling == null){
            error("Make sure billing information are filled out before proceeding")
        }

        val baseFragment = when (paymentType) {
            PAYMENT_CARD -> CardFragment.getInstance(callback)
            PAYMENT_MOBILE -> MobileFragment.getInstance(callback)
            else -> PaymentOptions.getInstance(callback)
        }

        baseFragment.show(activity!!.supportFragmentManager.beginTransaction(), baseFragment.toString())
    }


    fun getFormattedPlans(): Map<String, ArrayList<*>>{
        val paymentPrices: ArrayList<Int> = arrayListOf()
        val paymentDuration: ArrayList<Int> = arrayListOf()
        val paymentLabels: ArrayList<String> = arrayListOf()

        if(paymentRequest != null){
            paymentRequest!!.paymentPlans.toList().forEach {
                paymentDuration.add(it.second.toList()[0].first * when {
                    paymentRequest!!.basePlan.toLowerCase() == "month" -> 30
                    paymentRequest!!.basePlan.toLowerCase() == "week" -> 7
                    else -> 1
                })
                paymentPrices.add(it.second.toList()[0].second)
                paymentLabels.add("${it.first}  ${it.second.toList()[0].second}/= ${paymentRequest?.currency}")
            }
        }
        return mutableMapOf(TAG_PRICES to paymentPrices, TAG_DURATION to paymentDuration, TAG_LABELS to paymentLabels)
    }


    companion object{
        val instance = FurahitechPay()

        const val PAYMENT_CARD = 1

        const val PAYMENT_MOBILE = 2

        const val PAYMENT_ALL = 3

        const val TAG_PRICES = "prices"

        const val TAG_DURATION = "duration"

        const val TAG_LABELS = "labels"

        @JvmStatic
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}