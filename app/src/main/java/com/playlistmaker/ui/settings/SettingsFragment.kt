package com.playlistmaker.ui.settings


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.playlistmaker.ui.presentation.IntentManager
import com.playlistmaker.ui.presentation.IntentType
import com.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding

import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    private val vm by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSwitcherState()
        applyOnClickListeners()
    }

    private fun applyOnClickListeners() {
        binding.apply {


            swDarkTheme.setOnCheckedChangeListener { _, checked ->
                vm.saveTheme(checked)
            }

            tvShare.setOnClickListener {
                IntentManager.startIntentByType(IntentType.SHARE, requireContext())
            }

            tvSupport.setOnClickListener {
                IntentManager.startIntentByType(IntentType.SUPPORT, requireContext())
            }

            tvAgreement.setOnClickListener {
                IntentManager.startIntentByType(IntentType.AGREEMENT, requireContext())
            }
        }
    }

    private fun setSwitcherState() {
        systemThemeToVM(vm::checkTheme)
        val switcherState = vm.getDarkThemeLiveData().value
        if (switcherState != null) {
            binding.swDarkTheme.isChecked = switcherState

            vm.getDarkThemeLiveData().observe(viewLifecycleOwner) {
                binding.swDarkTheme.isChecked
            }
        }
    }

    private fun systemThemeToVM(checkTheme: (Boolean) -> Unit) {
        checkTheme(
            ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
