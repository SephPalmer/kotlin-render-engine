package com.example.engine.render.kotlin.kotlinrenderengine.engine

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.engine.render.kotlin.kotlinrenderengine.BuildConfig
import com.example.engine.render.kotlin.kotlinrenderengine.R
import com.example.engine.render.kotlin.kotlinrenderengine.geometry.Shape
import com.example.engine.render.kotlin.kotlinrenderengine.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min

class BasicRenderer(private val shape: Shape) : GLSurfaceView.Renderer {

    companion object {
        private const val VERTEX_POSITION_DIMENSIONS = 3
        private const val NORMAL_DIMENSIONS = 3
        private const val BYTES_PER_FLOAT = 4
        private const val A_COLOR = "a_Color"
        private const val A_POSITION = "a_Position"
        private const val A_NORMAL = "a_Normal"
        private const val U_MVP = "u_MVP"
        private const val U_MODEL_MATRIX = "u_ModelMatrix"
        private const val U_LIGHT_DIRECTION = "u_LightDirection"
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE = (VERTEX_POSITION_DIMENSIONS + NORMAL_DIMENSIONS + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
    private var width: Int = 0
    private var height: Int = 0
    private val vertexData: FloatBuffer
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var aPositionLocation: Int = 0
    private var aColorLocation: Int = 0
    private var aNormalLocation: Int = 0
    private var uMvpLocation: Int = 0
    private var uModelMatrixLocation: Int = 0
    private var uLightDirectionLocation: Int = 0
    private var vertexCount: Int
    private var shaderProgram: Int = 0

    private var lastFrameTime: Long = 0
    private var rotationAngle: Float = 0f

    private val lightDirection = floatArrayOf(-0.5f, -1.0f, 1.0f)  // Light coming from above and slightly towards the viewer


    init {
        // Assuming shape.vertexPositions now includes normal data
        vertexData = ByteBuffer
            .allocateDirect(shape.vertexPositions.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(shape.vertexPositions) }
        vertexCount = shape.vertexPositions.size / (VERTEX_POSITION_DIMENSIONS + NORMAL_DIMENSIONS + COLOR_COMPONENT_COUNT)
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastFrameTime) / 1e9f
        lastFrameTime = currentTime

        rotationAngle += 90f * deltaTime // 90 degrees per second
        rotationAngle %= 360f

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -5f)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 1.0f, 0.3f, 0.0f)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(uMvpLocation, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrixLocation, 1, false, modelMatrix, 0)
        GLES20.glUniform3fv(uLightDirectionLocation, 1, lightDirection, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)

        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspect, 1f, 10f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        try {
            shaderProgram = createShaderProgram()
            setupShaderAttributes()
        } catch (e: ShaderCompilationException) {
            Log.e("BasicRenderer", "Shader compilation failed: ${e.message}")
        }

        lastFrameTime = System.nanoTime()
    }

    private fun createShaderProgram(): Int {
        val vertexShader = readTextFileFromRaw(R.raw.vertex_shader)
        val fragmentShader = readTextFileFromRaw(R.raw.fragment_shader)

        val compiledVertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val compiledFragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        val program = linkProgram(compiledVertexShader, compiledFragmentShader)
            ?: throw ShaderCompilationException("Shader program linking failed")

        if (BuildConfig.DEBUG) {
            validateProgram(program)
        }

        return program
    }

    private fun setupShaderAttributes() {
        GLES20.glUseProgram(shaderProgram)

        aPositionLocation = GLES20.glGetAttribLocation(shaderProgram, A_POSITION)
        aColorLocation = GLES20.glGetAttribLocation(shaderProgram, A_COLOR)
        aNormalLocation = GLES20.glGetAttribLocation(shaderProgram, A_NORMAL)
        uMvpLocation = GLES20.glGetUniformLocation(shaderProgram, U_MVP)
        uModelMatrixLocation = GLES20.glGetUniformLocation(shaderProgram, U_MODEL_MATRIX)
        uLightDirectionLocation = GLES20.glGetUniformLocation(shaderProgram, U_LIGHT_DIRECTION)

        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            aPositionLocation,
            VERTEX_POSITION_DIMENSIONS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(VERTEX_POSITION_DIMENSIONS)
        GLES20.glVertexAttribPointer(
            aNormalLocation,
            NORMAL_DIMENSIONS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aNormalLocation)

        vertexData.position(VERTEX_POSITION_DIMENSIONS + NORMAL_DIMENSIONS)
        GLES20.glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aColorLocation)
    }


    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderId = GLES20.glCreateShader(type)
        if (shaderId == 0) {
            throw ShaderCompilationException("Failed to create shader")
        }

        GLES20.glShaderSource(shaderId, shaderCode)
        GLES20.glCompileShader(shaderId)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            val logLength = IntArray(1)
            GLES20.glGetShaderiv(shaderId, GLES20.GL_INFO_LOG_LENGTH, logLength, 0)
            val log = if (logLength[0] > 0) GLES20.glGetShaderInfoLog(shaderId) else "Unknown error"
            GLES20.glDeleteShader(shaderId)
            throw ShaderCompilationException("Shader compilation failed: $log")
        }

        return shaderId
    }

    class ShaderCompilationException(message: String) : Exception(message)
}