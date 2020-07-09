package com.furahitechpay.furahitechpay.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class TokenResponse (@SerializedName("request_code") var requestCode: Int,
                          @SerializedName("request_status") var requestStatus: String,
                          @SerializedName("request_message") var requestMessage: String,
                          @SerializedName("payment_status") var paymentStatus: String,
                          @SerializedName("payment_token") var paymentToken: String,
                          @SerializedName("payment_orderId") var paymentOrderId: String,
                           @SerializedName("payment_url") var paymentUrl: String,
                           @SerializedName("payment_qr") var paymentQr: String,
                           @SerializedName("payment_instruction") var instructions: PaymentInstruction?,
                           var error: Boolean = requestStatus == "SUCCESS"
){
    class TokenResponseSerializer : ResponseDeserializable<TokenResponse> {
        override fun deserialize(content: String) = Gson().fromJson(content, TokenResponse::class.java)!!
    }
}

data class PaymentInstruction(@SerializedName("info_hId") var hoverActionId: String,
                              @SerializedName("info_swa") var infoSwahili: String,
                              @SerializedName("info_en") var infoEnglish: String){

    class PaymentInstructionSerializer : ResponseDeserializable<PaymentInstruction> {
        override fun deserialize(content: String) = Gson().fromJson(content, PaymentInstruction::class.java)!!
    }
}

