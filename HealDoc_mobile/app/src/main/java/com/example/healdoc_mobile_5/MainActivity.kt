package com.example.healdoc_mobile_5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.healdoc_mobile_5.ui.main.SectionsPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var user = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //var user = "홍길동"
        //노티피케이션 채널 생성
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "MEDICATION_AlARM_CHANNEL"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )

            notificationManager.createNotificationChannel(mChannel)
        }




        // first of all add design library.
        // now create an adapter class.

        val fragmentAdapter = SelectActivity(supportFragmentManager)
//        fragmentAdapter.uuser = "박광운"
        view_pager.adapter = fragmentAdapter

        tabs.setupWithViewPager(view_pager)

        btn_QR.setOnClickListener(this)
        btn_mp.setOnClickListener(this) //마이페이지

        if (intent.hasExtra("UserName")) {
            user = intent.getStringExtra("UserName")
            user_name.text = user
            fragmentAdapter.uuser = "${user_name.text}"
            Log.e("로그인에서 유저이름 전달받음", "${user_name.text}")
        } else {
//            fragmentAdapter.uuser = "김유진"
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View){
        when (view.id){
            R.id.btn_QR -> {
                Log.e("qr", user)
                val intent = Intent(this, QrReaderActivity::class.java)
                intent.putExtra( "UserName", user) // 넘길 intent에 extra넣겠다.

                startActivity(intent)
            }
        }

        when (view.id){
            R.id.btn_mp -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}