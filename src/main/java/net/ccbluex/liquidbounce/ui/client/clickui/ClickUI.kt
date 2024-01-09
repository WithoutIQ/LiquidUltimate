package net.ccbluex.liquidbounce.ui.client.clickui

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.animation.Animation
import net.ccbluex.liquidbounce.utils.animation.Type
import net.ccbluex.liquidbounce.utils.render.CommonRenderUtil.Companion.isHovered
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.min

class ClickUI : GuiScreen() {
    companion object {

    }

    var x: Float = -1f;
    var y: Float = -1f;
    private var sWidth: Float = 0f;
    private var sHeight: Float = 0f;


    override fun initGui() {
        super.initGui()
        val sr = ScaledResolution(mc)
        // set width, height
        if (sWidth == 0f)
            sWidth = min(600f, sr.scaledWidth - 40f)
        if (sHeight == 0f)
            sHeight = min(350f, sr.scaledHeight - 40f)


        // set x,y to center of screen if x,y is unset;
        if (x == -1f && y == -1f) {
            x = (sr.scaledWidth - width) / 2f
            y = (sr.scaledHeight - height) / 2f
        }
        alphaAnimation.reset()
        alphaAnimation.start(50.0, 255.0, 0.3f, Type.EASE_IN_OUT_QUAD)
    }

    private var currentType = ModuleCategory.COMBAT;
    private val gap = 20
    private val iconSize = 16

    private val alphaAnimation = Animation()


    private var scrollY = 0f


    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)
        alphaAnimation.update()
        val alpha = alphaAnimation.getValue().toInt()
        RenderUtils.drawBorderedRect(
            x,
            y,
            x + sWidth,
            y + sHeight,
            3f,
            Color(33, 33, 33, alpha).rgb,
            Color(33, 33, 33, alpha).rgb
        )
        RenderUtils.drawBorderedRect(
            x,
            y + titleHeight,
            x + sWidth,
            y + titleHeight + 1,
            3f,
            Color(52, 52, 52, alpha).rgb,
            Color(52, 52, 52, alpha).rgb
        )

        RenderUtils.drawImage(ResourceLocation("liquidbounce/ui/logo.png"), x.toInt() + 16, y.toInt() + 14, 45, 13)


        val categoryWidth = ModuleCategory.values().size * iconSize + (ModuleCategory.values().size - 1) * gap
        var categoryStartX = x + sWidth / 2 - categoryWidth / 2
        for (category: ModuleCategory in ModuleCategory.values()) {
            val lowercase = category.name.lowercase()
            if (currentType == category) {
                RenderUtils.drawBorderedRect(
                    categoryStartX - 4,
                    y + 8,
                    categoryStartX + iconSize + 2,
                    y + 14 + iconSize,
                    3f,
                    Color(59, 59, 59, alpha).rgb,
                    Color(59, 59, 59, alpha).rgb
                )
                RenderUtils.drawImage(
                    ResourceLocation("liquidbounce/ui/categories/$lowercase.png"),
                    categoryStartX.toInt(),
                    y.toInt() + 10,
                    iconSize,
                    iconSize
                )
            } else {
                RenderUtils.drawImage(
                    ResourceLocation("liquidbounce/ui/categories/$lowercase.png"),
                    categoryStartX.toInt(),
                    y.toInt() + 10,
                    iconSize,
                    iconSize
                )
            }

            categoryStartX += gap + iconSize;
        }


        // show modules
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        RenderUtils.makeScissorBox(x, y + titleHeight + 12, x + sWidth, y + sHeight - 5)
        var modY = y + titleHeight + 12 + scrollY
        for (module: Module in LiquidBounce.moduleManager.modules) {
            if (module.category != currentType)
                continue
//            if (modY > y + sHeight)
//                continue
//            if (modY + 24 < y)
//                continue
            RenderUtils.drawBorderedRect(
                x.toInt() + 10,
                modY.toInt(),
                x.toInt() + sWidth.toInt() - 20,
                modY.toInt() + 24,
                1,
                Color(59, 59, 59, alpha).rgb,
                Color(38, 38, 38, alpha).rgb
            )
            Fonts.font35.drawString(module.name, x + 20, modY + 8, Color.WHITE.rgb)

            modY += 32
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST)

        // drag
        if (isDragging) {
            x = p_drawScreen_1_ - dragX
            y = p_drawScreen_2_ - dragY
            if (!Mouse.isButtonDown(0)) {
                isDragging = false
            }
        }

        // scroll
        var dWheel = Mouse.getDWheel()
        if (dWheel > 0) {
            scrollY += 3
        }else if(dWheel < 0){
            scrollY -= 3
        }
    }

    private var isDragging = false;
    private val titleHeight: Float = 36f;
    private var dragX = 0f
    private var dragY = 0f


    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
        if (isHovered(x, y, sWidth, titleHeight, p_mouseClicked_1_, p_mouseClicked_2_)) {
            if (p_mouseClicked_3_ == 0) {
                isDragging = true
                dragX = p_mouseClicked_1_ - x
                dragY = p_mouseClicked_2_ - y
            }
        }


        val categoryWidth = ModuleCategory.values().size * iconSize + (ModuleCategory.values().size - 1) * gap
        var categoryStartX = x + sWidth / 2 - categoryWidth / 2
        for (category: ModuleCategory in ModuleCategory.values()) {
            if (isHovered(
                    categoryStartX,
                    y + 8,
                    iconSize.toFloat(),
                    iconSize.toFloat(),
                    p_mouseClicked_1_,
                    p_mouseClicked_2_
                )
            ) {
                currentType = category
            }
            categoryStartX += gap + iconSize;
        }


    }

}
