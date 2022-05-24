package top.oasismc.oasisrecipe.cmd.subcmd;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.ISubCommand;
import top.oasismc.oasisrecipe.cmd.AbstractSubCommand;
import top.oasismc.oasisrecipe.config.ConfigFile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveCommand extends AbstractSubCommand {

    private static final ConfigFile removedRecipeConfig = new ConfigFile("removed_recipes.yml");
    private final Map<NamespacedKey, Recipe> recipeMap;

    public static final ISubCommand INSTANCE = new RemoveCommand();

    private RemoveCommand() {
        super("remove", null);
        recipeMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            OasisRecipe.getPlugin().sendMsg(sender, "commands.missingParam");
            return true;
        }
        if (removeRecipe(args.get(0))) {
            OasisRecipe.getPlugin().sendMsg(sender, "commands.removed");
            reloadRecipeSet();
        }
        else
            OasisRecipe.getPlugin().sendMsg(sender, "commands.notExist");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> arrayList = new ArrayList<>();
            for (NamespacedKey key : recipeMap.keySet()) {
                String str = key.toString();
                if (str.startsWith(args.get(0)))
                    arrayList.add(key.toString());
            }
            return arrayList;
        }
        return super.onTabComplete(sender, args);
    }

    public void reloadRecipeSet() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        recipeMap.clear();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            NamespacedKey key = getRecipeKey(recipe);
            recipeMap.put(key, recipe);
        }
    }

    public Map<NamespacedKey, Recipe> getRecipeMap() {
        return Collections.unmodifiableMap(recipeMap);
    }

    public static ConfigFile getRemovedRecipeConfig() {
        return removedRecipeConfig;
    }

    public boolean removeRecipe(String keyStr) {
        NamespacedKey key = NamespacedKey.fromString(keyStr);
        Recipe recipe = recipeMap.get(key);
        if (recipe == null) {
            OasisRecipe.getPlugin().sendMsg(Bukkit.getConsoleSender(), "commands.notExist");
            return false;
        }
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe1 = recipeIterator.next();
            NamespacedKey key1 = getRecipeKey(recipe1);
            if (key.equals(key1)) {
                recipeIterator.remove();
                List<String> removedList = removedRecipeConfig.getConfig().getStringList("recipes");
                if (!removedList.contains(keyStr))
                    removedList.add(keyStr);
                removedRecipeConfig.getConfig().set("recipes", removedList);
                removedRecipeConfig.saveConfig();
                return true;
            }
        }
        return false;
    }

    private NamespacedKey getRecipeKey(Recipe recipe) {
        try {
            Class<?> recipeClass = Class.forName(recipe.getClass().getName());
            Method getKeyMethod = recipeClass.getMethod("getKey");
            return (NamespacedKey) getKeyMethod.invoke(recipe);
        } catch (Exception e) {
            return null;
        }

    }

}