package com.furahitechpay.furahitechpay.mobile

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.sapereaude.maskedEditText.MaskedEditText
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.R
import com.furahitechpay.furahitechpay.callback.PayCallback
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_BUTTON_INFO
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_BUTTON_PAY
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_CONTACT_INFO
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_EXTRA_INFO
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_HOWTO_PAY
import com.furahitechpay.furahitechpay.mobile.MobilePresenter.Companion.LABEL_PAYMENT_INFO
import com.furahitechpay.furahitechpay.model.PaymentRequest
import com.furahitechpay.furahitechpay.model.TokenResponse
import com.furahitechpay.furahitechpay.util.BaseFragment
import com.google.android.material.snackbar.Snackbar

class MobileFragment : BaseFragment(), MobileView {


    private val mobileUiLabels = hashMapOf(
        "swa" to hashMapOf(
            LABEL_PAYMENT_INFO to "Taarifa za Malipo", LABEL_EXTRA_INFO to "Maelezo",
            LABEL_CONTACT_INFO to "Taarifa za mawasiliano",LABEL_BUTTON_INFO to "Nakili Tokeni",
            LABEL_BUTTON_PAY to "Tengeneza Tokeni", LABEL_HOWTO_PAY to "Jinsi ya kulipia"),
        "en" to hashMapOf(
            LABEL_PAYMENT_INFO to "Payment Information", LABEL_EXTRA_INFO to "Extra Information",
            LABEL_CONTACT_INFO to "Contact Information", LABEL_BUTTON_INFO to "Copy Token",
            LABEL_BUTTON_PAY to "Generate Token",  LABEL_HOWTO_PAY to "Payment Instruction")
    )

    private val furahitechPay = FurahitechPay.instance

    private lateinit var presenter: MobilePresenter

    private var isTokenRequest = true

    private var paymentPrices: ArrayList<Int> = arrayListOf()

    private var paymentDuration: ArrayList<Int> = arrayListOf()

    private var paymentLabels: ArrayList<String> = arrayListOf()

    private lateinit var startPaymentBtn: Button

    private lateinit var plansOptions: Spinner

    private lateinit var totalAmountView: TextView

    private lateinit var howToPayInfo: TextView

    private lateinit var paymentInfoView: TextView

    private lateinit var languageMap: HashMap<String, String>

    private lateinit var phoneNumberView: MaskedEditText

    private lateinit var operatorIcon: ImageView

    private lateinit var closeInstruction: ImageView

    private lateinit var progressBar: ProgressBar

    private var currentMno = 6

    private lateinit var paymentDetailsHolder: LinearLayout

    private lateinit var contactDetailsHolder: LinearLayout

    private lateinit var tokenDetailsHolder: LinearLayout

    private lateinit var mainHolder: LinearLayout

    private lateinit var coordinatorLayout: CoordinatorLayout

    private var currentToken = ""

    private val paymentRequest: PaymentRequest = furahitechPay.paymentRequest!!

    private var selectedPlan: Int = paymentRequest.preSelectedPlan

