package me.justahuman.spiritsunchained.implementation.mobs;

import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import me.justahuman.spiritsunchained.SpiritsUnchained;
import me.justahuman.spiritsunchained.slimefun.ItemStacks;
import me.justahuman.spiritsunchained.spirits.SpiritDefinition;
import me.justahuman.spiritsunchained.utils.MiscUtils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Allay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class UnIdentifiedSpirit extends AbstractCustomMob<Allay> {

    private int particleCount = 5;

    public UnIdentifiedSpirit() {
        super(Allay.class, "UNIDENTIFIED_SPIRIT", "&7Unidentified Spirit", 10);
    }

    @Nonnull
    @Override
    public Allay spawn(@Nonnull Location loc, @Nonnull World world, String reason, String type) {
        Allay mob = world.spawn(loc, this.getClazz());
        SpiritDefinition definition = SpiritsUnchained.getSpiritsManager().getSpiritMap().get(EntityType.valueOf(type));
        String state;

        if (reason.equals("Natural")) {
            state = definition.getStates().get(new Random().nextInt(definition.getStates().size()));
        } else {
            state = reason;
        }

        if (type == null) {
            type = "COW_SPIRIT";
        } else {
            type = type + "_SPIRIT";
        }

        PersistentDataAPI.setString(mob, MiscUtils.EntityKey, this.getId());
        PersistentDataAPI.setString(mob, MiscUtils.spiritStateKey, state);
        PersistentDataAPI.setString(mob, MiscUtils.spiritTypeKey, type);

        Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(this.getMaxHealth());
        mob.setHealth(this.getMaxHealth());
        mob.setCustomName(this.getName());
        mob.setCustomNameVisible(true);
        mob.setRemoveWhenFarAway(true);

        onSpawn(mob);
        return mob;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onSpawn(Allay allay) {
        allay.setCollidable(false);
        allay.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000*20, 1, true));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onTick(Allay allay) {
        MiscUtils.spawnParticleRadius(allay.getLocation(), Particle.SPELL_INSTANT, 0.1, particleCount, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onDeath(EntityDeathEvent event) {
        Allay allay = (Allay) event.getEntity();
        if (allay.getKiller() != null) {
            event.getDrops().add(ItemStacks.SU_ECTOPLASM);
            return;
        }
        event.setShouldPlayDeathSound(false);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onInteract(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onHit(EntityDamageByEntityEvent event) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void reveal(Allay allay, Player player) {
        allay.damage(allay.getHealth());
        SpiritsUnchained.getSpiritEntityManager().getCustomClass(null, PersistentDataAPI.getString(allay, MiscUtils.spiritTypeKey)).spawn(allay.getLocation(), allay.getWorld(), "Reveal", null);
    }
}
