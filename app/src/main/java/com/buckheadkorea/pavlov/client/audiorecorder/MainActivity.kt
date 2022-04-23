package com.buckheadkorea.pavlov.client.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.buckheadkorea.pavlov.client.audiorecorder.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE = 200

class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener{
    private lateinit var amplitudes: ArrayList<Float>
    val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording =false
    private var isPaused = false

    private lateinit var timer:Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)

        timer =Timer(this)

        binding.btnRecord.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
        }

        binding.btnList.setOnClickListener {
            //TODO
            Toast.makeText(this,"List button", Toast.LENGTH_SHORT).show()
        }

        binding.btnDone.setOnClickListener {
            stopRecorder()
            //TODO
            Toast.makeText(this,"Record saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnDelete.setOnClickListener {
            stopRecorder()
            File("$dirPath$filename.mp3")
        }
        binding.btnDelete.isClickable = false
    }
    private fun resumeRecording(){
        recorder.resume()
        isPaused = false
        binding.btnRecord.setImageResource(R.drawable.ic_pause)

        timer.start()
    }
    private fun pauseRecorder(){
        recorder.pause()
        isPaused = true
        binding.btnRecord.setImageResource(R.drawable.ic_record)

        timer.pause()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
    private fun startRecording(){
        Log.d("Audio recorder", "startRecording()")
        if(!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }
        //start recording
        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"
        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")
            println("$dirPath$filename.mp3")

            try{
                prepare()
            }catch(e:IOException){

            }
            start()
        }
        binding.btnRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false

        timer.start()

        binding.btnDelete.isClickable = true
        binding.btnDelete.setImageResource(R.drawable.ic_delete)

        binding.btnList.visibility = View.GONE
        binding.btnDone.visibility = View.VISIBLE

    }

    private fun stopRecorder(){
        timer.stop()
        recorder.apply {
            stop()
            release()
        }
        isPaused = false
        isRecording = false

        binding.btnList.visibility = View.VISIBLE
        binding.btnDone.visibility = View.GONE

        binding.btnDelete.isClickable = false
        binding.btnDelete.setImageResource(R.drawable.ic_delete_disabled)

        binding.btnRecord.setImageResource(R.drawable.ic_record)

        binding.tvTimer.text="00:00:00"
        amplitudes = binding.waveformView.clear()






    }

    override fun onTimerTick(duration: String) {
//        println(duration)
        binding.tvTimer.text = duration
        binding.waveformView.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}