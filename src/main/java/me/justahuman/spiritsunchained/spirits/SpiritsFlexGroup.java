package me.justahuman.spiritsunchained.spirits;

import de.unpixelt.locale.Locale;
import de.unpixelt.locale.Translate;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.bakedlibs.dough.items.CustomItemStack;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

import me.justahuman.spiritsunchained.SpiritsUnchained;
import me.justahuman.spiritsunchained.utils.PlayerUtils;
import me.justahuman.spiritsunchained.slimefun.Groups;
import me.justahuman.spiritsunchained.utils.SpiritUtils;

import net.kyori.adventure.text.Component;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * I definitely sampled from Sefiraat's Crystamae Historia to make this flex group
 * So please do check out his awesome stuff:
 * <a href="https://github.com/Sefiraat/CrystamaeHistoria/blob/master/src/main/java/io/github/sefiraat/crystamaehistoria/slimefun/itemgroups/MainFlexGroup.java">...</a>
 */
public class SpiritsFlexGroup extends FlexItemGroup {

    private static final int GUIDE_BACK = 1;
    private static final int PAGE_PREVIOUS = 46;
    private static final int PAGE_NEXT = 52;
    private static final int PAGE_SIZE = 36;

    private static final int[] HEADER = new int[]{
            0, 1, 2, 3, 4, 5, 6, 7, 8
    };
    private static final int[] FOOTER = new int[]{
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    private static final int[] DIVIDER = new int[]{
            13, 22, 31, 40, 49
    };

    private static final int[] AFRAID = new int[]{
            9, 10, 11, 12
    };

    private static final int[] AFRAID_ENTRIES = new int[]{
            18, 19, 20, 21,
            27, 28, 29, 30,
            36, 37, 38, 39,
            45, 46, 47, 48
    };

    private static final int[] SCARE = new int[]{
            14, 15, 16, 17
    };

    private static final int[] SCARE_ENTRIES = new int[]{
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44,
            50, 51, 52, 53
    };

    private static final int SPIRIT_SLOT = 22;
    private static final int GOAL_SLOT = 30;
    private static final int RELATIONS_SLOT = 32;
    private static final int TRAIT_SLOT = 40;

    private static final String backLore = "&7" + Slimefun.getLocalization().getMessage("guide.back.guide");

    private static String translate(String path) {
        return SpiritUtils.getTranslation("names.spirit_group." + path);
    }

    public static String name(String path) {
        return translate(path + ".name");
    }

    private static String[] lore(String path, String... replace) {
        final String[] originalLore = SpiritUtils.getTranslationList("names.spirit_group." + path + ".lore").toArray(String[]::new);
        final String[] lore = new String[originalLore.length];
        int l = 0;
        for (String line : originalLore) {
            int i = 0;
            for (String piece : replace) {
                if (i % 2 == 0) {
                    line = line.replace(piece, replace[i + 1]);
                }
                i++;
            }
            lore[l] = line;
            l++;
        }
        return lore;
    }

    private static final ItemStack notEnoughKnowledge = new CustomItemStack(
            Material.WRITABLE_BOOK,
            name("missing_knowledge"),
            lore("missing_knowledge")

    );

    private static final ItemStack afraidItemStack = new CustomItemStack(
            Material.BLACK_STAINED_GLASS_PANE,
            name("afraid_item"),
            lore("afraid_item")

    );

    private static final ItemStack scareItemStack = new CustomItemStack(
            Material.PURPLE_STAINED_GLASS_PANE,
            name("scare_item"),
            lore("scare_item")

    );

    @ParametersAreNonnullByDefault
    public SpiritsFlexGroup(NamespacedKey key, ItemStack item) {
        super(key, item);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player player, PlayerProfile playerProfile, SlimefunGuideMode guideMode) {
        return true;
    }

    public void playSound(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
    }
    
    @Override
    @ParametersAreNonnullByDefault
    public void open(Player player, PlayerProfile profile, SlimefunGuideMode mode) {
        final ChestMenu menu = new ChestMenu(SpiritUtils.getTranslation("names.groups.spirits"));

        for (int slot : HEADER) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }
        for (int slot : FOOTER) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), (player1, i1, itemStack, clickAction) -> false);
        }

        menu.setEmptySlotsClickable(false);
        prepare(player, profile, mode, menu, 1);
        menu.open(player);
    }

    @ParametersAreNonnullByDefault
    private void prepare(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu, int page) {
        final List<SpiritDefinition> spiritList = new ArrayList<>(SpiritsUnchained.getSpiritsManager().getSpiritMap().values());
        final int spiritCount = spiritList.size();
        final int totalPages = (int) Math.ceil(spiritCount / (double) PAGE_SIZE);
        final int start = (page - 1) * PAGE_SIZE;
        final int end = Math.min(start + PAGE_SIZE, spiritCount);

        spiritList.sort(Comparator.comparing(definition -> definition.getType().name()));
        spiritList.sort(Comparator.comparing(SpiritDefinition::getTier));
        final List<SpiritDefinition> spiritSubList = spiritList.subList(start, end);
    
        playSound(player);
        pageControls(player, profile, mode, menu, page, totalPages);

        menu.replaceExistingItem(GUIDE_BACK, ChestMenuUtils.getBackButton(player, backLore));
        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            SlimefunGuide.openItemGroup(profile, Groups.SU_MAIN_GROUP, mode, 1);
            return false;
        });

        for (int i = 0; i < 36; i++) {
            final int slot = i + 9;

            if (i + 1 <= spiritSubList.size()) {
                final SpiritDefinition definition = spiritSubList.get(i);
                menu.replaceExistingItem(slot, getSpiritMenuItem(definition));
                menu.addMenuClickHandler(slot, (player1, i1, itemStack1, clickAction) -> {
                    displayDefinition(player1, profile, mode, menu, page, definition);
                    return false;
                });
            } else {
                menu.replaceExistingItem(slot, null);
                menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    private void pageControls(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu, int page, int totalPages) {
        for (int slot : FOOTER) {
            menu.replaceExistingItem(slot, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(slot, ((player1, i, itemStack, clickAction) -> false));
        }

        menu.replaceExistingItem(PAGE_PREVIOUS, ChestMenuUtils.getPreviousButton(player, page, totalPages));
        menu.addMenuClickHandler(PAGE_PREVIOUS, (player1, slot, itemStack, clickAction) -> {
            final int previousPage = page - 1;
            if (previousPage >= 1) {
                prepare(player1, profile, mode, menu, previousPage);
            }
            return false;
        });

        menu.replaceExistingItem(PAGE_NEXT, ChestMenuUtils.getNextButton(player, page, totalPages));
        menu.addMenuClickHandler(PAGE_NEXT, (player1, slot, itemStack, clickAction) -> {
            final int nextPage = page + 1;
            if (nextPage <= totalPages) {
                prepare(player1, profile, mode, menu, nextPage);
            }
            return false;
        });
    }

    @ParametersAreNonnullByDefault
    private void displayDefinition(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu, int returnPage, SpiritDefinition definition) {
        playSound(player);
        final EntityType entityType = definition.getType();

        // Back Button
        menu.replaceExistingItem(GUIDE_BACK, ChestMenuUtils.getBackButton(player, backLore));
        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            prepare(player1, profile, mode, menu, returnPage);
            return false;
        });

        clearDisplay(menu);

        //Spirit Type
        menu.replaceExistingItem(SPIRIT_SLOT, getSpiritMenuItem(definition));

        //Pass On Task
        if (PlayerUtils.hasKnowledgePiece(player, entityType, 1) || mode == SlimefunGuideMode.CHEAT_MODE) {
            menu.replaceExistingItem(GOAL_SLOT, definition.getGoal().getDisplayStack());
        } else {
            menu.replaceExistingItem(GOAL_SLOT, notEnoughKnowledge);
        }

        //Relations
        if (PlayerUtils.hasKnowledgePiece(player, entityType, 2) || mode == SlimefunGuideMode.CHEAT_MODE) {
            menu.replaceExistingItem(RELATIONS_SLOT, new CustomItemStack(
                    Material.WRITTEN_BOOK,
                    name("relation_book"),
                    lore("relation_book")
            ));
            menu.addMenuClickHandler(RELATIONS_SLOT, (player1, slot, itemStack, clickAction) -> {
                displayRelationsTree(player1, profile, mode, menu, returnPage, definition);
                return false;
            });
        } else {
            menu.replaceExistingItem(RELATIONS_SLOT, notEnoughKnowledge);
        }

        //Trait
        if (PlayerUtils.hasKnowledgePiece(player, entityType, 3) || mode == SlimefunGuideMode.CHEAT_MODE) {
            final Map<String, Object> traitList = SpiritUtils.getTraitInfo(definition.getTrait());
            final CustomItemStack traitItemStack = new CustomItemStack(
                    Material.GLASS,
                    name("trait_item"),
                    lore("trait_item", "{trait_name}", (String) traitList.get("name"))
            );
            menu.replaceExistingItem(TRAIT_SLOT, traitItemStack);
            menu.addMenuClickHandler(TRAIT_SLOT, (player1, slot, itemStack, clickAction) -> {
                playSound(player);
                final boolean showingDescription = itemStack.getItemMeta().hasLore() && itemStack.lore().size() != 3;
                if (showingDescription) {
                    menu.replaceExistingItem(TRAIT_SLOT, traitItemStack);
                } else {
                    final ItemStack descriptionItemStack = traitItemStack.clone();
                    final List<Component> currentLore = descriptionItemStack.lore();
                    currentLore.remove(2);
                    for (String line : (List<String>) traitList.get("lore")) {
                        currentLore.add(Component.text(ChatColors.color(ChatColor.GRAY + line)));
                    }
                    currentLore.add(Component.text(""));
                    currentLore.add(Component.text(translate("trait_item.open")));
                    descriptionItemStack.lore(currentLore);
                    menu.replaceExistingItem(TRAIT_SLOT, descriptionItemStack);
                }
                return false;
            });
        } else {
            menu.replaceExistingItem(TRAIT_SLOT, notEnoughKnowledge);
        }
    }

    @ParametersAreNonnullByDefault
    private void displayRelationsTree(Player player, PlayerProfile profile, SlimefunGuideMode mode, ChestMenu menu, int returnPage, SpiritDefinition definition) {
        // Back Button
        menu.replaceExistingItem(GUIDE_BACK, ChestMenuUtils.getBackButton(player, backLore));

        menu.addMenuClickHandler(GUIDE_BACK, (player1, slot, itemStack, clickAction) -> {
            displayDefinition(player1, profile, mode, menu, returnPage, definition);
            return false;
        });
    
        playSound(player);
        clearDisplay(menu);

        for (int SLOT : DIVIDER) {
            menu.replaceExistingItem(SLOT, ChestMenuUtils.getBackground());
            menu.addMenuClickHandler(SLOT, ChestMenuUtils.getEmptyClickHandler());
        }
        for (int SLOT : AFRAID) {
            menu.replaceExistingItem(SLOT, afraidItemStack);
            menu.addMenuClickHandler(SLOT, ChestMenuUtils.getEmptyClickHandler());
        }
        for (int SLOT : SCARE) {
            menu.replaceExistingItem(SLOT, scareItemStack);
            menu.addMenuClickHandler(SLOT, ChestMenuUtils.getEmptyClickHandler());
        }

        int currentA = 0;
        int currentS = 0;
        for (Map.Entry<String, List<EntityType>> Entry : definition.getRelations().entrySet()) {
            final String relation = Entry.getKey();
            for (EntityType mobType : Entry.getValue()) {
                final SpiritDefinition currentRelator = SpiritsUnchained.getSpiritsManager().getSpiritMap().get(mobType);
                final int slot = relation.equals("Afraid") ? AFRAID_ENTRIES[currentA] : SCARE_ENTRIES[currentS];
                currentA = relation.equals("Afraid") ? currentA + 1 : currentA;
                currentS = relation.equals("Scare") ? currentS + 1 : currentS;
                menu.replaceExistingItem(slot, getSpiritMenuItem(currentRelator));
                menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void clearDisplay(ChestMenu menu) {
        for (int i = 0; i < 45; i++) {
            final int slot = i + 9;
            menu.replaceExistingItem(slot, null);
            menu.addMenuClickHandler(slot, (player1, i1, itemStack1, clickAction) -> false);
        }
    }

    @ParametersAreNonnullByDefault
    private ItemStack getSpiritMenuItem(SpiritDefinition definition) {
        final ItemStack itemStack = new ItemStack(Material.FIREWORK_STAR);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final ChatColor chatColor = SpiritUtils.tierColor(definition.getTier());
        final String spiritType  = Translate.getEntity(Locale.zh_tw, definition.getType());

        itemMeta.displayName(Component.text(name("spirit_item").replace("{tier_color}", chatColor.toString()).replace("{mob_type}", spiritType)));
        ((FireworkEffectMeta) itemMeta).setEffect(SpiritUtils.effectColor(definition.getType()));

        final List<Component> lore = new ArrayList<>();
        for (String line : lore("spirit_item", "{mob_type}", spiritType, "{tier_color}", chatColor.toString(), "{tier}", String.valueOf(definition.getTier()))) {
            lore.add(Component.text(line));
        }
        itemMeta.lore(lore);
        PersistentDataAPI.setString(itemMeta, new NamespacedKey(SpiritsUnchained.getInstance(), "spirit_type"), spiritType);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
