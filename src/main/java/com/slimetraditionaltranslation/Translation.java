package com.slimetraditionaltranslation;

import de.unpixelt.locale.Locale;
import de.unpixelt.locale.Translate;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.justahuman.spiritsunchained.utils.SpiritUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Translation {
    public static String MobStatsInfo(String State) {
        return switch (State) {
            case "Hostile" -> SpiritUtils.getTranslation("translation_extra.mob_stats.hostile");
            case "Aggressive" -> SpiritUtils.getTranslation("translation_extra.mob_stats.aggressive");
            case "Passive" -> SpiritUtils.getTranslation("translation_extra.mob_stats.passive");
            case "Gentle" -> SpiritUtils.getTranslation("translation_extra.mob_stats.gentle");
            case "Friendly" -> SpiritUtils.getTranslation("translation_extra.mob_stats.friendly");
            default -> "ERROR STATE - " + State;
        };
    }

    public static String ItemType(String Type) {
        return switch (Type) {
            case "Use Item" -> SpiritUtils.getTranslation("translation_extra.item_type.use_item");
            case "Passive" -> SpiritUtils.getTranslation("translation_extra.item_type.passive");
            default -> "ERROR TYPE - " + Type;
        };
    }

    public static String GoalLoreEnd(String Goal) {
        SlimefunItem sfItem = SlimefunItem.getById(Goal);
        Material mcMaterial = Material.matchMaterial(Goal);
        EntityType mcEntityType = null;
        try {
            mcEntityType = EntityType.valueOf(Goal.toUpperCase());
        } catch (IllegalArgumentException e) {
            //
        }

        if (sfItem != null) {
            return sfItem.getItemName();
        } else if (mcMaterial != null) {
            return Translate.getMaterial(Locale.zh_tw, mcMaterial);
        } else if (mcEntityType != null) {
            return Translate.getEntity(Locale.zh_tw, mcEntityType);
        } else {
            return "ERROR - UNKNOW " + Goal;
        }
    }
}
