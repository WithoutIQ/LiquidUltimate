package net.ccbluex.liquidbounce.utils.render

class CommonRenderUtil {
    companion object {
        fun isHovered(x: Float, y: Float, width: Float, height: Float, mouseX: Int, mouseY: Int): Boolean {
            return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height
        }
    }
}