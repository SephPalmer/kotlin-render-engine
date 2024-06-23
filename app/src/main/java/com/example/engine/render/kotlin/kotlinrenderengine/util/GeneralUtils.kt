package com.example.engine.render.kotlin.kotlinrenderengine.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.opengl.GLUtils.texImage2D
import android.util.Log
import android.widget.Toast
import com.example.engine.render.kotlin.kotlinrenderengine.App
import com.example.engine.render.kotlin.kotlinrenderengine.BuildConfig
import java.io.InputStream

fun logD(msg: String) {
    if (BuildConfig.DEBUG)
        Log.d("Logging", msg)
}

fun msg(context: Context, message: String) {
    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}

fun readTextFileFromRaw(resourceId: Int): String {
    val stream: InputStream = App.get().resources.openRawResource(resourceId)
    return stream.bufferedReader().readText()
}

fun compileVertexShader(shaderTextView: String): Int? {
    return compileShader(GLES20.GL_VERTEX_SHADER, shaderTextView)
}

fun compileFragmentShader(shaderTextView: String): Int? {
    return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderTextView)
}

fun compileShader(shader: Int, shaderText: String): Int? {
    logD("Starting shader compilation")
    // Create an empty shader object
    val shaderObjectId = GLES20.glCreateShader(shader)
    if (shaderObjectId == 0) {
        if (shader == GLES20.GL_VERTEX_SHADER) logD("Error creating the vertex shader object id")
        if (shader == GLES20.GL_FRAGMENT_SHADER) logD("Error creating the fragment shader object id")
        return null
    }
    // Add the shader source
    GLES20.glShaderSource(shaderObjectId, shaderText)
    // Compile the shader
    GLES20.glCompileShader(shaderObjectId)

    // Check the shader compilation status
    val compilationStatus = IntArray(1)
    GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compilationStatus, 0)
    logD("Shader compilation info - " + GLES20.glGetShaderInfoLog(shaderObjectId))

    // Delete the shader if compilation failed
    if (compilationStatus[0] == 0) {
        GLES20.glDeleteShader(shaderObjectId)
        return null
    }
    return shaderObjectId
}

fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int? {

    // Create a Open GL ES shader program
    // This essentially combines and links the vertex and fragment shaders
    val shaderProgramId = GLES20.glCreateProgram()
    if (shaderProgramId == 0) {
        logD("Error creating the shader program")
        return null
    }
    // Attach the shaders to the shader program
    GLES20.glAttachShader(shaderProgramId, vertexShaderId)
    GLES20.glAttachShader(shaderProgramId, fragmentShaderId)

    // Link the shaders
    GLES20.glLinkProgram(shaderProgramId)

    // Check whether linking worked
    val linkStatus = IntArray(1)
    GLES20.glGetProgramiv(shaderProgramId, GLES20.GL_LINK_STATUS, linkStatus, 0)
    logD("Shader program link status - " + GLES20.glGetProgramInfoLog(shaderProgramId))
    if (linkStatus[0] == 0) {
        GLES20.glDeleteProgram(shaderProgramId)
        logD("Program linking failed")
        return null
    }
    return shaderProgramId
}

fun validateProgram(shaderProgramId: Int): Boolean {
    // Validate the shader program
    GLES20.glValidateProgram(shaderProgramId)
    val shaderValidationStatus = IntArray(1)
    GLES20.glGetProgramiv(shaderProgramId, GLES20.GL_VALIDATE_STATUS, shaderValidationStatus, 0)
    logD("Shader program validation results - " + shaderValidationStatus[0])
    return shaderValidationStatus[0] != 0
}


fun loadTexture(context: Context, resourceId: Int): Int {
    val textureObjectIds = IntArray(1)
    GLES20.glGenTextures(1, textureObjectIds, 0)

    if (textureObjectIds[0] == 0) {
        Log.e("loadTexture", "Could not generate a new OpenGL texture object.")
        return 0
    }

    val options = BitmapFactory.Options()
    options.inScaled = false

    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

    if (bitmap == null) {
        Log.e("loadTexture", "Resource ID $resourceId could not be decoded.")
        GLES20.glDeleteTextures(1, textureObjectIds, 0)
        return 0
    }

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

    bitmap.recycle()

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    return textureObjectIds[0]
}



