package me.justahuman.spiritsunchained.managers;

import me.justahuman.spiritsunchained.SpiritsUnchained;
import me.justahuman.spiritsunchained.listeners.IdentifyingGlassListener;
import me.justahuman.spiritsunchained.listeners.PlayerReleaseSpiritListener;
import me.justahuman.spiritsunchained.listeners.PlayerClickListener;
import me.justahuman.spiritsunchained.listeners.SpiritItemListeners;
import me.justahuman.spiritsunchained.listeners.TraitListeners;

import org.bukkit.plugin.PluginManager;

public class ListenerManager {
    public ListenerManager() {
        final PluginManager manager = SpiritsUnchained.getPluginManager();
        final SpiritsUnchained instance = SpiritsUnchained.getInstance();

        manager.registerEvents(new IdentifyingGlassListener(), instance);
        manager.registerEvents(new PlayerClickListener(), instance);
        manager.registerEvents(new PlayerReleaseSpiritListener(), instance);
        manager.registerEvents(new SpiritItemListeners(), instance);
        manager.registerEvents(new TraitListeners(), instance);
        manager.registerEvents(new PlayerReleaseSpiritListener(), instance);
    }
}
