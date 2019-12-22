package com.furahitechpay.furahitechpay.model

data class BillingInfo(var userEmail: String, var userFirstName: String, var userLastName:String,
                       var userPhone: String = "", var userCountry: String = "", var userCity: String = "",
                       var userState: String = "",var userStreet: String = "", var userAddress: String = "",
                       var postalCode: String = "")