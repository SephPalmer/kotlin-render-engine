package com.example.engine.render.kotlin.kotlinrenderengine.engine

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.os.SystemClock
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

class BasicRenderer(private val context: Context, private val shape: Shape) :
    GLSurfaceView.Renderer {

    companion object {
        private const val VERTEX_POSITION_DIMENSIONS = 3
        private const val NORMAL_DIMENSIONS = 3
        private const val TEXTURE_COORDINATE_DIMENSIONS = 2
        private const val BYTES_PER_FLOAT = 4
        private const val A_POSITION = "a_Position"
        private const val A_NORMAL = "a_Normal"
        private const val A_TEX_COORD = "a_TexCoord"
        private const val U_MVP = "u_MVP"
        private const val U_MODEL_MATRIX = "u_ModelMatrix"
        private const val U_LIGHT_DIRECTION = "u_LightDirection"
        private const val U_TEXTURE = "u_Texture"
        private const val STRIDE =
            (VERTEX_POSITION_DIMENSIONS + NORMAL_DIMENSIONS + TEXTURE_COORDINATE_DIMENSIONS) * BYTES_PER_FLOAT
    }

    private var width: Int = 0
    private var height: Int = 0
    private val vertexData: FloatBuffer
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var aPositionLocation: Int = 0
    private var aNormalLocation: Int = 0
    private var aTexCoordLocation: Int = 0
    private var uMvpLocation: Int = 0
    private var uModelMatrixLocation: Int = 0
    private var uLightDirectionLocation: Int = 0
    private var uTextureLocation: Int = 0
    private var vertexCount: Int
    private var shaderProgram: Int = 0

    private var textureHandle: Int = 0

    private val lightDirection = floatArrayOf(0.5f, -1.0f, -0.5f)

    init {
        vertexData = ByteBuffer
            .allocateDirect(shape.vertexPositions.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(shape.vertexPositions) }
        vertexCount =
            shape.vertexPositions.size / (VERTEX_POSITION_DIMENSIONS + NORMAL_DIMENSIONS + TEXTURE_COORDINATE_DIMENSIONS)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        try {
            shaderProgram = createShaderProgram()
            setupShaderAttributes()
        } catch (e: ShaderCompilationException) {
            Log.e("BasicRenderer", "Shader compilation failed: ${e.message}")
        }

        textureHandle = loadTexture(context, R.drawable.torus_texture) ?: 0
        if (textureHandle == 0) {
            Log.e("BasicRenderer", "Failed to load texture")
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)

        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspect, 1f, 10f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val time = SystemClock.uptimeMillis() % 10000L
        val angleInDegrees = (360.0f / 10000.0f) * time.toInt()

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3.0f)
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0f, 1f, 0.5f)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(uMvpLocation, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrixLocation, 1, false, modelMatrix, 0)
        GLES20.glUniform3fv(uLightDirectionLocation, 1, lightDirection, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        GLES20.glUniform1i(uTextureLocation, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
    }

    private fun createShaderProgram(): Int {
        val vertexShader = readTextFileFromRaw(R.raw.vertex_shader)
        val fragmentShader = readTextFileFromRaw(R.raw.fragment_shader)

        val compiledVertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val compiledFragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        val program =
            (if (compiledFragmentShader != null && compiledVertexShader != null) linkProgram(
                compiledVertexShader,
                compiledFragmentShader
            ) else null)
                ?: throw ShaderCompilationException("Shader program linking failed")

        if (BuildConfig.DEBUG) {
            validateProgram(program)
        }

        return program
    }

    private fun setupShaderAttributes() {
        GLES20.glUseProgram(shaderProgram)

        aPositionLocation = GLES20.glGetAttribLocation(shaderProgram, A_POSITION)
        aNormalLocation = GLES20.glGetAttribLocation(shaderProgram, A_NORMAL)
        aTexCoordLocation = GLES20.glGetAttribLocation(shaderProgram, A_TEX_COORD)
        uMvpLocation = GLES20.glGetUniformLocation(shaderProgram, U_MVP)
        uModelMatrixLocation = GLES20.glGetUniformLocation(shaderProgram, U_MODEL_MATRIX)
        uLightDirectionLocation = GLES20.glGetUniformLocation(shaderProgram, U_LIGHT_DIRECTION)
        uTextureLocation = GLES20.glGetUniformLocation(shaderProgram, U_TEXTURE)

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
            aTexCoordLocation,
            TEXTURE_COORDINATE_DIMENSIONS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        GLES20.glEnableVertexAttribArray(aTexCoordLocation)
    }

    class ShaderCompilationException(message: String) : Exception(message)
}