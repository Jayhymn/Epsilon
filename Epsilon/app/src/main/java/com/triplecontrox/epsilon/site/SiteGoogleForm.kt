package com.triplecontrox.epsilon.site

import android.app.Dialog
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.WebAppInterface
import com.triplecontrox.epsilon.db.Maintenance

class SiteGoogleForm : AppCompatActivity() {
    private lateinit var poorConnectionAlertDialog: AlertDialog.Builder
    private lateinit var googleFormView: WebView
    private lateinit var url: String
    private lateinit var username: String
    private lateinit var site: Maintenance
    private lateinit var webviewLoadingProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_google_form)

        val bundle = intent.extras!!
        site = bundle.getParcelable("details")!!
        Log.d("SiteGoogle", site.Assignee)
        username = site.Assignee.toString()

        // create a poor connection dialog
        poorConnectionAlertDialog = AlertDialog.Builder(this)
        poorConnectionAlertDialog.setCancelable(false)

        // create page loading dialog
        val progress = AlertDialog.Builder(this)
        progress.setView(R.layout.progress)
        webviewLoadingProgressDialog = progress.create()
        webviewLoadingProgressDialog.setCancelable(false)
        webviewLoadingProgressDialog.show()

        url = "https://docs.google.com/forms/d/e/" +
                "1FAIpQLScDj9vpn24YDFnmwA-aFNgdiQqT-qV5v57Avr0iEvWOdF3cOA/viewform?"

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
                    WebAppInterface(this@SiteGoogleForm, bundle), "Android")
                view.loadUrl(
                    "javascript:document.getElementsByName('entry.665660413')[0].value = '"+username+"';" +
                            "javascript:document.getElementsByName('entry.1260559019')[0].value = '"+site.eqpCode+"';" +
                            "javascript:document.getElementsByName('entry.720123293')[0].value = '1twos3fours5';" +

                            "javascript:document.getElementsByName('entry.665660413')[0].readOnly = true;" +
                            "javascript:document.getElementsByName('entry.1260559019')[0].readOnly = true;" +
                            "javascript:document.getElementsByName('entry.720123293')[0].readOnly = true;"+
                            "javascript:document.getElementsByClassName(" +
                            "\"freebirdFormviewerViewNumberedItemContainer\")[12].hidden = true;")

                view.loadUrl("javascript: ( function() { " +
                        "if(document.getElementsByClassName('freebirdFormviewerViewResponseConfirmContentContainer').length > 0) {" +
                        "Android.siteForm(); } }) " +
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
    private fun dialog(url: String){
        poorConnectionAlertDialog
            .setTitle("No Network Detected")
            .setMessage("It looks like you have a poor internet connection! Retry?")
            .setPositiveButton("YES"){ _, _ ->
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

    private fun loadErrorPage(webView: WebView){
        val htmlData ="<html><body></div></body>"

        webView.loadUrl("about:blank")
        webView.loadDataWithBaseURL(null,htmlData,
            "text/html", "UTF-8",null)
        webView.invalidate()
    }
}
