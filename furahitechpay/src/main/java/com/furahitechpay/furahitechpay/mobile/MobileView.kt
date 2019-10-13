package com.furahitechpay.furahitechpay.mobile

import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BaseView

interface MobileView : BaseView {

    fun setMobileOperatorIcon(operator: MobilePresenter.MobileOperator)

    fun setButtonProps(btnColor: Int, txtColor: Int)

    fun setButtonEnabled(enabled: Boolean)

    fun showTokenResponse(response: TokenResponse)
}