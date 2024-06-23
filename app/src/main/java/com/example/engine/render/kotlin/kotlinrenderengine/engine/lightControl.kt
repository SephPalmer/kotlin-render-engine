package com.example.engine.render.kotlin.kotlinrenderengine.engine

import android.opengl.GLES20
import kotlin.math.cos
import kotlin.math.sin

class LightControl {
    var direction: FloatArray = floatArrayOf(0f, -1f, -1f)
    var intensity: Float = 1.0f
    var ambientStrength: Float = 0.1f

    private var angleX: Float = 0f
    private var angleY: Float = 0f
    var isRotating: Boolean = false

    fun setDirection(x: Float, y: Float, z: Float) {
        direction = floatArrayOf(x, y, z)
        normalizeDirection()
    }

    fun rotateX(angle: Float) {
        if (isRotating) {
            angleX += angle
            updateDirection()
        }
    }

    fun rotateY(angle: Float) {
        if (isRotating) {
            angleY += angle
            updateDirection()
        }
    }

    private fun updateDirection() {
        val cosX = cos(angleX)
        val sinX = sin(angleX)
        val cosY = cos(angleY)
        val sinY = sin(angleY)

        direction[0] = sinY
        direction[1] = -sinX * cosY
        direction[2] = -cosX * cosY

        normalizeDirection()
    }

    private fun normalizeDirection() {
        val length = kotlin.math.sqrt(direction[0] * direction[0] + direction[1] * direction[1] + direction[2] * direction[2])
        direction[0] /= length
        direction[1] /= length
        direction[2] /= length
    }

    fun applyToShader(program: Int) {
        val directionHandle = GLES20.glGetUniformLocation(program, "u_LightDirection")
        val intensityHandle = GLES20.glGetUniformLocation(program, "u_LightIntensity")
        val ambientHandle = GLES20.glGetUniformLocation(program, "u_AmbientStrength")

        GLES20.glUniform3fv(directionHandle, 1, direction, 0)
        GLES20.glUniform1f(intensityHandle, intensity)
        GLES20.glUniform1f(ambientHandle, ambientStrength)
    }
}