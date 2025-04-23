package com.example.capstonepresentation.fragments.Home

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.capstonepresentation.repository.BluetoothRepository
import com.example.capstonepresentation.fragments.Home.HomeViewModel


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothRepository.power.collect {powerValue ->
                    viewModel.updatePower(powerValue)}
            }
        }

        viewModel.powerliveData.observe(viewLifecycleOwner) {powerValue ->
            binding.tvPower.text = "Power: $powerValue"
        }
    }
}

@Composable
fun HomeScreen() {

}