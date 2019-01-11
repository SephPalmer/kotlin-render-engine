package com.example.engine.render.kotlin.kotlinrenderengine.ui.render

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.engine.render.kotlin.kotlinrenderengine.R

class RenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.render_activity)
        Log.d("Debug", "Render Activity Launched")
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RenderFragment.newInstance())
                .commitNow()
        }
    }

}
