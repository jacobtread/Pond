package me.jacobtread.pond.util

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object Icons {

    private val ICON_MAP: HashMap<String, BufferedImage> = HashMap()

    operator fun get(name: String): BufferedImage {
        return if (name in ICON_MAP) {
            ICON_MAP[name]!!
        } else {
            val image: BufferedImage = ImageIO.read(javaClass.getResourceAsStream("/icons/$name.png"))
            ICON_MAP[name] = image
            image
        }
    }

}

fun BufferedImage.icon(): ImageIcon {
    return ImageIcon(this)
}