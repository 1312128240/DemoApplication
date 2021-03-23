package com.qm.demoapplication

import android.Manifest
import android.content.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.qm.demoapplication.adapters.PickerAdapter
import com.qm.demoapplication.bean.PhotoBean
import kotlinx.android.synthetic.main.activity_image_picker.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.FileNameMap
import java.net.URLConnection
import java.util.*


class ImagePickerActivity : AppCompatActivity() {

    private var pickerAdapter: PickerAdapter?=null
    private var showCamer=true

    private var mImageUri:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)

        iv_back.setOnClickListener {
            finish()
        }

        tv_sure.setOnClickListener {
            val selectList = pickerAdapter?.getSelectList()
            if (selectList?.size==0){
                Toast.makeText(this, "请选择照片", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, selectList.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    private fun getAllPhoto(){

        val cursor= contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        val photoList= arrayListOf<PhotoBean>()
        while (cursor!!.moveToNext()) {
            //获取图片的名称
            val name: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))?:"暂无"
            //获取图片的生成日期
            val data: ByteArray = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val path= String(data, 0, data.size - 1)
            //获取图片的详细信息
            val desc=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION))?:"暂无"
            //获取图片的ID
            val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))

            val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
            val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
//            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
            val size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))

            Log.e("图片信息:", "${id}-->${name}--->${data}--->${desc}--->${path}-->${title}-->${mimeType}-->${size}")

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                // val imageContentUri = getImageContentUri(this, path)
                val baseUri: Uri = Uri.parse("content://media/external/images/media")
                val resultUri=Uri.withAppendedPath(baseUri, "" + id)
                if (resultUri!=null){
                    photoList.add(PhotoBean(name, resultUri, desc))
                }
            }else{
                val u=Uri.fromFile(File(path))
                photoList.add(PhotoBean(name, u, desc))
            }

        }
        cursor?.close()

        if (showCamer){
            photoList.add(0, PhotoBean("CamerView", Uri.EMPTY, ""))
        }

        pickerAdapter= PickerAdapter(this, photoList)
        val layoutManager=GridLayoutManager(this, 4)
        recy_picker.layoutManager=layoutManager
        recy_picker.addItemDecoration(SpaceManager())
        recy_picker.adapter=pickerAdapter
        pickerAdapter?.listener=object :PickerAdapter.ImagePickerListener{
            override fun onClickCamer() {
                ActivityCompat.requestPermissions(this@ImagePickerActivity, arrayOf(Manifest.permission.CAMERA), 102)
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==101){
            var hasPermission=true
            grantResults.forEach {
                if (it==-1){
                    hasPermission=false
                    return@forEach
                }
            }

            if (hasPermission){
                getAllPhoto()
            }else{
                finish()
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
            }
        }else if (requestCode==102){
            var hasPermission=true
            grantResults.forEach {
                if (it==-1){
                    hasPermission=false
                    return@forEach
                }
            }

            if (hasPermission){
                openCamer()
            }else{
                Toast.makeText(this, "开启相机权限", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun deleteAudioCollection(context: Context, id: Long) {
//        val contentResolver = context.contentResolver
//        val selection = "${MediaStore.Audio.Media._ID} = ?"
//        val selectionArgs = arrayOf(id.toString())
//        val audioUri = ContentUris.withAppendedId(Uri.parse("content://media/external/images/media"), id)
//        audioUri.let {
//            context.contentResolver.openFileDescriptor(audioUri, "w")?.use {
//                val result = contentResolver.delete(audioUri, selection, selectionArgs)
//                Log.e("Android 10 删除", "deleteAudioCollection() called : $result")
//                it.close()
//            }
//        }

//        val filePath="${Environment.getExternalStorageDirectory()}/tencent/MicroMsg/WeiXin/wx_camera_1592297679027.jpg"
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        val mContentResolver = context.getContentResolver();
//        val where = MediaStore.Images.Media.DATA + "='" + filePath + "'";
//        val delete = mContentResolver.delete(uri, where, null);
//        Log.e("Android 旧版删除",delete.toString())

//        val fileApk="${Environment.getExternalStorageDirectory()}/app-release.apk"
//        val file=File(fileApk)
//        if (file.exists()){
//            file.delete()
//        }
//
//        MediaScannerConnection.scanFile(this, arrayOf(fileApk),null,object :MediaScannerConnection.OnScanCompletedListener{
//            override fun onScanCompleted(path: String?, uri: Uri?) {
//                 println("Android 删除Apk返回-->${path}-->${uri}")
//            }
//
//        })


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==103){
            iv_test.setImageURI(mImageUri)
            savePictureToGamlly(File(mImageUri.toString()))
        }
    }

    private fun openCamer() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //打开相机的Intent
        if (takePhotoIntent.resolveActivity(packageManager) != null) { //这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
          //  val imageFile?: File = createImageFile() //创建用来保存照片的文件
            val createImageFile = createImageFile()
            if (createImageFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0以上要通过FileProvider将File转化为Uri*/
                    mImageUri = FileProvider.getUriForFile(this, "com.qm.demoapplication.fileprovider", createImageFile)
                } else {
                    /*7.0以下则直接使用Uri的fromFile方法将File转化为Uri*/
                    mImageUri = Uri.fromFile(createImageFile)
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri) //将用于输出的文件Uri传递给相机
                startActivityForResult(takePhotoIntent, 103) //打开相机
            }
        }
    }



    private fun createImageFile():File?{
        try {
           val rootPath="${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/lhs"
            //  val rootPath="${Environment.getExternalStorageDirectory()}/Pictures/Screenshots"
            val rootFile=File(rootPath)
            if (!rootFile.exists()){
                rootFile.mkdirs()
            }
            val file=File(rootFile, "${System.currentTimeMillis()}.jpg")
            if (!file.exists()){
                file.createNewFile()
            }
            return file
        }catch (e:Exception){
            e.printStackTrace()
        }
        return  null
    }


    private fun savePictureToGamlly(files: File){
        println("拍照图片路径-->${files.path}")
//        val string1=file.path;
//        val string2=string1.substringBefore("my_images")
//        val string3=string1.substringAfter("my_images/")
//
//        val files=File(string3)
//        println("拍照路径截取-->${string2}-->${string3}--->${files.name}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fileName: String = files.getName()
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            val contentResolver: ContentResolver =getContentResolver()
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            try {
                val out: OutputStream? = contentResolver.openOutputStream(uri!!)
                val fis = FileInputStream(files)
                FileUtils.copy(fis, out!!)
                fis.close()
                out.close()
                MediaScannerConnection.scanFile(this, arrayOf(files.getPath()), arrayOf<String>("image/jpeg")) { path: String, uri: Uri? ->
                    println("拍照图片保存相册结果-->${path}-->${uri}")
                    Toast.makeText(this, "图片已成功保存到$path", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                println("拍照保存相册异常-->${e}")
                e.printStackTrace()
            }

        } else {
//            MediaScannerConnection.scanFile(this, arrayOf(files.getPath()), arrayOf<String>("image/jpeg")) { path: String, uri: Uri? ->
//                println("拍照图片保存相册结果-->${path}-->${uri}")
////                Toast.makeText(this, "图片已成功保存到$path", Toast.LENGTH_SHORT).show()
//            }
            MediaStore.Images.Media.insertImage(contentResolver, "Android/data/com.qm.demoapplication/files/Pictures/lhs/1614329541923.jpg", files.name, null);
        }

    }

}