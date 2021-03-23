package com.qm.demoapplication.bean

import android.net.Uri

data class PhotoBean (
     var photoName:String,
     var photoFile:Uri,
     var photoDesc:String
){
     var isChecked=false
}