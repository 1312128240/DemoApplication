package com.qm.demoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setClick()
    }

    private fun setClick(){
        findViewById<View>(R.id.btn_camer).setOnClickListener {
            val i=Intent(this,CamerXActivity::class.java)
            startActivity(i)
        }

        findViewById<View>(R.id.btn_image_picker).setOnClickListener {
            val i=Intent(this,ImagePickerActivity::class.java)
            startActivity(i)
        }


        btn_chart.setOnClickListener {
            val i=Intent(this,BrokenChartActivity::class.java)
            startActivity(i)
        }


        btn_columnar_chart.setOnClickListener {
            val i=Intent(this,Chart2Activity::class.java)
            startActivity(i)
        }

        btn_fingerprint.setOnClickListener {
            val i=Intent(this,FingerprintActivity::class.java)
            startActivity(i)
        }

        btn_scratch_card.setOnClickListener {
            val i=Intent(this,ScratchActivity::class.java)
            startActivity(i)
        }

        btn_step_count.setOnClickListener {
            val i=Intent(this,StepCountActivity::class.java)
            startActivity(i)
        }
    }
}