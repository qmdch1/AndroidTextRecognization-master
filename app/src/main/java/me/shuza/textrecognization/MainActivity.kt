package me.shuza.textrecognization

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.SurfaceHolder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.regex.Pattern
import kotlin.properties.Delegates

/**
 *
 * :=  created by:  Shuza
 * :=  create date:  28-Jun-18
 * :=  (C) CopyRight Shuza
 * :=  www.shuza.ninja
 * :=  shuza.sa@gmail.com
 * :=  Fun  :  Coffee  :  Code
 *
 **/

class MainActivity : AppCompatActivity() {

    private var mCameraSource by Delegates.notNull<CameraSource>()
    private var textRecognizer by Delegates.notNull<TextRecognizer>()
    private var getResult : String = "don't have data"

    private val PERMISSION_REQUEST_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCameraSource()
        button.setOnClickListener{
            var intent = Intent(this@MainActivity, getLicense::class.java)
            intent.putExtra("license",getResult)
            startActivity(intent)
        }
    }

    private fun startCameraSource() {

        //  Create text Recognizer
        textRecognizer = TextRecognizer.Builder(this).build()

        if (!textRecognizer.isOperational) {
            toast("Dependencies are not loaded yet...please try after few moment!!")
            Logger.d("Dependencies are downloading....try after few moment")
            return
        }

        //  Init camera source to use high resolution and auto focus
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder?) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(surface_camera_preview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    toast("Error:  ${e.message}")
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                tv_result.post {
                    var stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    tv_result.text = stringBuilder.toString()

                    var result : String?
                    result = sort(stringBuilder.toString())
                    if(result != null || result == ""){
                        getResult = result
                    }
                }
            }
        })
    }

    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                mCameraSource.start(surface_camera_preview.holder)
            } else {
                toast("Permission need to grant")
                finish()
            }
        }
    }


    fun sort(getwords : String ): String? {


        //정렬한 데이터를 모아 놓은 스트링
        var imsisortwords = ArrayList<String>()
        var lastsortwords = ArrayList<String>()

        //대문자, 숫자 만 리스트에 삽입
        for (i in getwords.indices) {
            // 숫자, 영대문자 데이터를 imsi배열에 추가
            if (Pattern.matches("[0-9]", getwords[i].toString()) == true || Pattern.matches("[A-Z]", getwords[i].toString()) == true) {
                imsisortwords.add(getwords[i].toString())
            }
        }//for

        Log.e("", "imsisortwords = ${imsisortwords}")

        try {
            // imsi배열에 있는 값에서 lastsortwords에 번호판 추출값 삽입
            for (i in imsisortwords.indices) {

                if (Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 4]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 5]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 6]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 7]) == true) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    lastsortwords.add(imsisortwords[i + 5])
                    lastsortwords.add(imsisortwords[i + 6])
                    lastsortwords.add(imsisortwords[i + 7])
                    imsisortwords.clear()
                    break
                }// 1
                else if(Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 4]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 5]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 6]) == true
                        ) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    lastsortwords.add(imsisortwords[i + 5])
                    lastsortwords.add(imsisortwords[i + 6])
                    imsisortwords.clear()
                    break
                }// 2
                else if(Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 4]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 5]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 6]) == true) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    lastsortwords.add(imsisortwords[i + 5])
                    lastsortwords.add(imsisortwords[i + 6])
                    imsisortwords.clear()
                    break
                }// 3
                else if(Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 4]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 5]) == true
                        ) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    lastsortwords.add(imsisortwords[i + 5])
                    imsisortwords.clear()
                    break
                }// 4
                else if(Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 4]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 5]) == true) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    lastsortwords.add(imsisortwords[i + 5])
                    imsisortwords.clear()
                    break
                }// 5
                else if(Pattern.matches("[A-Z]", imsisortwords[i]) == true
                        && Pattern.matches("[0-9]", imsisortwords[i + 1]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 2]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 3]) == true
                        && Pattern.matches("[A-Z]", imsisortwords[i + 4]) == true) {
                    lastsortwords.add(imsisortwords[i])
                    lastsortwords.add(imsisortwords[i + 1])
                    lastsortwords.add(imsisortwords[i + 2])
                    lastsortwords.add(imsisortwords[i + 3])
                    lastsortwords.add(imsisortwords[i + 4])
                    imsisortwords.clear()
                    break
                }// 6
                else {
                    return null
                }
            }
        }catch (E: Exception){
            Log.e("", "catch error = $E")
            return null
        }

        Log.e("", "lastsortwords = $lastsortwords")
        return lastsortwords.toString()
    }//sort 메서드


}
