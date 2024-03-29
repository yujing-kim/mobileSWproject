package com.example.healdoc_mobile_5
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.healdoc_mobile_5.R
import com.example.healdoc_mobile_5.model.Pharm
//import com.example.healdoc_mobile_5.model.Url
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.healdoc_mobile_5.model.Date
import com.example.healdoc_mobile_5.model.SideMed
//import sun.jvm.hotspot.utilities.IntArray
//import android.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class QrReaderActivity : AppCompatActivity() {
    internal var bitmap: Bitmap? = null
    private var iv: ImageView? = null
    private var linkview: TextView? = null
    var urllink: String? = null
    var dates: Int = -1
    private lateinit var urlReference: DatabaseReference
    private lateinit var dateReference: DatabaseReference
    var user = "홍길동"
    var date_arr : ArrayList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_reader)

        if (intent.hasExtra("UserName")) {
            if(intent.getStringExtra("UserName") == " "){
                user = "홍길동"
            }
            else{
                user = intent.getStringExtra("UserName") //유저 이름 받아오기
            }
            Log.d("name!!!!!", "$user")
        } else{
            Toast.makeText(this, "전달된 유저 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        iv = findViewById(R.id.iv) as ImageView
        linkview = findViewById(R.id.linkview) as TextView

        collecDates()
        //getUrl()

    }

    //@Throws(WriterException::class)
    private fun TextToImageEncode(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        Log.d("QrReaderActivitiy", "try bitMat!!")
        try {
            bitMatrix = MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                QRcodeWidth, QRcodeWidth, null
            )

        } catch (Illegalargumentexception: IllegalArgumentException) {

            return null
        }

        val bitMatrixWidth = bitMatrix.getWidth()

        val bitMatrixHeight = bitMatrix.getHeight()

        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth

            for (x in 0 until bitMatrixWidth) {

                pixels[offset + x] = if (bitMatrix.get(x, y))
                    resources.getColor(R.color.black)
                else
                    resources.getColor(R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        Log.d("QrReaderActivitiy", "return next!!")
        return bitmap
    }

    companion object {

        val QRcodeWidth = 500
        private val IMAGE_DIRECTORY = "/QRcodeDemonuts"
    }


    fun latestDate(){
        for(temp in date_arr){
            if(temp >= dates){
                dates = temp
            }
        }

        getUrl()
    }

    fun collecDates(){
        //디비에서 환자/처방전 아래의 날짜들 쭈욱 받아오기

        dateReference = FirebaseDatabase.getInstance().getReference("$user/처방전")
        val dateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val onedate : String = snapshot.key.toString()
                    Log.d("QrReaderActivity","날짜한개!!!: ${onedate}")
                    if(onedate!= null){
                        date_arr.add(
                            onedate.toInt()
                        )
                    }
                    else{
                        Log.d("QrReaderActivity","date가 없단다..")
                    }
                }
                latestDate()
                Log.d("QrReaderActivity", "최신 날짜!!: $dates")
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("QrReaderActivity", "loading db error!!!")
            }
        }

        dateReference.addValueEventListener(dateListener)
    }

    fun getUrl(){
        //위에서 받아온 최신 날짜를 기준으로 처방목록 불러오기
        urlReference = FirebaseDatabase.getInstance().getReference("$user/처방전/$dates/url")

        val urlListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                urllink = dataSnapshot.getValue().toString()
                Log.d("QrReaderActivity!", "In Listener name: $user")
                Log.d("QrReaderActivity", "In listener url: ${urllink}")
                if (urllink != null) {
                    bitmap = TextToImageEncode(urllink!!)
                    Log.d("QrReaderActivity", "url은?????? : ${urllink}")
                    iv!!.setImageBitmap(bitmap)

                    val mTransform = object : Linkify.TransformFilter {
                        override fun transformUrl(match: Matcher, url: String): String {
                            return ""
                        }
                    }
                    val pattern1 = Pattern.compile("나의 처방 내역 자세히 보기")
                    Linkify.addLinks(linkview, pattern1, urllink,null,mTransform);
                }
                else {
                    Log.d("QrReaderActivitiy", "error here!!")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("QrReaderActivity", "loadUrl:onCancelled", databaseError.toException())
            }
        }
        urlReference.addValueEventListener(urlListener)

    }
}