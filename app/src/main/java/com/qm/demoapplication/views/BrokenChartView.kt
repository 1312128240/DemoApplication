package com.qm.demoapplication.views

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.qm.demoapplication.MyUtils
import com.qm.demoapplication.bean.ChartBean
import java.util.*


class BrokenChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint=Paint()
    private var sharedPath=Path()

    private var dataList= arrayListOf<ChartBean>()
    private var maxValue=0f
    private var textOffset=5f
    private var path=Path()
    private var triangleSize=20  //小三角边长
    private var rectList= arrayListOf<RectF>()

    private var touchX=0f
    private var touchY=0f

    private var drawMarkerView=false

    private var markeViewWidth=200
    private var markeViewHeight=150
    private var markeIndex=-1


    init {
        paint.isAntiAlias=true

        val dateList= MyUtils.getDayByMonth(2021, 1)
        dateList.forEach {
            val value= Random().nextInt(5000)
            val chartBean=ChartBean(it!!, value)
            dataList.add(chartBean)
            if (maxValue<value){
                maxValue=value.toFloat()
            }
           // println("最大值-->${it}--->${value}-->${maxValue}")
        }

        maxValue=maxValue*1.2f
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width=MeasureSpec.getSize(widthMeasureSpec)

        var height=MeasureSpec.getSize(heightMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)


        if (heightMode!=MeasureSpec.EXACTLY){
             height=500
        }

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

             val ll=marginLeft+85f
             val tt=marginTop+20f
             val rr=width-marginRight-20f
             val bb=marginBottom+65f

             //y轴线
             path.moveTo(ll + (triangleSize / 2), tt)
             path.lineTo(ll, tt + triangleSize)
             path.lineTo(ll + triangleSize, tt + triangleSize)
             path.lineTo(ll + (triangleSize / 2), tt)
             path.close()
             paint.setColor(Color.BLACK)
             canvas?.drawPath(path,paint)
             canvas?.drawLine(ll + (triangleSize / 2), tt + triangleSize, ll + (triangleSize / 2), height - bb - (triangleSize / 2), paint)

             val yLength=(height-bb-(triangleSize/2))-(tt+triangleSize)-50f

             for (i in 0 until 5){
                 //y轴圆点
                 paint.strokeWidth=10f
                 paint.setColor(Color.BLACK)
                 paint.setStrokeCap(Paint.Cap.ROUND);
                 canvas?.drawPoint(ll + (triangleSize / 2), (height - bb - (triangleSize / 2)) - ((yLength / 4 * i)), paint)
                 if (i!=0){
                     paint.strokeWidth=2f
                     canvas?.drawLine(ll + (triangleSize / 2), (height - bb - (triangleSize / 2)) - ((yLength / 4 * i)), rr, (height - bb - (triangleSize / 2)) - ((yLength / 4 * i)), paint)
                 }

                 //y轴文本
                 paint.strokeWidth=2f
                 paint.setColor(Color.BLACK)
                 paint.textSize=28f
                 canvas?.drawText("${((maxValue / 4) * i).toInt()}", 20f, textOffset + (height - bb - (triangleSize / 2)) - (yLength / 4 * i), paint)
             }

             //x轴线
             path.moveTo(rr, height - bb - (triangleSize / 2))
             path.lineTo(rr - triangleSize, height - bb - triangleSize)
             path.lineTo(rr - triangleSize, height - bb)
             path.lineTo(rr, height - bb - (triangleSize / 2))
             path.close()
             canvas?.drawPath(path, paint)
             canvas?.drawLine(ll + (triangleSize / 2), height - bb - (triangleSize / 2), rr - triangleSize, height - bb - (triangleSize / 2), paint)

             val xItemSize=((rr-triangleSize)-(ll+(triangleSize/2))-25)/dataList.size

             var i3=1
             for((i1, i2) in dataList.withIndex()){

                 //x辆圆点
                 paint.strokeWidth=10f
                 paint.setColor(Color.BLACK)
                 paint.setStrokeCap(Paint.Cap.ROUND);
                 canvas?.drawPoint((ll + (triangleSize / 2)) + (i1 * xItemSize), height - bb - (triangleSize / 2), paint)


                 //x轴文本
                 paint.strokeWidth=2f
                 paint.setColor(Color.BLACK)
                 paint.textSize=22f
                 paint.style=Paint.Style.FILL
                 drawRotateText(canvas!!, i2.date
                         ?: "", (ll + (triangleSize / 2)) + (i1 * xItemSize) - textOffset, height - textOffset * 2, paint, -45f)


                 //画折线区域圆点
                 paint.strokeWidth=10f
                 paint.setStrokeCap(Paint.Cap.ROUND);
                 paint.setColor(Color.RED)
                 val y2=((i2.value)*yLength)/maxValue
                 val yPoint=(height-bb-(triangleSize/2))-y2
                 val xPoint=(ll+(triangleSize/2))+(i1*xItemSize)
                 canvas.drawPoint(xPoint, yPoint, paint)

                 //保存圆点坐标
                 val rect=RectF(xPoint - 30, yPoint - 30, xPoint + 30, yPoint + 30)
                 rectList.add(rect)

                 //画折线区域折线
                 if (i1==0){
                     path.moveTo(ll + (triangleSize / 2) + (i1 * xItemSize), yPoint)
                     sharedPath.moveTo(ll + (triangleSize / 2) + (i1 * xItemSize), yPoint)
                 }else{
                     path.lineTo(ll + (triangleSize / 2) + (i1 * xItemSize), yPoint)
                     sharedPath.lineTo(ll + (triangleSize / 2) + (i1 * xItemSize), yPoint)
                 }

//                if (i1==0){
//                    path.moveTo(ll+(triangleSize/2)+(i1*xItemSize),y2)
//                }else{
//                    //path.lineTo(ll+(triangleSize/2)+(i1*xItemSize),y2)
//                    path.quadTo(ll+(triangleSize/2)+(i3*xItemSize),y2,ll+(triangleSize/2)+((i3+1)*xItemSize),y2)
//                    i3+=1
//                }

             }

//             paint.strokeWidth=2f
//             paint.setColor(Color.RED)
//             paint.style=Paint.Style.STROKE
            // canvas?.drawPath(path, paint)

             //填充色
             //paint.shader=getShader()
             paint.style=Paint.Style.STROKE
             canvas?.drawPath(sharedPath,paint)


         if (drawMarkerView){
             showMakeView(canvas!!)
         }

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                for ((i1, i2) in rectList.withIndex()) {
                    if (i2.contains(touchX, touchY)) {
                        drawMarkerView = true
                        markeIndex = i1
                        invalidate()
                        //println("在区域范围内-->${i1}--->${touchX}:${touchY}-->${i2}")
                        break
                    } else {
                        // println("不在区域范围内")
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }



    fun showMakeView(canvas: Canvas){
        paint.setColor(Color.GREEN)
        if (touchX>(width/2)){
            canvas.drawRect(touchX - markeViewWidth - 50, touchY - (markeViewHeight / 2), touchX - 50, touchY + (markeViewHeight / 2), paint)
            canvas.translate(touchX - markeViewWidth, touchY - 50);
        }else{
            canvas.drawRect(touchX + markeViewWidth + 50, touchY - (markeViewHeight / 2), touchX + 50, touchY + (markeViewHeight / 2), paint)
            if (markeIndex!=-1){
//                val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
//                textPaint.textSize=24f
//                textPaint.setColor(Color.RED)
//                val content="${dataList.get(markeIndext).date}${dataList.get(markeIndext).value}"
//                val startString=dataList.get(markeIndext).date
//                val endString=dataList.get(markeIndext).value.toString()
//                val layoutBuilder = StaticLayout.Builder.obtain(content, startString.length, endString.length, textPaint, 500).build()
//                canvas.translate(touchX+50,touchY-50);
//                layoutBuilder.draw(canvas)

                canvas.translate(touchX + 50, touchY - 50);
            }
        }

        val textPaint=TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50f);
        val content="${dataList.get(markeIndex).date}${dataList.get(markeIndex).value}"
        val staticLayout=StaticLayout(content, textPaint, getTextWidth(textPaint,dataList.get(markeIndex).date), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
       // canvas.translate(touchX - markeViewWidth, touchY - 50);
        staticLayout.draw(canvas)
    }



    fun drawRotateText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, angle: Float){
        if(angle!= 0f){
            canvas.rotate(angle, x, y);
        }
        canvas.drawText(text, x, y, paint);
        if(angle!= 0f){
            canvas.rotate(-angle, x, y);
        }
    }


    fun getTextWidth(paint: Paint,str:String):Int{
       return paint.measureText(str).toInt()
    }


    private fun getShader(): Shader {
        val shadeColors = intArrayOf(Color.argb(255, 250, 49, 33), Color.argb(165, 234, 115, 9), Color.argb(200, 32, 208, 88))
        return  LinearGradient((measuredWidth/2).toFloat(), measuredHeight.toFloat(), (measuredWidth/2).toFloat(), 0f, shadeColors, null, Shader.TileMode.CLAMP)
    }
}