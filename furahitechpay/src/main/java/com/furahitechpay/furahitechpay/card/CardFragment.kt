package com.furahitechpay.furahitechpay.card


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.util.BaseFragment

class CardFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        return view
    }

    companion object{
        lateinit var callback: PayCallback

        fun getInstance(callback: PayCallback): CardFragment{
            this.callback = callback
            return CardFragment()
        }
    }


}