    private var selectedPrice = paymentRequest.amount


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_mobile, container, false)

        startPaymentBtn = view.findViewById(R.id.action_pay)
        plansOptions = view.findViewById(R.id.payment_length)
        totalAmountView = view.findViewById(R.id.total_amount)
        paymentInfoView = view.findViewById(R.id.payment_info)
        phoneNumberView = view.findViewById(R.id.phone_number)
        operatorIcon = view.findViewById(R.id.payment_gateway)
        progressBar = view.findViewById(R.id.progress_bar)
        paymentDetailsHolder = view.findViewById(R.id.order_details)
        contactDetailsHolder = view.findViewById(R.id.contact_details)
        tokenDetailsHolder = view.findViewById(R.id.token_details)
        howToPayInfo = view.findViewById(R.id.instruction_content)
        mainHolder = view.findViewById(R.id.main_holder)
        closeInstruction = view.findViewById(R.id.close_instruction)
        coordinatorLayout = view.findViewById(R.id.bottom_sheet)

        phoneNumberView.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                presenter.handleOnNumberTyping(phoneNumberView.rawText, phoneNumberView.rawText.length)
            }

        })

        closeInstruction.setOnClickListener { dismiss() }

        phoneNumberView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val view1 = activity!!.currentFocus
                if (view1 != null) {
                    hideKeyboard()
                }
            }
            false
        }

        startPaymentBtn.setOnClickListener {
           if(isTokenRequest){
               isTokenRequest = false
               isCancelable = false
               val billing = furahitechPay.paymentBilling!!
               billing.userPhone = "255${phoneNumberView.rawText}"
               presenter.handleCreatingToken(callback,paymentRequest, billing, currentMno, furahitechPay.authToken!!)
           }else{
               val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val clip = ClipData.newPlainText("payment-token", currentToken)
               clipboard.setPrimaryClip(clip)
               Toast.makeText(activity!!, if(furahitechPay.isEnglish)  "Token copied" else "Umenakili tokeni", Toast.LENGTH_LONG).show()
           }
        }

        languageMap = mobileUiLabels[if(furahitechPay.isEnglish) "en" else "swa"]!!


        setUpPaymentOptions()

        setUILabels(view)

        presenter = MobilePresenter(this)
        presenter.onCreate()

        return view
    }

    private fun hideKeyboard() {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
    }

    private fun setUILabels(view: View){
        view.findViewById<TextView>(R.id.contact_info_label).text = languageMap[LABEL_CONTACT_INFO]
        view.findViewById<TextView>(R.id.pay_info_label).text = languageMap[LABEL_PAYMENT_INFO]
        view.findViewById<TextView>(R.id.extra_info_label).text = languageMap[LABEL_EXTRA_INFO]
        view.findViewById<TextView>(R.id.payment_for).text = furahitechPay.paymentRequest!!.paymentForWhat
        view.findViewById<TextView>(R.id.instruction_label).text = languageMap[LABEL_HOWTO_PAY]
        startPaymentBtn.text = languageMap[LABEL_BUTTON_PAY]
    }

    @SuppressLint("SetTextI18n")
    private fun updateAmount(){
        paymentRequest.amount = selectedPrice
        totalAmountView.text = formatPrice(selectedPrice,paymentRequest.currency)
        paymentInfoView.text = paymentRequest.paymentSummary + " " + paymentLabels[selectedPlan]
        paymentRequest.duration = paymentDuration[selectedPlan]
    }

    private fun setUpPaymentOptions(){
        val visibility = if(furahitechPay.paymentRequest!!.paymentPlans.toList().isNotEmpty()) VISIBLE else GONE
        plansOptions.visibility = visibility

        if(furahitechPay.paymentRequest!!.paymentPlans.isNotEmpty()){
            furahitechPay.paymentRequest!!.paymentPlans.toList().forEach {
                paymentDuration.add(it.second.toList()[0].first * if(paymentRequest.basePlan.toLowerCase() == "month") 30 else if(paymentRequest.basePlan.toLowerCase() == "week") 7 else 1)
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

    override fun setMobileOperatorIcon(operator: MobilePresenter.MobileOperator) {
        operatorIcon.setImageResource(operator.icon)
        this.currentMno = operator.index
    }

    override fun getBaseContext(): Context { return activity!!}

    override fun showError(message: String) {
        Snackbar.make(coordinatorLayout,message,Snackbar.LENGTH_LONG).show()
    }

    override fun setButtonProps(btnColor: Int, txtColor: Int) {
        startPaymentBtn.isEnabled
        startPaymentBtn.setBackgroundColor(btnColor)
        startPaymentBtn.setTextColor(txtColor)
    }

    override fun setButtonEnabled(enabled: Boolean) {
        startPaymentBtn.isEnabled = enabled
    }

    override fun showPaymentResponse(response: TokenResponse) {
        currentToken = response.paymentToken
        progressBar.visibility = GONE
        mainHolder.visibility = VISIBLE
        tokenDetailsHolder.visibility = VISIBLE
        val instruction: Spanned = if(furahitechPay.isEnglish) Html.fromHtml(
            String.format(response.instructions,
                "Which is ${response.paymentToken}", "Which is $selectedPrice"
            ) + "<br/> Your payment token is <big><b> ${response.paymentToken}</b></big>"
        ) else Html.fromHtml(
            String.format(response.instructions,
                "Ambayo ni ${response.paymentToken}", "Ambacho ni $selectedPrice"
            ) + "<br/> Token namba yako ya malipo ni <big><b> ${response.paymentToken}</b></big>"
        );
        howToPayInfo.text = instruction
        startPaymentBtn.text = languageMap[LABEL_BUTTON_INFO]
    }

    override fun showProgressVisible(show: Boolean) {
        mainHolder.visibility = GONE
        progressBar.visibility = VISIBLE
        paymentDetailsHolder.visibility = GONE
        contactDetailsHolder.visibility = GONE

    }


    companion object{
        lateinit var callback: PayCallback

        fun formatPrice(price: Int, currency: String?): String {
            return  "${String.format("%,d", price)} ${(currency ?: "")}"
        }

        fun getInstance(callback: PayCallback): MobileFragment {
            this.callback = callback
            return MobileFragment()
        }
    }
}