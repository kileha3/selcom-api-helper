package com.furahitechpay.furahitechpay.model

import com.google.gson.annotations.SerializedName

data class Countries (@SerializedName("Code") var code: String, @SerializedName("Name") var name: String)