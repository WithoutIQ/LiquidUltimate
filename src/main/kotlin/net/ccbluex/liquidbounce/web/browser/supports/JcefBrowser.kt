/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2023 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package net.ccbluex.liquidbounce.web.browser.supports

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.mcef.MCEF
import net.ccbluex.liquidbounce.mcef.MCEFDownloader
import net.ccbluex.liquidbounce.utils.io.HttpClient
import net.ccbluex.liquidbounce.web.browser.BrowserType
import net.ccbluex.liquidbounce.web.browser.supports.tab.JcefTab

/**
 * Uses a modified fork of the JCEF library browser backend made for Minecraft.
 * This browser backend is based on Chromium and is the most advanced browser backend.
 * JCEF is available through the MCEF library, which provides a Minecraft compatible version of JCEF.
 *
 * @see <a href="https://github.com/CCBlueX/java-cef/">JCEF</a>
 * @see <a href="https://github.com/CCBlueX/mcef/">MCEF</a>
 *
 * @author 1zuna <marco@ccbluex.net>
 */
class JcefBrowser : IBrowser, Listenable {

    private val tabs = mutableListOf<JcefTab>()

    override fun makeDependenciesAvailable() {
        if (!MCEF.isInitialized()) {
            MCEF.getSettings().apply {
                downloadMirror = "https://dl.ccbluex.net/resources"
                // Uses a natural user agent to prevent websites from blocking the browser
                userAgent = HttpClient.DEFAULT_AGENT
            }
            // todo: add progression bar for downloading
            MCEFDownloader.downloadJcef()
        }
    }

    override fun initBrowserBackend() {
        if (!MCEF.isInitialized()) {
            MCEF.initialize()
        }
    }

    override fun shutdownBrowserBackend() {
        MCEF.shutdown()
    }

    override fun createTab(url: String) = JcefTab(this, url) { false }.apply {
        synchronized(tabs) {
            tabs.add(this)
        }
    }

    override fun createInputAwareTab(url: String, takesInput: () -> Boolean) = JcefTab(this, url, takesInput).apply {
        synchronized(tabs) {
            tabs.add(this)
        }
    }

    override fun getTabs() = tabs

    internal fun removeTab(tab: JcefTab) {
        synchronized(tabs) {
            tabs.remove(tab)
        }
    }

    override fun getBrowserType() = BrowserType.JCEF

}