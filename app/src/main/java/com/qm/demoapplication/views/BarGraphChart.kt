package com.qm.demoapplication.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.qm.demoapplication.MyUtils
import com.qm.demoapplication.bean.ChartBean

class BarGraphChart(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint=Paint()
    private var marginSpace=50f
    private var dataList= arrayListOf<ChartBean>()
    private var yAxisValue= arrayListOf<Long>(1600,1400,1200,1000,800,600,400,200,0)
    private var columnWidth=60

    init {
        paint.isAntiAlias=true

        dataList.add(ChartBean("2012",727))
        dataList.add(ChartBean("2013",1029))
        dataList.add(ChartBean("2014",1301))
        dataList.add(ChartBean("2015",1438))
        dataList.add(ChartBean("2016",1469))
        dataList.add(ChartBean("2017",1465))
        dataList.add(ChartBean("2018",1404))
        dataList.add(ChartBean("2019",1376))
        dataList.add(ChartBean("2020",1496))


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthModel=MeasureSpec.getMode(widthMeasureSpec)
        val widthSize=MeasureSpec.getSize(widthMeasureSpec)

        val heightModel=MeasureSpec.getMode(heightMeasureSpec)
        val heightSize=MeasureSpec.getSize(heightMeasureSpec)

        if (heightModel==MeasureSpec.EXACTLY){
            setMeasuredDimension(widthSize,heightSize)
        }else{
            setMeasuredDimension(widthSize,500)
        }
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //1外部边框
        val ll=marginSpace+marginLeft
        val tt=marginSpace+marginTop
        val rr=width-marginSpace-marginRight
        val bb=height-marginSpace-marginBottom
        paint.setColor(Color.BLACK)
        paint.style=Paint.Style.STROKE
        canvas?.drawRect(ll,tt,rr,bb,paint)

        //2标题
        paint.textSize=42f
        paint.style=Paint.Style.FILL
        paint.setFakeBoldText(true);
        val title="2012-2020年全球智能手机出货量趋势"
        val titleStringWidth=MyUtils.getTextWidth(paint,title)
        val titleStringStart=(rr-ll-titleStringWidth)/2
        canvas?.drawText(title,ll+titleStringStart,marginSpace+80,paint)

        //3比例
        paint.textSize=32f
        paint.setColor(Color.parseColor("#303030"))
        val ratioString="出货量 (百万部)"
        val ratioStringWidth=MyUtils.getTextWidth(paint,ratioString)
        canvas?.drawText(ratioString,rr-ratioStringWidth-100,marginSpace+150,paint)
        canvas?.drawRect(rr-ratioStringWidth-135,marginSpace+125,rr-ratioStringWidth-110,marginSpace+150,paint)

        //4横轴
        paint.style=Paint.Style.FILL
        paint.strokeWidth=2f
        paint.textSize=28f
        paint.setColor(Color.BLACK)

        val h_axis_height=((bb-100)-(marginSpace+200))/8

        val columnSpace=((rr-100)-(ll+150)-(columnWidth*dataList.size))/(dataList.size-1)

        val yValuePx=(((bb-100)-(marginSpace+200)))/1600.0

        for ((i1,i2) in dataList.withIndex()){
            //横轴线
            canvas?.drawLine(ll+120,(marginSpace+200)+(h_axis_height*i1),rr-50,(marginSpace+200)+(h_axis_height*i1),paint)

            //y轴值文本
            paint.textAlign=Paint.Align.RIGHT
            canvas?.drawText(yAxisValue.get(i1).toString(),ll+90,(marginSpace+210)+(h_axis_height*i1),paint)

            //柱子
            val columnHeight=yValuePx*(i2.value)
            val columnL=(ll+150)+(columnWidth*i1)+(columnSpace*i1)
            val columnT=(bb-100-columnHeight).toFloat()
            val columnR=columnL+columnWidth
            val columnB=bb-100
            canvas?.drawRect(columnL,columnT,columnR,columnB,paint)

            //年份
            paint.textAlign=Paint.Align.LEFT
            canvas?.drawText(i2.date,columnL,bb-48,paint)

            //柱子上值
            canvas?.drawText(i2.value.toString(),columnL,columnT-20,paint)
        }




    }



}