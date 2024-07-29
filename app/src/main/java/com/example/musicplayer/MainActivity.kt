package com.example.musicplayer

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var btnDownload: Button
    private lateinit var txtStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDownload = findViewById(R.id.btnDownload)
        txtStatus = findViewById(R.id.txtStatus)

        btnDownload.setOnClickListener {
            downloadAndPlayMusic("https://drive.google.com/uc?export=download&id=1IhkZLjw9eBBYSomWl4T_KKx6PSWN_FbX")
        }

        player = ExoPlayer.Builder(this).build()
    }

    private fun downloadAndPlayMusic(fileUrl: String) {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(fileUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val inputStream: InputStream = response.body?.byteStream() ?: return@Thread
                    val file = File(cacheDir, "downloadedMusic.mp3")
                    val outputStream: OutputStream = FileOutputStream(file)

                    inputStream.copyTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    runOnUiThread {
                        playMusic(file.absolutePath)
                        txtStatus.text = "Status: Playing"
                    }
                } else {
                    runOnUiThread {
                        txtStatus.text = "Status: Error downloading"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    txtStatus.text = "Status: Error"
                }
            }
        }.start()
    }

    private fun playMusic(filePath: String) {
        val mediaItem = MediaItem.fromUri(filePath)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
