package com.qm.demoapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qm.demoapplication.R
import com.qm.demoapplication.MyUtils
import com.qm.demoapplication.bean.PhotoBean


class PickerAdapter(var mContext: Context, var lists: ArrayList<PhotoBean>) :RecyclerView.Adapter<PickerAdapter.PickerViewHolder>() {

    private var MaxSelectCount=3
    private var selectPhotoList= arrayListOf<PhotoBean>()

    
    class PickerViewHolder(v: View):RecyclerView.ViewHolder(v) {
        var iv=v.findViewById<ImageView>(R.id.iv_picker)

        var checkBox=v.findViewById<CheckBox>(R.id.cb_picker)

        var rView=v.findViewById<View>(R.id.rview)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
       val view=LayoutInflater.from(mContext).inflate(R.layout.item_picker, parent, false)
        return PickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {

        //view大小
        val itemLp = holder.rView.layoutParams
        itemLp.width=(MyUtils.getScreenWidth(mContext)-15)/ 4
        itemLp.height=itemLp.width


        if (lists[position].photoName=="CamerView"){
            holder.checkBox.visibility=View.GONE
            holder.iv.scaleType=ImageView.ScaleType.CENTER
           // holder.iv.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_baseline_camera_alt_24))
            val ivLp=holder.iv.layoutParams
            ivLp.width=(itemLp.width/1.5).toInt()
            ivLp.height=ivLp.height

            Glide.with(mContext).load(R.drawable.ic_baseline_camera_alt_24).into(holder.iv)

            holder.rView.setOnClickListener {
                   listener?.onClickCamer()
            }

        }else{
            Glide.with(mContext)
                  .load(lists[position].photoFile)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                            val lp = holder.iv.layoutParams
//                            lp.width =(Utils.getScreenWidth(mContext)-15)/ 4
//                            lp.height = lp.width
//                          //  holder.iv.scaleType = ImageView.ScaleType.FIT_XY
//                            holder.iv.setImageDrawable(resource)
//                            return true
//                        }
//
//                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                            return false
//                        }
//                    })
                    .error(R.mipmap.ic_launcher)
                    .into(holder.iv)

            holder.iv.scaleType=ImageView.ScaleType.CENTER_CROP
            holder.checkBox.visibility=View.VISIBLE
            holder.checkBox.isChecked=lists[position].isChecked
            holder.checkBox.setOnClickListener {
                if ((it as CheckBox).isChecked){
                    if (MaxSelectCount==selectPhotoList.size){
                        it.isChecked=false
                        Toast.makeText(mContext, "最多只能选择${MaxSelectCount}张", Toast.LENGTH_SHORT).show()
                    }else{
                        lists[position].isChecked=true
                        selectPhotoList.add(lists[position])
                    }
                }else{
                    lists[position].isChecked=false
                    selectPhotoList.remove(lists[position])
                }
            }

            holder.rView.setOnClickListener {  }
        }



    }

    override fun getItemCount(): Int {
       return lists.size
    }


     fun getSelectList():ArrayList<PhotoBean>{
        return selectPhotoList
    }


    var listener:ImagePickerListener?=null

    interface ImagePickerListener{
         fun onClickCamer()
    }
}