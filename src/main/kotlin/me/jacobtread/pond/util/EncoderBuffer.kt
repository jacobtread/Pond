package me.jacobtread.pond.util

import java.util.*

class EncoderBuffer {

    private val bytes: LinkedList<Byte> = LinkedList()
    var writes: Int = 0

    private fun add(byte: Int) {
        writes++
        bytes.add(byte.toByte())
    }

    operator fun plusAssign(byte: Int) {
        this.add(byte)
    }

    fun startCount() {
        writes = 0
    }

    fun bytes(): ByteArray {
        return bytes.toByteArray()
    }

}