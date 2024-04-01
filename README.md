# Kotlin Render Engine

This project is an OpenGL ES 2.0 rendering engine implemented in Kotlin for Android. It demonstrates the basics of creating a 3D renderer that can display simple shapes, such as cubes, with dynamic rotation.

## Features

- OpenGL ES 2.0 rendering.
- Dynamic object rotation based on system uptime.
- Perspective projection and model transformation.
- Basic shader compilation and linking.
- Surface handling on different screen sizes.

## Requirements

- Android SDK with a minimum API level of 16 (Jelly Bean).
- Kotlin plugin for Android Studio.
- An Android device or emulator with support for OpenGL ES 2.0.

## Setup and Running

1. **Clone the Repository:**
Here...


2. **Open the Project:**
- Open Android Studio.
- Select "Open an existing Android Studio project".
- Navigate to the cloned repository and open it.

3. **Run the Application:**
- Connect your Android device or use the Android emulator.
- Run the application through Android Studio.

## Implementation Details

- **OpenGL Initialization:**
- Setup occurs in `onSurfaceCreated`, where shaders are compiled, and the program is linked.

- **Rendering Loop:**
- `onDrawFrame` is called to render each frame. It handles dynamic object rotation and applies projection and model transformations.

- **Shader Management:**
- Vertex and fragment shaders are loaded from raw resources, compiled, and linked to the OpenGL program.

- **Geometry Handling:**
- A `Cube` class is provided as an example shape, but the renderer is designed to be extendable for additional geometries.

## Customization

- **Adding New Shapes:**
- Implement the `Shape` interface and define vertex positions and color data.

- **Shader Modifications:**
- Modify or replace the vertex and fragment shaders in `res/raw` to achieve different visual effects.

## Contribution

Contributions are welcome. Please follow the standard fork and pull request workflow.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Acknowledgments

- This project is a starting point for those interested in learning OpenGL ES rendering on Android.

