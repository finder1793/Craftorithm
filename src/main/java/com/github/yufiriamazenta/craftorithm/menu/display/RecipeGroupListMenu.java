package com.github.yufiriamazenta.craftorithm.menu.display;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.menu.editor.RecipeGroupEditor;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.ItemUtils;
import crypticlib.chat.TextProcessor;
import crypticlib.ui.display.Icon;
import crypticlib.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeGroupListMenu extends Menu {

    private int page;
    private final int maxPage;
    private final List<Map.Entry<String, ItemStack>> recipeGroupResultList;

    public RecipeGroupListMenu(Player player) {
        super(player);
        Map<String, ItemStack> recipeResultMap = new HashMap<>();
        RecipeManager.INSTANCE.recipeMap().forEach((recipeType, recipeGroupMap) ->
            recipeGroupMap.forEach((groupName, recipeGroup) -> {
                if (recipeGroup == null || recipeGroup.isEmpty())
                    return;
                Recipe firstRecipe = RecipeManager.INSTANCE.getRecipe(recipeGroup.groupRecipeKeys().get(0));
                if (firstRecipe == null)
                    return;
                recipeResultMap.put(groupName, firstRecipe.getResult());
            })
        );
        recipeGroupResultList = new ArrayList<>(recipeResultMap.entrySet());
        page = 0;
        int recipeGroupNum = recipeResultMap.size();
        if (recipeGroupNum % 45 == 0) {
            maxPage = recipeGroupNum / 45;
        } else {
            maxPage = recipeGroupNum / 45 + 1;
        }
        recipeGroupResultList.sort((o1, o2) -> {
            int sortId = RecipeManager.INSTANCE.getRecipeGroupSortId(o1.getKey());
            int sortId2 = RecipeManager.INSTANCE.getRecipeGroupSortId(o2.getKey());
            return Integer.compare(sortId, sortId2);
        });
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        resetIcons();
        Inventory inventory = Bukkit.createInventory(
            this,
            54,
            TextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_TITLE.value(player))
        );
        for (Integer slot : super.slotMap.keySet()) {
            inventory.setItem(slot, slotMap.get(slot).display());
        }
        return inventory;
    }

    public void nextPage() {
        setPage(Math.min(page + 1, maxPage - 1)).resetIcons();
        openedInventory.clear();
        for (Integer slot : slotMap.keySet()) {
            openedInventory.setItem(slot, slotMap.get(slot).display());
        }
    }

    public void previousPage() {
        setPage(Math.max(page - 1, 0)).resetIcons();
        openedInventory.clear();
        for (Integer slot : slotMap.keySet()) {
            openedInventory.setItem(slot, slotMap.get(slot).display());
        }
    }

    private void resetIcons() {
        slotMap.clear();
        int []frameSlots = {45, 47, 48, 49, 50, 51, 53};
        Icon frameIcon = new Icon(
            Material.BLACK_STAINED_GLASS_PANE,
            TextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_FRAME.value(player)
        ));
        for (int frameSlot : frameSlots) {
            slotMap.put(frameSlot, frameIcon);
        }
        slotMap.put(46, new Icon(
            Material.PAPER,
            TextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_PREVIOUS.value(player)),
            event -> previousPage()
        ));
        slotMap.put(52, new Icon(
            Material.PAPER,
            TextProcessor.color(Languages.MENU_NEW_RECIPE_LIST_ICON_NEXT.value(player)),
            event -> nextPage()
        ));
        int recipeSlot = page * 45;
        for (int invSlot = 0; invSlot < 45 && recipeSlot < recipeGroupResultList.size(); invSlot++, recipeSlot++) {
            slotMap.put(invSlot, wrapIcon(recipeSlot));
        }
        for (int i = 0; i < 45; i++) {
            if (slotMap.containsKey(i))
                continue;
            slotMap.put(i, new Icon(new ItemStack(Material.AIR)));
        }
    }

    @NotNull
    private Icon wrapIcon(int recipeSlot) {
        ItemStack display = recipeGroupResultList.get(recipeSlot).getValue();
        String recipeGroupName = recipeGroupResultList.get(recipeSlot).getKey();
        Icon icon = new Icon(display, event -> {
            switch (event.getClick()) {
                case RIGHT:
                case SHIFT_RIGHT:
                    if (!player.hasPermission("craftorithm.edit_recipe")) {
                        return;
                    }
                    new RecipeGroupEditor(
                        (Player) event.getWhoClicked(),
                        RecipeManager.INSTANCE.getRecipeGroup(recipeGroupName)
                    ).openMenu();
                    break;
                case LEFT:
                case SHIFT_LEFT:
                default:
                    new RecipeListMenu(
                        (Player) event.getWhoClicked(),
                        RecipeManager.INSTANCE.getRecipeGroup(recipeGroupName).groupRecipeKeys(),
                        this
                    ).openMenu();
                    break;
            }
        });
        ItemUtils.setLore(icon.display(), Languages.MENU_NEW_RECIPE_LIST_ICON_ELEMENTS_LORE.value(player));
        return icon;
    }

    public int page() {
        return page;
    }

    public RecipeGroupListMenu setPage(int page) {
        this.page = page;
        return this;
    }

}