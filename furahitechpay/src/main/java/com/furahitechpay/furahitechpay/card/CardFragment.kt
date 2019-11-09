package com.furahitechpay.furahitechpay.card


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.card.CardPaymentRedirection.Companion.REDIRECTION_URL
import com.furahitechpay.furahitechpay.mobile.MobileFragment.Companion.formatPrice
import com.furahitechpay.furahitechpay.mobile.MobilePresenter
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CardFragment : BaseFragment(), CardView, PayCallback{

    private val mobileUiLabels = hashMapOf(
        "swa" to hashMapOf(
            MobilePresenter.LABEL_PAYMENT_INFO to "Taarifa za Malipo",
            MobilePresenter.LABEL_EXTRA_INFO to "Maelezo",
            MobilePresenter.LABEL_CONTACT_INFO to "Taarifa za mawasiliano",
            MobilePresenter.LABEL_BUTTON_PAY to "Anza Kulipa"),
        "en" to hashMapOf(
            MobilePresenter.LABEL_PAYMENT_INFO to "Payment Information",
            MobilePresenter.LABEL_EXTRA_INFO to "Extra Information",
            MobilePresenter.LABEL_CONTACT_INFO to "Billing Information",
            MobilePresenter.LABEL_BUTTON_PAY to "Start Payment")
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

    private lateinit var streetAddress: EditText

    private lateinit var cityAddress: EditText

    private lateinit var regionAddress: EditText

    private lateinit var coordinatorLayout: CoordinatorLayout

    private var selectedPlan: Int = 0

    private val paymentRequest: PaymentRequest = furahitechPay.paymentRequest!!

    private var selectedPrice = paymentRequest.amount

    private lateinit var presenter: CardPresenter

    private lateinit var mainHolder: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        payCallback = this

        startPaymentBtn = view.findViewById(R.id.action_pay)
        plansOptions = view.findViewById(R.id.payment_length)
        totalAmountView = view.findViewById(R.id.total_amount)
        paymentInfoView = view.findViewById(R.id.payment_info)
        phoneNumberView = view.findViewById(R.id.phone_number)
        progressBar = view.findViewById(R.id.progress_bar)
        streetAddress = view.findViewById(R.id.street_address)
        cityAddress = view.findViewById(R.id.address_city)
        regionAddress = view.findViewById(R.id.address_region)
        mainHolder = view.findViewById(R.id.main_holder)
        coordinatorLayout = view.findViewById(R.id.bottom_sheet)

        view.findViewById<EditText>(R.id.full_name).setText("${furahitechPay.paymentBilling?.userFirstName} ${furahitechPay.paymentBilling?.userLastName}")
        view.findViewById<EditText>(R.id.email_address).setText(furahitechPay.paymentBilling?.userEmail)
        phoneNumberView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        languageMap = mobileUiLabels[if(furahitechPay.isEnglish) "en" else "swa"]!!

        setUpPaymentOptions()

        setUILabels(view)

        presenter = CardPresenter(this)
        presenter.onCreate()

        startPaymentBtn.setOnClickListener {
            val billing = furahitechPay.paymentBilling!!
            billing.userPhone = "255${phoneNumberView.rawText}"
            billing.userCity = cityAddress.text.toString()
            billing.userRegion = regionAddress.text.toString()
            billing.userAdress = streetAddress.text.toString()
            presenter.handleGetRedirection(paymentRequest, billing, furahitechPay.authToken!!, payCallback)
        }

        phoneNumberView.addTextChangedListener(textWatcher)

        regionAddress.addTextChangedListener(textWatcher)

        streetAddress.addTextChangedListener(textWatcher)

        cityAddress.addTextChangedListener(textWatcher)


        return view
    }

    private fun isNoEmpty(view: EditText): Boolean{
        return view.text.isNotEmpty()
    }

    private fun setUILabels(view: View){
        view.findViewById<TextView>(R.id.billing_info_label).text = languageMap[MobilePresenter.LABEL_CONTACT_INFO]
        view.findViewById<TextView>(R.id.pay_info_label).text = languageMap[MobilePresenter.LABEL_PAYMENT_INFO]
        view.findViewById<TextView>(R.id.extra_info_label).text = languageMap[MobilePresenter.LABEL_EXTRA_INFO]
        view.findViewById<TextView>(R.id.payment_for).text = furahitechPay.paymentRequest!!.paymentForWhat
        startPaymentBtn.text = languageMap[MobilePresenter.LABEL_BUTTON_PAY]
    }


    private fun setUpPaymentOptions(){
        val visibility = if(furahitechPay.paymentRequest!!.paymentPlans.toList().isNotEmpty()) View.VISIBLE else View.GONE
        plansOptions.visibility = visibility

        if(furahitechPay.paymentRequest!!.paymentPlans.isNotEmpty()){
            furahitechPay.paymentRequest!!.paymentPlans.toList().forEach {
                paymentDuration.add(it.second.toList()[0].first * if(paymentRequest.basePlan == "Month") 30 else 7)
                paymentPrices.add(it.second.toList()[0].second)
                paymentLabels.add("${it.first}  ${it.second.toList()[0].second}/= ${paymentRequest.currency}")
            }

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
            paymentInfoView.text = paymentRequest.paymentSummary
            totalAmountView.text = formatPrice(selectedPrice,paymentRequest.currency)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAmount(){
        paymentRequest.amount = selectedPrice
        totalAmountView.text = formatPrice(selectedPrice,paymentRequest.currency)
        paymentInfoView.text = paymentRequest.paymentSummary + " " + paymentLabels[selectedPlan]
        paymentRequest.duration = paymentDuration[selectedPlan]
        presenter.updateBtnState()
    }


    override fun checkEmptyFields() {
        val enableBtn = isNoEmpty(cityAddress) && isNoEmpty(regionAddress)
                && isNoEmpty(phoneNumberView) && isNoEmpty(streetAddress)
        presenter.handleButtonEnabling(enableBtn)

    }

    override fun getBaseContext(): Context { return activity!!}

    override fun showError(message: String) {
        Snackbar.make(coordinatorLayout,message, Snackbar.LENGTH_LONG).show()
    }

    override fun showPaymentResponse(response: TokenResponse) {

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

    override fun onSuccess(tokenResponse: TokenResponse) {
        showProgressVisible(false)
        val intent = Intent(activity, CardPaymentRedirection::class.java)
        intent.putExtra(REDIRECTION_URL,tokenResponse.instructions)
        dismiss()
        startActivity(intent)
    }

    override fun onFailre(message: String) {
        showError(if(FurahitechPay.instance.isEnglish) "Payment didn't go through, try gain" else "Malipo hayakukamilika, jaribu tena")
        Handler().postDelayed({ dismiss() }, TimeUnit.SECONDS.toMillis(2))
    }


    companion object{

        private lateinit var payCallback: PayCallback

        fun getInstance(callback: PayCallback): CardFragment{
            this.payCallback = callback
            return CardFragment()
        }
    }


}
