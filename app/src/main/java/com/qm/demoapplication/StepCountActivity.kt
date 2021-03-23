package com.qm.demoapplication

import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_step_count.*

class StepCountActivity : AppCompatActivity(),SensorEventListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_count)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL)
    }



    override fun onSensorChanged(event: SensorEvent) {
        println("步数-->${event.values[0]}")

        tv_step_count.setText("步数${event.values[0]}")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged-->${sensor}")
    }


}