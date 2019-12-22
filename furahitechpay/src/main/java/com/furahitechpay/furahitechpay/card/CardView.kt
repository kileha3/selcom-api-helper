package com.furahitechpay.furahitechpay.card

import com.furahitechpay.furahitechpay.util.BaseView

interface CardView: BaseView {

    fun checkEmptyFields()

    fun setCountries(countries: HashMap<String,String>)
}