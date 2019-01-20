package com.example.engine.render.kotlin.kotlinrenderengine.engine

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import com.example.engine.render.kotlin.kotlinrenderengine.geometry.Cube
import com.example.engine.render.kotlin.kotlinrenderengine.BuildConfig
import com.example.engine.render.kotlin.kotlinrenderengine.R
import com.example.engine.render.kotlin.kotlinrenderengine.geometry.Shape
import com.example.engine.render.kotlin.kotlinrenderengine.util.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// This renderer is three dimensional, so we define three positions (x,y,z) for each vertex
const val VERTEX_POSITION_DIMENSIONS = 3

// The renderer runs in the native environment, not the virtual machine.
// A float has 32 bits of precision and a byte has 8 bits, so a float has 4 bytes
const val BYTES_PER_FLOAT = 4
// Next we define the names of the uniforms and attributes we are passing to the shaders
const val A_COLOR = "a_Color"
const val A_POSITION = "a_Position"
const val U_MATRIX = "u_Matrix"
const val COLOR_COMPONENT_COUNT = 3
// The stride defines how many bytes is associated with each vertex. This is used to
// traverse the vertex array
const val STRIDE = (VERTEX_POSITION_DIMENSIONS + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT


class BasicRenderer
    : GLSurfaceView.Renderer {

    private var w: Int = 0
    private var h: Int = 0
    private val vertexData: FloatBuffer
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val shape: Shape
    private var aPositionLocation: Int = 0
    private var aColorLocation: Int = 0
    private var uMatrixLocation: Int = 0
    private var vertexCount: Int

    init {
        shape = Cube()
        // Allocate the amount of bytes we require in the native environment
        // and specify whether we they should be big endian or small endian.
        // We just use whichever the system uses
        // After allocating the appropriate amount of memory, add the vertex positions
        vertexData = ByteBuffer
            .allocateDirect(shape.vertexPositions.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(shape.vertexPositions)
        vertexCount = shape.vertexPositions.size / (VERTEX_POSITION_DIMENSIONS + COLOR_COMPONENT_COUNT)
    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val uptime = SystemClock.uptimeMillis() % 28000L
        val angle = 0.15f * uptime

        //Create a perspective projection
        Matrix.perspectiveM(
            projectionMatrix,
            0,
            45f,
            w.toFloat() / h.toFloat(),
            1f,
            10f
        )
        // To move the model into view we translate it
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -5f)
        // Rotate the model
        Matrix.rotateM(modelMatrix, 0, angle, 1.0f, 0.3f, 0.0f)

        // Rather than adding an extra matrix to the shader for the model matrix
        // We multiply both matrices together and use the result of that
        val temp = FloatArray(16)

        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)


        // Send the matrix we use for projection to the shader
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)
        // we specify the number of vertices by finding the number of positions and dividing by the
        // number of dimensions. So a 2D shape with 3 vertices has 6 positions,
        // while a 3D has 9.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
    }



    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        // Fill the surface with the viewport
        w = width
        h = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
        val vertexShader = readTextFileFromRaw(R.raw.vertex_shader)
        val fragmentShader = readTextFileFromRaw(R.raw.fragment_shader)
        val compiledVertexShader = compileVertexShader(vertexShader)
        val compiledFragmentShader = compileFragmentShader(fragmentShader)
        val shaderProgram: Int?
        if (compiledVertexShader != null && compiledFragmentShader != null) {
            shaderProgram = linkProgram(compiledVertexShader, compiledFragmentShader)
        } else {
            TODO("Couldn't create the shaders")
        }
        if (BuildConfig.DEBUG && shaderProgram != null) {
            validateProgram(shaderProgram)
        } else {
            TODO("Couldn't validate the shader program")
        }
        shaderProgram.let {
            GLES20.glUseProgram(it)
            aColorLocation = GLES20.glGetAttribLocation(shaderProgram, A_COLOR)
            aPositionLocation = GLES20.glGetAttribLocation(it, A_POSITION)
            uMatrixLocation = GLES20.glGetUniformLocation(it, U_MATRIX)
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
            // Associate the color data with the shaders
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
    }
}