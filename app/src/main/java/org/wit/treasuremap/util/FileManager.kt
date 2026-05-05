package org.wit.treasuremap.util

import android.content.Context
import java.io.*

// fully AI generated due to time constraints
// Check if the file exists
fun exists(context: Context, name: String): Boolean =
    context.getFileStreamPath(name).exists()

// Save the text to a file
fun write(context: Context, name: String, data: String) {
    val writer = OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE))
    writer.write(data)
    writer.close()
}

// Read the text from a file
fun read(context: Context, name: String): String {
    val reader = context.openFileInput(name).bufferedReader()
    val content = reader.use { it.readText() }
    return content
}