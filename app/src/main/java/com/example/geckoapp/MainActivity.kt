package com.example.geckoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
//import android.R



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view: GeckoView = findViewById<GeckoView>(R.id.geckoview)
        val session = GeckoSession()
        val runtime: GeckoRuntime = GeckoRuntime.create(this)

        session.open(runtime)
        view.setSession(session)
        session.loadUri("about:buildconfig") // Or any other URL...

    }
}