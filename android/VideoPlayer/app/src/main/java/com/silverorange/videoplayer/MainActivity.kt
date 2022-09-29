package com.silverorange.videoplayer

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.silverorange.videoplayer.retrofit.RetrofitHelper
import com.silverorange.videoplayer.retrofit.VideoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var mPlayerView: StyledPlayerView
    lateinit var mExoPlayer: ExoPlayer
    lateinit var mMediaItem: MediaItem
    lateinit var mTxtTitle: TextView
    lateinit var mTxtAuthor: TextView
    lateinit var mTxtDescription: TextView
    lateinit var mPrevious: ImageButton
    lateinit var mPlay: ImageButton
    lateinit var mPause: ImageButton
    lateinit var mNext: ImageButton
    lateinit var dialog: AlertDialog
    lateinit var mVideoList: ArrayList<ApiResponse>
    var mIndex: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPlayerView = findViewById(R.id.mPlayerView)
        mTxtTitle = findViewById(R.id.mTxtTitle)
        mTxtAuthor = findViewById(R.id.mTxtAuthor)
        mTxtDescription = findViewById(R.id.mTxtDescription)
        mPlayerView.hideController()
        mExoPlayer = ExoPlayer.Builder(applicationContext).build()
        mPlayerView.player = mExoPlayer;

        var mController: LinearLayout = findViewById(R.id.mController)
        mController.bringToFront()

        mPrevious = findViewById(R.id.mPrevious)
        mPlay = findViewById(R.id.mPlay)
        mPause = findViewById(R.id.mPause)
        mNext = findViewById(R.id.mNext)
        mPlay.visibility = View.VISIBLE
        mPause.visibility = View.GONE

        mPlay.setOnClickListener {
            mExoPlayer.prepare()
            mExoPlayer.play()
            mPause.visibility = View.VISIBLE
            mPlay.visibility = View.GONE
        }

        mPause.setOnClickListener {
            mExoPlayer.pause()
            mPause.visibility = View.GONE
            mPlay.visibility = View.VISIBLE
        }

        mNext.setOnClickListener {
            mExoPlayer.stop()
            mExoPlayer.moveMediaItem(mIndex, mIndex + 1)
            mExoPlayer.seekTo(0)
            mIndex++
            setMediaPlayer(mIndex)
        }
        mPrevious.setOnClickListener {
            mExoPlayer.stop()
            mExoPlayer.moveMediaItem(mIndex, mIndex - 1)
            mExoPlayer.seekTo(0)
            mIndex--
            setMediaPlayer(mIndex)
        }

        getVideoList()
    }

    fun getVideoList() {
        setProgressDialog()
        val retrofitAPI = RetrofitHelper.getInstance().create(VideoApi::class.java)
        val call: Call<ArrayList<ApiResponse>> = retrofitAPI.getVideo()
        call!!.enqueue(object : Callback<ArrayList<ApiResponse>?> {
            override fun onResponse(
                call: Call<ArrayList<ApiResponse>?>, response: Response<ArrayList<ApiResponse>?>
            ) {
                if (response.isSuccessful) {
                    hideProgressDialog()
                    mVideoList = response.body()!!
                    val mediaItems: ArrayList<MediaItem> = ArrayList()
                    for (item in mVideoList) {
                        mediaItems.add(MediaItem.fromUri(item.fullURL))
                    }
                    mExoPlayer.setMediaItems(mediaItems)
                    mIndex = 0
                    setMediaPlayer(mIndex)


                }
            }

            override fun onFailure(call: Call<ArrayList<ApiResponse>?>, t: Throwable) {
                hideProgressDialog()
                Toast.makeText(this@MainActivity, "Fail to get the data..", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun setProgressDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.NewDialog)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_view)
        dialog = builder.create()
        dialog.show()

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams

            // Disabling screen touch to avoid exiting the Dialog
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    fun hideProgressDialog() {
        dialog.hide()
        dialog.dismiss()
    }

    fun setMediaPlayer(mCounter: Int) {
        mPause.visibility = View.GONE
        mPlay.visibility = View.VISIBLE
        mTxtTitle.setText(mVideoList.get(mCounter).title)
        mTxtAuthor.setText(mVideoList.get(mCounter).author.name)
        var mDescription: String = mVideoList.get(mCounter).description.replace(
            "\\n", System.getProperty("line.separator")
        )
        mTxtDescription.setText(mDescription);

        if (mCounter == 0) {
            mPrevious.visibility = View.INVISIBLE
        } else {
            mPrevious.visibility = View.VISIBLE
        }

        if (mCounter == mVideoList.size - 1) {
            mNext.visibility = View.INVISIBLE
        } else {
            mNext.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer.stop()
        mExoPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        mExoPlayer.stop()
        mExoPlayer.release()
    }
}