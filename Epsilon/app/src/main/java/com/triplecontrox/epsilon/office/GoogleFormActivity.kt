package com.triplecontrox.epsilon.office

import android.app.Dialog
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.WebAppInterface
import com.triplecontrox.epsilon.db.Office

class GoogleFormActivity : AppCompatActivity() {
    private lateinit var poorConnectionAlertDialog: MaterialAlertDialogBuilder
    private lateinit var googleFormView: WebView
    private lateinit var url: String
    private lateinit var site: Office
    private lateinit var task: String
    private lateinit var webviewLoadingProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_office)

        val bundle = intent.extras!!

        task = bundle.getString("task")!!
        site = bundle.getParcelable("details")!!

        // create alert to indicate poor internet connectivity
        poorConnectionAlertDialog = MaterialAlertDialogBuilder(this)
        poorConnectionAlertDialog.setCancelable(false)

        // create a progress bar to indicate web view is loading page
        val progress = AlertDialog.Builder(this)
        progress.setView(R.layout.progress)
        webviewLoadingProgressDialog = progress.create()
        webviewLoadingProgressDialog.setCancelable(false)
        webviewLoadingProgressDialog.show()

        url = "https://docs.google.com/forms/d/e/" +
                "1FAIpQLSfxPXMYXKXIEcbYKzN4QWjNEMYfkqPMWA4nSmYRMtX80mP4UA/viewform"
        googleFormView = findViewById(R.id.webview)

        val webSettings: WebSettings = googleFormView.settings
        webSettings.javaScriptEnabled = true

        googleFormView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, urlPage: String) {
                webviewLoadingProgressDialog.dismiss()
                if (urlPage == "data:text/html,chromewebdata") {
                    loadErrorPage(view)
                    dialog(url)
                }

                view.addJavascriptInterface(
                    WebAppInterface(applicationContext, bundle, task), "Android"
                )

                view.loadUrl(
                    "javascript: document.getElementsByClassName" +
                            "(\"freebirdFormviewerViewHeaderTitleRow\")[0].innerText = '"+task+"'; " +
                            "javascript:document.getElementsByName('entry.2059612622')[0].readOnly = true;" +
                            
                            "javascript:document.getElementsByName('entry.2059612622')[0].value = '"+site.SiteName+"';" +
                            "javascript:document.getElementsByName('entry.1941245306')[0].value = '"+site.Assignee+"';" +
                            "javascript:document.getElementsByName('entry.1087768675')[0].value = '1twos3fours5';" +

                            "javascript:document.getElementsByName('entry.1941245306')[0].readOnly = true;" +
                            "javascript:document.getElementsByName('entry.1087768675')[0].readOnly = true;"+
                            "javascript:document.getElementsByClassName(" +
                            "\"freebirdFormviewerViewNumberedItemContainer\")[5].hidden = true;")

                view.loadUrl("javascript: ( function() { " +
                        "if(document.getElementsByClassName('freebirdFormviewerViewResponseConfirmContentContainer').length > 0) {" +
                        "Android.officeForm(); } }) " +
                        "()")
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                webviewLoadingProgressDialog.dismiss()
                if (view != null) {
                    loadErrorPage(view)
                }
                dialog(url)
            }
        }

        googleFormView.loadUrl(url)
    }

    fun dialog(url: String){
        poorConnectionAlertDialog
            .setTitle("No Network Detected")
            .setMessage("It looks like you have a poor internet connection! Retry?")
            .setPositiveButton("YES"){ dialog, id ->
                webviewLoadingProgressDialog.show()
                googleFormView.loadUrl(url)
            }
            .setNegativeButton("NO"){ _, _ ->
                finish()
            }
            .create()
        val alert = poorConnectionAlertDialog.create()
        alert.show()
    }
    /** Instantiate the interface and set the context  */

    fun loadErrorPage(webView: WebView){
        val htmlData ="<html><body></div></body>"

        webView.loadUrl("about:blank");
        webView.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
        webView.invalidate();

    }
}