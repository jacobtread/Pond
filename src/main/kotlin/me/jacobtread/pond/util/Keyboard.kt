package me.jacobtread.pond.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.io.Reader

object Keyboard {
    private val dataMap: HashMap<String, KeyReference> = HashMap()
    var loaded: Boolean = false

    fun _load() {
        if (!loaded) {
            javaClass.getResourceAsStream("/layout/us.json")?.run {
                val bin = BufferedInputStream(this)
                val reader = InputStreamReader(bin)
                reader.use {load(it) }
            }
        }
    }

    fun load(reader: Reader) {
        val gson = Gson()
        val root: JsonObject = gson.fromJson(reader, JsonObject::class.java)
        for (key in root.keySet()) {
            if (key.trim() == "__comment") continue
            val value: String = root.getAsJsonPrimitive(key).asString
            val parts: List<String> = value.split(",", limit = 3)
            try {
                val mod: Int = parts[0].toInt(16)
                val keyCode: Int = parts[2].toInt(16)
                dataMap[key] = KeyReference(mod, keyCode)
            } catch (e: NumberFormatException) {
                println("Unable to parse key $key value: $e")
            }
        }
        loaded = true
    }

    fun keys(): Set<String> {
        return dataMap.keys
    }

    fun get(key: Char): KeyReference? {
        return get(key.toString())
    }
    fun get(key: String): KeyReference? {
        return if (key in dataMap) {
            dataMap[key]
        } else null
    }

    fun has(key: String): Boolean {
        return key in dataMap
    }
}

data class KeyReference(val mod: Int, val keyCode: Int)