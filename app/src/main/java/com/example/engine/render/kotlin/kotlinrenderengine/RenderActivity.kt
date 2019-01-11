package com.example.engine.render.kotlin.kotlinrenderengine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.engine.render.kotlin.kotlinrenderengine.ui.render.RenderFragment

class RenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.render_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RenderFragment.newInstance())
                .commitNow()
        }
    }

}
