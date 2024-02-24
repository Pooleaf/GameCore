package net.pooleaf.gamecore.utils

import org.bukkit.inventory.ItemStack

fun ItemStack.removeEnchantmentAll(): ItemStack {
    this.enchantments.keys.forEach { this.removeEnchantment(it) }
    return this
}