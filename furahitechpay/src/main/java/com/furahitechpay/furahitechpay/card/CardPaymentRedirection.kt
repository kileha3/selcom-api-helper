package com.furahitechpay.furahitechpay.card

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.furahitechpay.furahitechpay.FurahitechPay
import com.furahitechpay.furahitechpay.R
import java.util.concurrent.TimeUnit


class CardPaymentRedirection : AppCompatActivity() {

    private var redirectionUrl = ""

    private lateinit var secureWebView: WebView

    private lateinit var toolbar: Toolbar

    private lateinit var progressDialog: ProgressBar

    private val isEnglish = FurahitechPay.instance.isCardEnglish

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment_redirection)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        redirectionUrl = intent.getStringExtra(REDIRECTION_URL)!!

        toolbar = findViewById(R.id.tool_bar)
        secureWebView = findViewById(R.id.secure_web)
        progressDialog = findViewById(R.id.progressBar)

        val toolBarTitle = findViewById<TextView>(R.id.tool_bar_title)
        toolBarTitle.text = if(isEnglish) "Card Payment" else "Malipo ya kadi"

        setToolbar()

        setUpWebView()

    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        if (::toolbar.isInitialized) {
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeButtonEnabled(true)
            }
            toolbar.title = ""
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            secureWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            secureWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        secureWebView.clearCache(true)

        secureWebView.webChromeClient = WebChromeClient()
        secureWebView.clearHistory()
        secureWebView.settings.javaScriptEnabled = true
        secureWebView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        secureWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        this.progressDialog.progress = 0
        this.progressDialog.max = 100
        secureWebView.setBackgroundColor(Color.TRANSPARENT)
        secureWebView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        renderWebPage()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && secureWebView.canGoBack()) {
            secureWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        secureWebView.clearHistory()
        secureWebView.clearFormData()
    }

    override fun onBackPressed() {
        showCancelDialog()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            showCancelDialog()
        }
        return true
    }

    inner class  MyJavaScriptInterface{
        @JavascriptInterface
        public fun processHTML(html:String){
            if(html.toLowerCase().contains("STATUS SUCCESS".toLowerCase()) || html.toLowerCase().contains("STATUS FAILED".toLowerCase())){
                Handler().postDelayed({ finish() }, TimeUnit.SECONDS.toMillis(5))
            }
        }
    }


    private fun showCancelDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if(isEnglish) "Cancel Payment?"  else "Ghairi malipo ")
        builder.setMessage(if(isEnglish) "This will stop all the payment processes going on right now, would you like to proceed?"
        else "Hatua hii ita sitisha malipo yoyote yanayo endelea kwa sasa, ungependa kuendelea?")
        builder.setPositiveButton(if(isEnglish) "OK" else "NDIO") { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        builder.setNegativeButton(if(isEnglish) "CANCEL" else "HAPANA") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun renderWebPage(){
        secureWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                progressDialog.visibility = View.GONE
                secureWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
            }


            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        secureWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressDialog.progress = newProgress
                progressDialog.visibility = if(newProgress == 100) View.GONE else View.VISIBLE
            }
        }
        secureWebView.loadUrl(redirectionUrl)
    }



    companion object{
        const val REDIRECTION_URL = "redirection_url"
    }
}
