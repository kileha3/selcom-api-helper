package com.furahitechpay.furahitechpay.card

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.TAG_DURATION
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.TAG_LABELS
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.TAG_PRICES
import com.furahitechpay.furahitechpay.FurahitechPay.Companion.hideKeyboard
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.card.CardPaymentRedirection.Companion.REDIRECTION_URL
import com.furahitechpay.furahitechpay.mobile.MobileFragment.Companion.formatPrice
import com.furahitechpay.furahitechpay.mobile.MobilePresenter
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.concurrent.TimeUnit


class CardFragment : BaseFragment(), CardView, PayCallback{

    private val mobileUiLabels = hashMapOf(
        "swa" to hashMapOf(
            MobilePresenter.LABEL_PAYMENT_INFO to "Taarifa za Malipo",
            MobilePresenter.LABEL_EXTRA_INFO to "Maelezo",
            MobilePresenter.LABEL_CONTACT_INFO to "Taarifa za mawasiliano",
            MobilePresenter.LABEL_BUTTON_PAY to "Anza Kulipa",
            MobilePresenter.LABEL_FIRST_NAME to "Jina la kwanza",
            MobilePresenter.LABEL_LAST_NAME to "Jina la mwisho",
            MobilePresenter.LABEL_ADRESS to "Anuani yako",
            MobilePresenter.LABEL_COUNTRY_NAME to "Nchi",
            MobilePresenter.LABEL_CITY_NAME to "Mji utokako",
            MobilePresenter.LABEL_STATE_NAME to "Mkoa au Wilaya",
            MobilePresenter.LABEL_STREET_NAME to "Mtaa wako",
            MobilePresenter.LABEL_POSTAL_CODE to "Sanduku la posta"),
        "en" to hashMapOf(
            MobilePresenter.LABEL_PAYMENT_INFO to "Payment Information",
            MobilePresenter.LABEL_EXTRA_INFO to "Extra Information",
            MobilePresenter.LABEL_CONTACT_INFO to "Contact Information",
            MobilePresenter.LABEL_BUTTON_PAY to "Start Payment",
            MobilePresenter.LABEL_FIRST_NAME to "First Name",
            MobilePresenter.LABEL_LAST_NAME to "Last name",
            MobilePresenter.LABEL_ADRESS to "Address",
            MobilePresenter.LABEL_COUNTRY_NAME to "Country",
            MobilePresenter.LABEL_CITY_NAME to "City",
            MobilePresenter.LABEL_STATE_NAME to "State or Region",
            MobilePresenter.LABEL_STREET_NAME to "Street",
            MobilePresenter.LABEL_POSTAL_CODE to "Postal Code or P.O.Box")
    )

