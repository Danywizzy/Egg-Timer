package com.danywizzy.timer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.danywizzy.timer.R
import com.danywizzy.timer.databinding.MainFragmentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import java.lang.Exception

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val fd = requireContext().assets.openFd("soung.wav")
            viewModel.initSound(fd)
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener { viewModel.onStartBtnClick() }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            viewModel.onItemSelected(item.itemId)
            true
        }

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.timerState
                    .collect { state ->
                        with(binding) {
                            when (state) {
                                is TimerState.Default -> {
                                    message.text = state.time
                                    startBtn.setText(R.string.start_btn)
                                    bottomNavigation.setItemsEnable(true)
                                }
                                is TimerState.Running -> {
                                    message.text = state.time
                                    startBtn.setText(R.string.stop_btn)
                                    bottomNavigation.setItemsEnable(false)
                                }
                                TimerState.Done -> {
                                    message.setText(R.string.done)
                                    startBtn.setText(R.string.start_btn)
                                    bottomNavigation.setItemsEnable(true)
                                }
                            }
                        }
                    }
            }
    }

    private fun BottomNavigationView.setItemsEnable(enable: Boolean){
        for(i in 0 until menu.size()){
            menu.get(i).isEnabled = enable
        }
    }
}