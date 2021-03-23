package com.qm.demoapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class MyUtils {

    companion object{

        fun getScreenWidth(mContext: Context):Int{
            val outMetrics = DisplayMetrics()
            val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.getDefaultDisplay().getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels
            return widthPixels
        }

        fun getScreenHeight(mContext: Context):Int{
            val outMetrics = DisplayMetrics()
            val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.getDefaultDisplay().getMetrics(outMetrics)
            val heightPixels = outMetrics.heightPixels
            return heightPixels
        }

        fun getDayByMonth(yearParam: Int, monthParam: Int): List<String?> {
            val list= arrayListOf<String>()
            val aCalendar: Calendar = Calendar.getInstance(Locale.CHINA)
            aCalendar.set(yearParam, monthParam, 1)
            val year: Int = aCalendar.get(Calendar.YEAR) //年份
            val month: Int = aCalendar.get(Calendar.MONTH) + 1 //月份
            val day: Int = aCalendar.getActualMaximum(Calendar.DATE)
            for (i in 1..day) {
                var aDate: String=""
                if (month < 10 && i < 10) {
                 //   aDate = "$year-0$month-0$i"
                    aDate = "$month/0$i"
                }
                if (month < 10 && i >= 10) {
                 //   aDate = "$year-0$month-$i"
                    aDate = "0$month/$i"
                }
                if (month >= 10 && i < 10) {
                  //  aDate = "$year-$month-0$i"
                    aDate = "$month/0$i"
                }
                if (month >= 10 && i >= 10) {
                  //  aDate = "$year-$month-$i"
                    aDate = "$month/$i"
                }
                list.add(aDate)
            }
            return list
        }

        fun getTextWidth(paint: Paint,str:String):Int{
            return paint.measureText(str).toInt()
        }
    }
}


class SpaceManager:RecyclerView.ItemDecoration(){

    var paint=Paint()

    init {
       // paint.setColor(Color.RED)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition2 = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val itemPosition = parent.getChildLayoutPosition(view)
        val spanCount=(parent.layoutManager as GridLayoutManager).spanCount

        //最底部不用绘制分割线
        val count=parent.adapter!!.itemCount
        val d = if (count % spanCount != 0) Math.ceil(count * 1.0 / spanCount) else Math.floor(count * 1.0 / spanCount)
        if ((itemPosition+1)%spanCount==0){
            val currentLine=(itemPosition+1)/(spanCount*1.0)
            if (currentLine==d){
                outRect.bottom=0
            }else{
                outRect.bottom=5
            }
        }else{
            val currentLine=(itemPosition+1)/(spanCount*1.0)
            if (Math.ceil(currentLine)==d){
                outRect.bottom=0
            }else{
                outRect.bottom=5
            }
        }


        //最右边一列不用绘制分割线
        if((itemPosition+1)%spanCount!=0){
             outRect.right=5
        }

    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount){
             val view=parent.getChildAt(i)

             c.drawRect(
                 view.left.toFloat(),
                 view.bottom.toFloat(),
                 view.right.toFloat(),
                 view.bottom.toFloat() + 5,
                 paint
             )
             c.drawRect(
                 view.right.toFloat(),
                 view.top.toFloat(),
                 view.bottom.toFloat(),
                 view.right.toFloat() + 5,
                 paint
             )
        }

    }


}