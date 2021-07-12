package nz.non.miqrefresh

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.non.miqrefresh.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var progressCall: Int = 0
    private lateinit var binding: ActivityMainBinding
    private var isScan = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setBtn()
    }

    private fun setBtn() {
        binding.startBtn.setOnClickListener {
            if (binding.startBtn.text == getString(R.string.start)) {
                binding.startBtn.text = getString(R.string.finish)
                isScan = true
                binding.startTime.text = getCurrentTime()
                CoroutineScope(Dispatchers.IO).launch {
                    startScan()
                }
            } else {
                binding.startBtn.text = getString(R.string.start)
                isScan = false
            }
        }
    }

    private suspend fun startScan() {
        progressCall += 1
        val connection = Jsoup.connect("https://allocation.miq.govt.nz/portal/")
        try {
            val doc = connection.get()
            val calendar = doc.getElementById("accommodation-calendar-home")
            val arrivalDates = calendar?.attr("data-arrival-dates")
            if (!arrivalDates.isNullOrBlank()) {
                setAvailability(arrivalDates)
            }
            binding.responseCode.text = connection.response().statusCode().toString()
        } catch (e: IOException) {
            binding.responseCode.text = getString(R.string.Error)
        }

        binding.progressText.text = progressCall.toString()
        Thread.sleep(DELAY)
        if (isScan) {
            startScan()
        }
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

    companion object {
        private const val DELAY = 7000L
    }
}