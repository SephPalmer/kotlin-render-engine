package com.example.engine.render.kotlin.kotlinrenderengine.geometry

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class Torus(
    private val majorRadius: Float = 0.7f,
    private val minorRadius: Float = 0.3f,
    private val majorSegments: Int = 48,
    private val minorSegments: Int = 24
) : Shape {

    override val vertexPositions: FloatArray by lazy { generateTorusVertices() }

    private fun generateTorusVertices(): FloatArray {
        val vertices = mutableListOf<Float>()

        for (i in 0 until majorSegments) {
            val u = i.toFloat() / majorSegments
            val nextU = (i + 1).toFloat() / majorSegments

            for (j in 0 until minorSegments) {
                val v = j.toFloat() / minorSegments
                val nextV = (j + 1).toFloat() / minorSegments

                // Generate vertices for two triangles
                val v1 = generateVertexData(u, v)
                val v2 = generateVertexData(nextU, v)
                val v3 = generateVertexData(u, nextV)
                val v4 = generateVertexData(nextU, nextV)

                // First triangle
                vertices.addAll(v1)
                vertices.addAll(v2)
                vertices.addAll(v3)

                // Second triangle
                vertices.addAll(v2)
                vertices.addAll(v4)
                vertices.addAll(v3)
            }
        }

        return vertices.toFloatArray()
    }

    private fun generateVertexData(u: Float, v: Float): List<Float> {
        val majorAngle = u * 2 * PI.toFloat()
        val minorAngle = v * 2 * PI.toFloat()

        val cosMinor = cos(minorAngle)
        val sinMinor = sin(minorAngle)
        val cosMajor = cos(majorAngle)
        val sinMajor = sin(majorAngle)

        val x = (majorRadius + minorRadius * cosMinor) * cosMajor
        val y = (majorRadius + minorRadius * cosMinor) * sinMajor
        val z = minorRadius * sinMinor

        // Calculate normal
        val nx = cosMinor * cosMajor
        val ny = cosMinor * sinMajor
        val nz = sinMinor

        // Generate a color based on position
        val r = 0.5f + 0.5f * cosMajor
        val g = 0.5f + 0.5f * sinMajor
        val b = 0.5f + 0.5f * sinMinor

        return listOf(
            x, y, z,    // Position
            nx, ny, nz, // Normal
            r, g, b     // Color
        )
    }
}