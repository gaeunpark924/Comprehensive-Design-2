package com.example.flatload

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar   //material 의존성 추가
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity(){

    var permissions = arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        init()
    }
    private fun checkPermision(): ArrayList<String>{
        var rejectPermission = ArrayList<String>()
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                rejectPermission.add(permission)
            }
        }
        return rejectPermission
    }
    private fun checkRequest(tmpPermission: ArrayList<String>): ArrayList<String>{
        var showRequest = ArrayList<String>()
        for(permission in tmpPermission){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                showRequest.add(permission)
            }
        }
        return showRequest
    }
    private fun init(){
        //권한 설정
        initCamera()
        //카메라 버튼 클릭

        //앨범 버튼 클릭
    }
    private fun initCamera(){
        var tmpPermission = checkPermision()

        if (tmpPermission.isNotEmpty()){
            var tmpRequest = checkRequest(tmpPermission)

            if (tmpRequest.isNotEmpty())   //권한 요청을 한 번 거절한 경우
            {
                var listRequest = tmpRequest.toArray(arrayOfNulls<String>(tmpRequest.size))
                val snackBar = Snackbar.make(cameraLayout, "위험요소 추가를 위해 접근 권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)

                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this,
                        listRequest,
                        200)
                }
                snackBar.show()
            }
            else {   //권한 요청을 처음 하는 경우
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                    200
                )
            }
        }
        else{
            Log.i("카메라 권한 설정완료","")
        }
    }
    //권한에 대한 응답이 있을때 작동하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==200){ //허용받았으면
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){ //둘다 허용되면
            }
            else{
                Toast.makeText(this,"카메라 권한 제공에 허용하셔야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}