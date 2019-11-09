package com.furahitechpay.furahitechpay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.card.CardFragment
import com.furahitechpay.furahitechpay.mobile.MobileFragment
import com.furahitechpay.furahitechpay.util.BaseFragment

class PaymentOptions: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_options, container, false)

        val cardPaymentLabel = view.findViewById<TextView>(R.id.option_label_card)
        val mobilePaymentLabel = view.findViewById<TextView>(R.id.option_label_mobile)

        val cardHolder = view.findViewById<RelativeLayout>(R.id.card_option_handle)
        val mobileHolder = view.findViewById<RelativeLayout>(R.id.mobile_option_handle)

        val labelsCard =  if(FurahitechPay.instance.isEnglish) "Card Payments" else "Lipa Kwa Kadi"
        val labelsMobile =  if(FurahitechPay.instance.isEnglish) "Mobile Payments" else "Lipa Kwa Simu"

        cardPaymentLabel.text = labelsCard
        mobilePaymentLabel.text = labelsMobile

        mobileHolder.setOnClickListener { handleOptionSelection(MobileFragment.getInstance(callback)) }

        cardHolder.setOnClickListener {handleOptionSelection(CardFragment.getInstance(callback)) }

        return view
    }

    private fun handleOptionSelection(fragment: BaseFragment){
        fragment.show(activity!!.supportFragmentManager.beginTransaction(), fragment.toString())
        dismiss()
    }

    companion object{
        lateinit var callback: PayCallback

        fun getInstance(callback: PayCallback): PaymentOptions {
            this.callback = callback
            return PaymentOptions()
        }
    }
}