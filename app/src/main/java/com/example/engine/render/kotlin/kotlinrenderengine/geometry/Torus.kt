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
            val majorAngle = u * 2 * PI.toFloat()

            for (j in 0 until minorSegments) {
                val v = j.toFloat() / minorSegments
                val minorAngle = v * 2 * PI.toFloat()

                // Calculate position
                val x = (majorRadius + minorRadius * cos(minorAngle)) * cos(majorAngle)
                val y = (majorRadius + minorRadius * cos(minorAngle)) * sin(majorAngle)
                val z = minorRadius * sin(minorAngle)

                // Calculate normal
                val nx = cos(minorAngle) * cos(majorAngle)
                val ny = cos(minorAngle) * sin(majorAngle)
                val nz = sin(minorAngle)

                // Add vertex data (position, normal, color)
                vertices.addAll(listOf(
                    x, y, z,                   // Position
                    nx, ny, nz,                // Normal
                    0.7f, 0.3f + u, 0.3f + v   // Color (varying for visibility)
                ))

                // Calculate indices for triangles
                val nextI = (i + 1) % majorSegments
                val nextJ = (j + 1) % minorSegments
                val verticesPerQuad = 6 // Two triangles per quad

                // First triangle
                vertices.addAll(listOf(
                    x, y, z,
                    nx, ny, nz,
                    0.7f, 0.3f + u, 0.3f + v
                ))
                vertices.addAll(generateVertexData(nextI, j))
                vertices.addAll(generateVertexData(i, nextJ))

                // Second triangle
                vertices.addAll(generateVertexData(nextI, j))
                vertices.addAll(generateVertexData(nextI, nextJ))
                vertices.addAll(generateVertexData(i, nextJ))
            }
        }

        return vertices.toFloatArray()
    }

    private fun generateVertexData(i: Int, j: Int): List<Float> {
        val u = i.toFloat() / majorSegments
        val v = j.toFloat() / minorSegments
        val majorAngle = u * 2 * PI.toFloat()
        val minorAngle = v * 2 * PI.toFloat()

        val x = (majorRadius + minorRadius * cos(minorAngle)) * cos(majorAngle)
        val y = (majorRadius + minorRadius * cos(minorAngle)) * sin(majorAngle)
        val z = minorRadius * sin(minorAngle)

        val nx = cos(minorAngle) * cos(majorAngle)
        val ny = cos(minorAngle) * sin(majorAngle)
        val nz = sin(minorAngle)

        return listOf(
            x, y, z,                   // Position
            nx, ny, nz,                // Normal
            0.7f, 0.3f + u, 0.3f + v   // Color
        )
    }
}