package nz.non.miqrefresh.scan.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import nz.non.miqrefresh.databinding.FragmentMainBinding
import nz.non.miqrefresh.scan.vm.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBtn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBtn() {
        binding.startBtn.setOnClickListener {
            if (binding.startBtn.text == getString(nz.non.miqrefresh.R.string.start)) {
                binding.isMiq.visibility = View.GONE
                binding.startBtn.text = getString(nz.non.miqrefresh.R.string.stop)
                binding.startTime.text = getCurrentTime()

                viewModel.startScan(!binding.isMiq.isChecked)
                    .observe(viewLifecycleOwner) { upgradeResult(it) }
            } else {
                binding.isMiq.visibility = View.VISIBLE
                binding.startBtn.text = getString(nz.non.miqrefresh.R.string.start)
                viewModel.stopScan()
            }
        }
    }

    private fun upgradeResult(mainUIO: MainUIO) {
        if (mainUIO.arrivalDates.isNotBlank()) {
            setAvailability(mainUIO.arrivalDates)
        }
        binding.responseCode.text = mainUIO.responseCode
        binding.progressText.text = mainUIO.process
    }

    private fun setAvailability(arrivalDates: String) {
        binding.availableDates.text = arrivalDates
        playSound()
        binding.timeAvailable.text = getCurrentTime()
    }

    private fun getCurrentTime(): String {
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return currentTime
    }

    private fun playSound() {
        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 1500)
    }
}