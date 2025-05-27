
package winlyps.leavesDisappear

import org.bukkit.plugin.java.JavaPlugin
import winlyps.leavesDisappear.event.BlockBreakEventListener

class LeavesDisappear : JavaPlugin() {

    override fun onEnable() {
        // Register event listener
        server.pluginManager.registerEvents(BlockBreakEventListener(), this)
        logger.info("LeavesDisappear plugin has been enabled!")
    }

    override fun onDisable() {
        logger.info("LeavesDisappear plugin has been disabled!")
    }
}