    private val textWatcher = object: TextWatcher{
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            presenter.updateBtnState()
        }

    }

    private var paymentPrices: ArrayList<Int> = arrayListOf()

    private var paymentDuration: ArrayList<Int> = arrayListOf()

    private var paymentLabels: ArrayList<String> = arrayListOf()

    private val furahitechPay = FurahitechPay.instance

    private lateinit var startPaymentBtn: Button

    private lateinit var plansOptions: Spinner

    private lateinit var totalAmountView: TextView

    private lateinit var paymentInfoView: TextView

    private lateinit var languageMap: HashMap<String, String>

    private lateinit var phoneNumberView: MaskedEditText

    private lateinit var progressBar: ProgressBar

    private lateinit var coordinatorLayout: CoordinatorLayout


    private val paymentRequest: PaymentRequest = furahitechPay.paymentRequest!!

    private var selectedPlan: Int = paymentRequest.preSelectedPlan

    private var selectedPrice = paymentRequest.amount

    private lateinit var presenter: CardPresenter

    private lateinit var mainHolder: LinearLayout

    private lateinit var rootView: View

    private lateinit var firstName: TextInputEditText

    private lateinit var lastName: TextInputEditText

    private lateinit var address: TextInputEditText

    private lateinit var cityName: TextInputEditText

    private lateinit var stateName: TextInputEditText

    private lateinit var streetName: TextInputEditText

    private lateinit var postalCode: TextInputEditText

    private val language = if(furahitechPay.isCardEnglish)  FurahitechPay.LANGUAGE_ENGLISH else FurahitechPay.LANGUAGE_SWAHILI

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_card, container, false)

        payCallback = this

        startPaymentBtn = rootView.findViewById(R.id.action_pay)
        plansOptions = rootView.findViewById(R.id.payment_length)
        totalAmountView = rootView.findViewById(R.id.total_amount)
        paymentInfoView = rootView.findViewById(R.id.payment_info)
        phoneNumberView = rootView.findViewById(R.id.phone_number)
        progressBar = rootView.findViewById(R.id.progress_bar)
        mainHolder = rootView.findViewById(R.id.main_holder)
        coordinatorLayout = rootView.findViewById(R.id.bottom_sheet)
        firstName = rootView.findViewById(R.id.first_name)
        lastName = rootView.findViewById(R.id.last_name)
        address = rootView.findViewById(R.id.first_address)
        cityName = rootView.findViewById(R.id.city_name)
        stateName = rootView.findViewById(R.id.state_name)
        streetName = rootView.findViewById(R.id.street_name)
        postalCode = rootView.findViewById(R.id.postal_code)

        phoneNumberView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        languageMap = mobileUiLabels[if(furahitechPay.isCardEnglish) "en" else "swa"]!!

        setUpPaymentOptions()

        setUILabels(rootView)
        phoneNumberView.setText(furahitechPay.defaultPhoneForCard)

        presenter = CardPresenter(this)
        presenter.onCreate()

        startPaymentBtn.setOnClickListener {
            presenter.handleGetRedirection(paymentRequest, furahitechPay.paymentBilling!!, furahitechPay.authToken!!, payCallback)
        }

        phoneNumberView.addTextChangedListener(textWatcher)
        firstName.addTextChangedListener(textWatcher)
        lastName.addTextChangedListener(textWatcher)
        address.addTextChangedListener(textWatcher)
        cityName.addTextChangedListener(textWatcher)
        streetName.addTextChangedListener(textWatcher)
        stateName.addTextChangedListener(textWatcher)
        postalCode.addTextChangedListener(textWatcher)


        return rootView
    }

    private fun isNoEmpty(view: EditText): Boolean{
        return view.text.isNotEmpty()
    }

    private fun setUILabels(view: View){
        view.findViewById<TextView>(R.id.billing_info_label).text = languageMap[MobilePresenter.LABEL_CONTACT_INFO]
        view.findViewById<TextView>(R.id.pay_info_label).text = languageMap[MobilePresenter.LABEL_PAYMENT_INFO]
        view.findViewById<TextView>(R.id.extra_info_label).text = languageMap[MobilePresenter.LABEL_EXTRA_INFO]
        view.findViewById<TextView>(R.id.payment_for).text = furahitechPay.paymentRequest!!.paymentForWhat[language]
        firstName.hint = languageMap[MobilePresenter.LABEL_FIRST_NAME]
        lastName.hint = languageMap[MobilePresenter.LABEL_LAST_NAME]
        address.hint = languageMap[MobilePresenter.LABEL_ADRESS]
        cityName.hint = languageMap[MobilePresenter.LABEL_CITY_NAME]
        stateName.hint = languageMap[MobilePresenter.LABEL_STATE_NAME]
        streetName.hint = languageMap[MobilePresenter.LABEL_STREET_NAME]
        postalCode.hint = languageMap[MobilePresenter.LABEL_POSTAL_CODE]
        view.findViewById<TextView>(R.id.country_label).text = languageMap[MobilePresenter.LABEL_COUNTRY_NAME]
        startPaymentBtn.text = languageMap[MobilePresenter.LABEL_BUTTON_PAY]
    }


    private fun setUpPaymentOptions(){
        val visibility = if(furahitechPay.paymentRequest!!.paymentPlans.toList().isNotEmpty()) View.VISIBLE else View.GONE
        plansOptions.visibility = visibility

        if(furahitechPay.paymentRequest!!.paymentPlans.isNotEmpty()){
            paymentDuration = furahitechPay.getFormattedPlans(language)[TAG_DURATION] as ArrayList<Int>
            paymentPrices = furahitechPay.getFormattedPlans(language)[TAG_PRICES] as ArrayList<Int>
            paymentLabels = furahitechPay.getFormattedPlans(language)[TAG_LABELS] as ArrayList<String>

            if(paymentLabels.isNotEmpty()){
                val dataAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, paymentLabels)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                plansOptions.adapter = dataAdapter

                plansOptions.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(p0: AdapterView<*>?) { }

                    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedPlan = position
                        selectedPrice = paymentPrices[selectedPlan]
                        updateAmount()
                    }
                })
                plansOptions.setSelection(selectedPlan,true)
            }

        }else{
            paymentInfoView.text = paymentRequest.paymentSummary[language]
            totalAmountView.text = formatPrice(selectedPrice,paymentRequest.currency)
        }

        phoneNumberView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val view1 = activity!!.currentFocus
                if (view1 != null) {
                    hideKeyboard(activity!!)
                }
            }
            false
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateAmount(){
        paymentRequest.amount = selectedPrice
        totalAmountView.text = formatPrice(selectedPrice,paymentRequest.currency)
        paymentInfoView.text = paymentRequest.paymentSummary[language] + " " + paymentLabels[selectedPlan]
        paymentRequest.duration = paymentDuration[selectedPlan]
        presenter.updateBtnState()
    }


    override fun checkEmptyFields() {
        val paymentBilling =  furahitechPay.paymentBilling!!
        paymentBilling.userFirstName = firstName.text.toString()
        paymentBilling.userLastName = lastName.text.toString()
        paymentBilling.userCity = cityName.text.toString()
        paymentBilling.userState = stateName.text.toString()
        paymentBilling.userStreet = streetName.text.toString()
        paymentBilling.userAddress = address.text.toString()
        paymentBilling.postalCode = postalCode.text.toString()
        paymentBilling.userPhone = "255${phoneNumberView.rawText}"
        presenter.handleButtonEnabling(isNoEmpty(postalCode) && isNoEmpty(phoneNumberView) && isNoEmpty(firstName) && isNoEmpty(streetName)
                && isNoEmpty(lastName) && isNoEmpty(address) && isNoEmpty(cityName) && isNoEmpty(stateName)
                && phoneNumberView.rawText.length > 4)

    }

    override fun setCountries(countries: HashMap<String, String>) {
        val mCountries = rootView.findViewById<Spinner>(R.id.country_name)
        val countryArray = countries.keys.toTypedArray().sortedArray()
        val dataAdapter = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, countryArray)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mCountries.adapter = dataAdapter

        mCountries.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.handleSelectedCountry(countryArray[position])
            }
        })
        mCountries.setSelection(0,true)

    }

    override fun getBaseContext(): Context { return activity!!}

    override fun showError(message: String) {
        Snackbar.make(coordinatorLayout,message, Snackbar.LENGTH_LONG).show()
    }

    override fun showPaymentResponse(response: TokenResponse) {

    }

    override fun dismissDialog() {
        dismiss()
    }

    override fun setButtonProps(btnColor: Int, txtColor: Int) {
        startPaymentBtn.isEnabled
        startPaymentBtn.setBackgroundColor(btnColor)
        startPaymentBtn.setTextColor(txtColor)
    }

    override fun setButtonEnabled(enabled: Boolean) {
        startPaymentBtn.isEnabled = enabled
    }



    override fun showProgressVisible(show: Boolean) {
        mainHolder.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    @ExperimentalStdlibApi
    override fun onSuccess(tokenResponse: TokenResponse) {
        showProgressVisible(false)
        val intent = Intent(activity, CardPaymentRedirection::class.java)
        intent.putExtra(REDIRECTION_URL, Base64.decode(tokenResponse.instructions, Base64.DEFAULT).decodeToString())
        dismissDialog()
        startActivity(intent)
    }

    override fun onFailre(message: String) {
        showError(if(FurahitechPay.instance.isCardEnglish) "Payment flow didn't start successfully, try gain later" else "Malipo hayakufanikiwa kuanza, jaribu tena baadae")
        Handler().postDelayed({ dismissDialog() }, TimeUnit.SECONDS.toMillis(2))
    }


    companion object{

        private lateinit var payCallback: PayCallback

        fun getInstance(callback: PayCallback): CardFragment{
            this.payCallback = callback
            return CardFragment()
        }
    }


}
