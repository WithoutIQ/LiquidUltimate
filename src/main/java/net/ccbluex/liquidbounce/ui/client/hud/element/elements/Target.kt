/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils.getHealth
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils.deltaTime
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawBorderedRect
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect
import net.ccbluex.liquidbounce.utils.render.RenderUtils.drawScaledCustomSizeModalRect
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.glColor4f
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val fadeSpeed by FloatValue("FadeSpeed", 2F, 1F..9F)
    private val absorption by BoolValue("Absorption", true)
    private val healthFromScoreboard by BoolValue("HealthFromScoreboard", true)

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private var easingHealth = 0F
    private var lastTarget: Entity? = null

    override fun drawElement(): Border {
        var target: EntityLivingBase? = KillAura.target
        if (!ModuleManager[KillAura.javaClass].state && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit is EntityPlayer)
            target = mc.objectMouseOver.entityHit as EntityPlayer?

        if (target is EntityPlayer) {
            val targetHealth = getHealth(target, healthFromScoreboard, absorption)

            if (target != lastTarget || easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - targetHealth) < 0.01
            ) {
                easingHealth = targetHealth
            }

            val width = (38f + (target.name?.let(Fonts.font40::getStringWidth) ?: 0)).coerceAtLeast(118f)

            // Draw rect box
            drawBorderedRect(0F, 0F, width, 38F, 3F, Color(25, 25, 25, 100).rgb, Color(25, 25, 25, 100).rgb)

            // Damage animation
//            if (easingHealth > targetHealth.coerceAtMost(target.maxHealth))
//                drawRect(
//                    0F,
//                    34F,
//                    (easingHealth / target.maxHealth).coerceAtMost(1f) * width,
//                    38F,
//                    Color(252, 185, 65).rgb
//                )
//
            // Health bar
            var hColor = Color(252, 96, 66)
            if (targetHealth > target.maxHealth / 2){
                hColor = Color(102, 255, 0)
            }else if(targetHealth > target.maxHealth / 4){
                hColor = Color(255, 255, 0)
            }

            drawRect(0F, 34F, (targetHealth / target.maxHealth).coerceAtMost(1f) * width, 38F, hColor.rgb)

            // Heal animation
            if (easingHealth < targetHealth)
                drawRect(
                    (easingHealth / target.maxHealth).coerceAtMost(1f) * width, 34F,
                    (targetHealth / target.maxHealth).coerceAtMost(1f) * width, 38F, Color(44, 201, 144).rgb
                )

            easingHealth += ((targetHealth - easingHealth) / 2f.pow(10f - fadeSpeed)) * deltaTime

            target.name?.let { Fonts.font40.drawString(it, 36, 3, 0xffffff) }
            Fonts.font35.drawString(
                "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}",
                36,
                15,
                0xffffff
            )

            // Draw info
            val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            if (playerInfo != null) {
                Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}", 36, 24, 0xffffff)

                // Draw head
                val locationSkin = playerInfo.locationSkin
                drawHead(locationSkin, 30, 30)
            }
        }

        lastTarget = target
        return Border(0F, 0F, 120F, 36F)
    }

    private fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
        glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height, 64F, 64F)
    }

}