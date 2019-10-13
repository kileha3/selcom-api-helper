package com.furahitechpay.furahitechpay.model

data class BillingInfo(var userEmail: String, var userFirstName: String, var userLastName:String,
                  var userCity: String, var userAdress: String, var userPhone: String = "",
                  var userCountry: String, var userRegion: String)