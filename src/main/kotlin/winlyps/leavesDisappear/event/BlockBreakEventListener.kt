package winlyps.leavesDisappear.event

import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Leaves
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.LeavesDecayEvent

class BlockBreakEventListener : Listener {

    private val neighbours = BlockFace.values().toMutableList().apply {
        remove(BlockFace.SELF)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block

        if (Tag.LEAVES.isTagged(block.type)) {
            breakLeaf(block, isValidLeaf(block), block, 0)
        }

        if (Tag.LOGS.isTagged(block.type)) {
            // When a log is broken, start limited range leaf decay
            breakLeaf(block, false, block, 0)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onLeavesDecay(event: LeavesDecayEvent) {
        breakLeaf(event.block, isValidLeaf(event.block), event.block, 0)
    }

    private fun breakLeaf(block: Block, breakFirstBlock: Boolean, originBlock: Block, distance: Int) {
        // Limit the range to prevent excessive spread
        if (distance > 10) return

        if (breakFirstBlock) {
            block.breakNaturally()
        }

        neighbours.forEach { neighbour ->
            val neighbourBlock = block.getRelative(neighbour)
            if (!isValidLeaf(neighbourBlock, originBlock)) return@forEach

            // Calculate distance from origin
            val newDistance = distance + 1
            breakLeaf(neighbourBlock, true, originBlock, newDistance)
        }
    }

    private fun isValidLeaf(block: Block, originBlock: Block? = null): Boolean {
        val leafBlock = block.blockData as? Leaves ?: return false

        return when {
            leafBlock.isPersistent -> false
            originBlock != null -> {
                // Check if within reasonable range from the original broken block
                val dx = kotlin.math.abs(block.x - originBlock.x)
                val dy = kotlin.math.abs(block.y - originBlock.y)
                val dz = kotlin.math.abs(block.z - originBlock.z)
                val maxDistance = kotlin.math.max(dx, kotlin.math.max(dy, dz))

                // Only break leaves within 10 blocks of the original log
                maxDistance <= 10
            }
            else -> leafBlock.distance >= 7
        }
    }
}