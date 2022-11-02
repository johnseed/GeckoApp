package com.example.geckoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import org.mozilla.geckoview.*
import org.mozilla.geckoview.WebExtension.*


class MainActivity : AppCompatActivity() {
    private var mPort: WebExtension.Port? = null



    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view: GeckoView = findViewById(R.id.geckoview)
        val session = GeckoSession()
        val runtime: GeckoRuntime = GeckoRuntime.create(this)

        session.open(runtime)
        view.setSession(session)

        val portDelegate: PortDelegate = object : PortDelegate {
            override fun onPortMessage(message: Any, port: WebExtension.Port) {
                Log.d("PortDelegate", "Received message from extension: $message")
            }

            override fun onDisconnect(port: WebExtension.Port) {
                // This port is not usable anymore.
                if (port === mPort) {
                    mPort = null
                }
            }
        }

        val messageDelegate: MessageDelegate = object : MessageDelegate {
            override fun onConnect(port: WebExtension.Port) {
                mPort = port
                mPort!!.setDelegate(portDelegate)
            }
        }

        val contentMessageDelegate: MessageDelegate = object : MessageDelegate {
            override fun onMessage(
                nativeApp: String,
                message: Any,
                sender: MessageSender
            ): GeckoResult<Any>? {
                // start login here
                val intent = android.content.Intent()
                    .setAction("com.xx.xx")
                    .setPackage("com.xx.xx")
                startActivityForResult(intent, 99)
                return null
            }
        }


        // Note that the lifetime of the extension is not tied with the lifetime of the GeckoRuntime instance. The extension persists even when your app is restarted. Installing at every start up is fine, but it could be slow. To avoid installing multiple times you can use WebExtensionRuntime.ensureBuiltIn, which will only install if the extension is not installed yet.
        // runtime.webExtensionController.installBuiltIn("resource://android/assets/messaging/")
        runtime.webExtensionController
            .ensureBuiltIn("resource://android/assets/messaging/", "messaging@example.com")
            .accept(
                { extension: WebExtension? ->
                    extension?.setMessageDelegate(messageDelegate, "browser") // To receive messages from the background script, call setMessageDelegate on the WebExtension object.
                    if (extension != null) {
                        session?.webExtensionController.setMessageDelegate(extension, contentMessageDelegate, "browser") // SessionController.setMessageDelegate allows the app to receive messages from content scripts.
                    }
                    Log.i("MessageDelegate", "Extension installed: $extension")
                },
                { e: Throwable? ->
                    Log.e("MessageDelegate", "Error registering WebExtension", e)
                })

        session.loadUri("about:buildconfig") // Or any other URL...  about:buildconfig
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 99){
            var phone: String? = data?.getStringExtra("user_phone_num")
            var token: String? = data?.getStringExtra("user_login_token")

            // send login result back to content
            val loginMessage = JSONObject()
            try {
                loginMessage.put("phone", phone)
                loginMessage.put("token", token)
            } catch (ex: JSONException) {
                throw RuntimeException(ex)
            }
            mPort?.postMessage(loginMessage);
        }
    }
}