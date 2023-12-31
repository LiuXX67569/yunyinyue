package com.example.neteasecloudmusic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object FileUtil {
    val USEREMAIL: String by lazy { "userEmail" }
    private val portrait: String by lazy { "portrait_image.jpg" }
    val MY_PERMISSIONS_REQUEST_CAMERA: Int by lazy { 0 }
    val MY_PERMISSIONS_REQUEST_ALBUM: Int by lazy { 1 }
    val TAKE_PHOTO: Int by lazy { 1 }
    val CHOOSE_PHOTO: Int by lazy { 2 }

    fun saveFile(context: Context, fileName: String, inputText: String) {
        try {
            val output = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val bufferWriter = BufferedWriter(OutputStreamWriter(output))
            bufferWriter.use {
                it.write(inputText)
                it.newLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readFile(context: Context, fileName: String): List<String> {
        val mutableList = mutableListOf<String>()
        try {
            val input = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                it.forEachLine {it1 ->
                    mutableList.add(it1)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return mutableList
    }

//    private fun SharedPreferences.open(block: SharedPreferences.Editor.() -> Unit) {
//        try {
//            val editor = edit()
//            editor.block()
//            editor.apply()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun saveBySharedPreferences(context: Context, fileName: String, map: Map<String, String>) {
//        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).open {
//            map.forEach {
//                putString(it.key, it.value)
//            }
//        }
//    }
//
//    fun readFromSharedPreferences(context: Context, fileName: String, key: String): String? {
//        var content: String? = null
//        try {
//            content = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
//                .getString(key, "").toString()
//        } catch (e: Exception) {
//            e.printStackTrace()
////            saveBySharedPreferences(
////                context, fileName,
////                mapOf(key to  Gson().toJson(
////                    UserInfoBean(),
////                    object: TypeToken<UserInfoBean>() {}.type
////                ))
////            )
//        }
//        return content
//    }

//    fun getUriFromPortrait(context: Context): Uri {
//        val outputImage = File(context.externalCacheDir, "portrait_image.jpg")
//        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            FileProvider.getUriForFile(
//                context,
//                "com.portrait.camera.fileProvider",
//                outputImage)
//        } else {
//            Uri.fromFile(outputImage)
//        }
//        return imageUri
//    }

    private fun createPortraitToUri(context: Context): Uri {
        val outputImage = File(context.externalCacheDir, portrait)
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "com.portrait.camera.fileProvider",
                outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        return imageUri
    }

    fun runCamera(context: Context): Intent {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, createPortraitToUri(context))
        return intent
    }

    fun openAlbum(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        return intent
    }

//    fun parseUriToMultipart(uri: Uri, fileName: String): MultipartBody.Part {
//        val file = File(uri.path.toString())
//        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData("image", encryption(fileName), requestFile)
//    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":")[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
    }

    fun albumPrepareFilePart(context: Context, partName: String, fileName: String, fileUri: Uri): MultipartBody.Part {
        val file = File(getRealPathFromURI(context, fileUri))
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestFile)
    }

    fun cameraPrepareFilePart(context: Context, partName: String, fileName: String): MultipartBody.Part {
        val file = File(context.externalCacheDir, portrait).absoluteFile
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestFile)
    }

    fun getUriForFile(context: Context, fileName: String): Uri {
        return FileProvider.getUriForFile(
            context,
            "com.portrait.camera.fileProvider",
            File(context.externalCacheDir, "test.mp3")
        )
    }
//    fun getPortrait(context: Context): Bitmap {
//        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(getUriFromPortrait(context)))
//    }
//
//    fun getBitmapFromUri(context: Context, uri: Uri)
//            = context.contentResolver.openFileDescriptor(uri, "r")?.use {
//        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
//    }

}