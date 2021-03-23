package com.qm.demoapplication.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.qm.demoapplication.R


class ScratchCardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var mTransparentPaint= Paint()
    private var mFingerPath=Path()
    private var mBgBitmap //第一张图的 bitmap
            : Bitmap? = null
    private var mFgBitmap //第二章图的 bitmap
            : Bitmap? = null
    private var mCanvas //新建的画布，用于操作 mFgBitmap
            : Canvas? = null

   init {

       mTransparentPaint.isAntiAlias = true
       mTransparentPaint.alpha = 0 //设置成透明
       mTransparentPaint.style = Paint.Style.STROKE
       mTransparentPaint.strokeJoin = Paint.Join.ROUND //让笔触和连接处更加圆滑
       mTransparentPaint.strokeCap = Paint.Cap.ROUND
       mTransparentPaint.setStrokeWidth(30f)
       mTransparentPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

       mBgBitmap = BitmapFactory.decodeResource(resources, R.drawable.timg) //第一张图


       mFgBitmap= Bitmap.createBitmap(mBgBitmap!!.width, mBgBitmap!!.height, Bitmap.Config.ARGB_8888)
       mCanvas=Canvas(mFgBitmap!!);
       mCanvas!!.drawColor(Color.parseColor("#e3e3e3"));

   }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(mBgBitmap!!, 0f, 0f, null);
        canvas.drawBitmap(mFgBitmap!!,0f, 0f, null);

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mFingerPath.reset() //因为之前的效果已经作用在 fgBitmap 上了，所以需要重置 path ，避免对下一步造成影响
                mFingerPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> mFingerPath.lineTo(event.x, event.y)
            MotionEvent.ACTION_UP -> {
            }
        }
        mCanvas!!.drawPath(mFingerPath, mTransparentPaint) //使透明的效果作用到 fgBitmap 上。
        invalidate() //执行重绘
        return true
    }
}