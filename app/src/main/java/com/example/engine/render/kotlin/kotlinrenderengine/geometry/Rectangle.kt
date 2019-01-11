package com.example.engine.render.kotlin.kotlinrenderengine.geometry

class Rectangle() : Shape {

    override val vertexPositions: FloatArray
    get() = floatArrayOf(
        // Triangle 1
        -0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 1.0f,
        0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f,
        -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.5f,

        // Triangle 2
        -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.5f,
        0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f,

        // Triangle 3
        -0.5f, -0.5f, 1.0f, 0.5f, 0.25f, 0.25f,
        -0.5f, -0.5f, 0.0f, 1.0f, 0.5f, 1.0f,
        0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f,

        // Triangle 4
        0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 0.0f,
        0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
        -0.5f, -0.5f, 0.0f, 1.0f, 0.5f, 1.0f
    )
}