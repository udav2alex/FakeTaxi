package ru.gressor.faketaxi

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.gressor.faketaxi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isOnStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        binding.car.setOnClickListener {
            val car = it as Car

            val screenHeight = binding.root.height
            val screenWidth = binding.root.width

            car.runCommandsList(
                if (isOnStart) {
                    listOf(
                        Car.Command.MoveForward(screenHeight - 580f),
                        Car.Command.RotateTo(Car.Direction.Right),
                        Car.Command.MoveForward(screenWidth - 400f),
                        Car.Command.RotateTo(Car.Direction.Down)
                    )
                } else {
                    listOf(
                        Car.Command.MoveForward(screenHeight - 580f),
                        Car.Command.RotateTo(Car.Direction.Left),
                        Car.Command.MoveForward(screenWidth - 400f),
                        Car.Command.RotateTo(Car.Direction.Up)
                    )
                }
            )
            isOnStart = !isOnStart
        }
    }
}