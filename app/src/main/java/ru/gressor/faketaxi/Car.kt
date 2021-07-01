package ru.gressor.faketaxi

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class Car @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defAttrs: Int = 0
) : View(context, attributeSet, defAttrs) {

    private val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = 5f
    }
    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            drawRoundRect(
                0f, 0f,
                width.toFloat(), height.toFloat(),
                ROUND_RX, ROUND_RY,
                bodyPaint
            )
            drawRoundRect(
                3f, 90f,
                width - 3f, height - 60f,
                ROUND_RX, ROUND_RX,
                glassPaint
            )
            drawRoundRect(
                6f, 130f,
                width - 6f, height - 80f,
                ROUND_RX, ROUND_RY,
                bodyPaint
            )
        }
    }

    fun runCommandsList(commands: List<Command>) {
        if (commands.isEmpty()) {
            this.isClickable = true
            return
        }
        this.isClickable = false

        val currentCommand = commands[0]
        val commandsTail = commands.subList(1, commands.size)

        when (currentCommand) {
            is Command.MoveForward -> moveForward(currentCommand.distance, commandsTail)
            is Command.RotateTo -> rotateTo(currentCommand.direction, commandsTail)
        }
    }

    private fun moveForward(distance: Float, nextCommands: List<Command> = listOf()) {
        val duration = (1000 * distance / speedPxPerSecond).toLong()
        val currentDirection = getDirection(this.rotation)

        this.animate()
            .yBy(distance * currentDirection.moveY)
            .xBy(distance * currentDirection.moveX)
            .setStartDelay(100L)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    runCommandsList(nextCommands)
                }
            })
            .start()
    }

    private fun rotateTo(direction: Direction, nextCommands: List<Command> = listOf()) {
        val currentDirection = getDirection(this.rotation)

        this.animate()
            .rotationBy(rotateTo(currentDirection, direction))
            .setStartDelay(100L)
            .setDuration(500L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    runCommandsList(nextCommands)
                }
            })
            .start()
    }

    private fun getDirection(rotation: Float): Direction = when (rotation % 360) {
        0f -> Direction.Up
        90f, -270f -> Direction.Right
        180f, -180f -> Direction.Down
        270f, -90f -> Direction.Left
        else -> throw RuntimeException("Wrong direction!")
    }

    private fun rotateTo(fromDirection: Direction, toDirection: Direction): Float {
        var diff = toDirection.rotation - fromDirection.rotation
        if (diff > 180) diff -= 360
        if (diff < -180) diff += 360

        return diff
    }

    sealed class Command {
        class MoveForward(val distance: Float): Command()
        class RotateTo(val direction: Direction): Command()
    }

    sealed class Direction(
        val rotation: Float,
        val moveX: Float,
        val moveY: Float
    ) {
        object Up : Direction(0f, 0f, -1f)
        object Right : Direction(90f, 1f, 0f)
        object Down : Direction(180f, 0f, 1f)
        object Left : Direction(270f, -1f, 0f)
    }

    companion object {
        private const val ROUND_RX = 10f
        private const val ROUND_RY = 20f
        var speedPxPerSecond = 500f
    }
}