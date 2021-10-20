package hu.unideb.flipper

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatDelegate
import com.tapadoo.alerter.Alerter
import hu.unideb.flipper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var isDice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // No night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpSensor()
        setUpTabBar()
    }

    private fun setUpTabBar(){
        binding.bottomNav.setOnItemSelectedListener {
            if(it == R.id.nav_dice){
                isDice = true
                binding.ivCoin.visibility = View.GONE
                binding.ivDice.visibility = View.VISIBLE
            }
            else{
                binding.ivCoin.visibility = View.VISIBLE
                binding.ivDice.visibility = View.GONE
                isDice = false
            }
        }
    }

    private fun setUpSensor(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_NORMAL,SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    private fun coinFlip(ImageId: Int,WinText: String){
        binding.ivCoin.animate().apply {
            duration = 2000 // 2sec
            rotationYBy(360f)
            rotationXBy(3600f)

        }.withEndAction(){
            binding.ivCoin.setImageResource(ImageId)
            setUpSensor()
            Alerter.Companion.create(this).setTitle(WinText).setIcon(R.drawable.ic_party_trumpet_svgrepo_com).setBackgroundColorRes(R.color.purple_500).setDuration(2000).show()
        }.start()
    }

    private fun diceFlip(ImageId: Int,WinText: String){
        binding.ivDice.animate().apply {
            duration = 2000
            rotationBy(3600f)

        }.withEndAction {
            binding.ivDice.setImageResource(ImageId)
            Alerter.Companion.create(this).setTitle(WinText).setIcon(R.drawable.ic_party_trumpet_svgrepo_com).setBackgroundColorRes(R.color.purple_500).setDuration(2000).show()
            setUpSensor()
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = p0.values[0]
            val upDown = p0.values[1]
            if((upDown >= 10 || sides >= 10) && !isDice){
                sensorManager.unregisterListener(this)
                val random = (0..1).random()
                if(random == 0){
                    coinFlip(R.drawable.ic_heads,"Heads")
                }
                else{
                    coinFlip(R.drawable.ic_tails,"Tails")
                }

            }
            else if((upDown >= 10 || sides >= 10) && isDice){
                sensorManager.unregisterListener(this)
                when((1..6).random()){
                    1 -> diceFlip(R.drawable.ic_dice_1,"1")
                    2 -> diceFlip(R.drawable.ic_dice_2,"2")
                    3 -> diceFlip(R.drawable.ic_dice_3,"3")
                    4 -> diceFlip(R.drawable.ic_dice_4,"4")
                    5 -> diceFlip(R.drawable.ic_dice_5,"5")
                    6 -> diceFlip(R.drawable.ic_dice_6,"6")
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        println("asd")
    }


}