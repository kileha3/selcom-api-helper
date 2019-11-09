package com.furahitechpay.furahitechpay.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class TokenResponse (var error: Boolean,
                          @SerializedName("payment_orderId") var paymentOrderId: String,
                          @SerializedName("payment_phone") var paymentPhone: String,
                          @SerializedName("payment_qr") var paymentQr: String,
                          @SerializedName("payment_status") var paymentStatus: String,
                          @SerializedName("payment_token") var paymentToken: String,
                          @SerializedName("payment_instruction") var instructions: String){
    class TokenResponseSerializer : ResponseDeserializable<TokenResponse> {
        override fun deserialize(content: String) = Gson().fromJson(content, TokenResponse::class.java)!!
    }
}

