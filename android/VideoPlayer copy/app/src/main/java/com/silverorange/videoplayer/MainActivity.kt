package com.silverorange.videoplayer

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : AppCompatActivity() {
    lateinit var mPlayerView: StyledPlayerView
    lateinit var mExoPlayer: ExoPlayer
    lateinit var mMediaItem: MediaItem
    lateinit var mPrevious: ImageButton
    lateinit var mPlay: ImageButton
    lateinit var mPause: ImageButton
    lateinit var mNext: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPlayerView = findViewById(R.id.mPlayerView)
        mPlayerView.hideController()
        mExoPlayer = ExoPlayer.Builder(applicationContext).build()
        mPlayerView.player = mExoPlayer;
        mMediaItem =
            MediaItem.fromUri("https://d140vvwqovffrf.cloudfront.net/media/5e87b9a811599/full/720.mp4")
        mExoPlayer.setMediaItem(mMediaItem)
        mExoPlayer.prepare()
        mExoPlayer.play()

        var mController: LinearLayout = findViewById(R.id.mController)
        mController.bringToFront()

        mPrevious = findViewById(R.id.mPrevious)
        mPlay = findViewById(R.id.mPlay)
        mPause = findViewById(R.id.mPause)
        mNext = findViewById(R.id.mNext)
        mPlay.setVisibility(View.GONE)

        mPlay.setOnClickListener {
            mExoPlayer.play()
            mPause.setVisibility(View.VISIBLE)
            mPlay.setVisibility(View.GONE)
        }

        mPause.setOnClickListener {
            mExoPlayer.pause()
            mPause.setVisibility(View.GONE)
            mPlay.setVisibility(View.VISIBLE)
        }

        setProgressDialog()
    }

    fun setProgressDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_view)
        val dialog: AlertDialog = builder.create()
        dialog.setContentView(R.layout.progress_view)
        dialog.show()

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams

            // Disabling screen touch to avoid exiting the Dialog
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}