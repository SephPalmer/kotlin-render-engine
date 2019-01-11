package com.example.engine.render.kotlin.kotlinrenderengine.ui.render

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.engine.render.kotlin.kotlinrenderengine.R

class RenderFragment : Fragment() {

    companion object {
        fun newInstance() = RenderFragment()
    }

    private lateinit var viewModel: RenderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.render_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RenderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
