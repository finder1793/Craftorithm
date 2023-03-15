package com.github.yufiriamazenta.craftorithm.cmd.subcmd.item;

import com.github.yufiriamazenta.craftorithm.cmd.subcmd.AbstractSubCommand;
import com.github.yufiriamazenta.craftorithm.cmd.subcmd.ISubCommand;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemSaveCommand extends AbstractSubCommand {

    public static final ISubCommand INSTANCE = new ItemSaveCommand();

    private ItemSaveCommand() {
        super("save");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 2) {
            sendNotEnoughCmdParamMsg(sender, 2 - args.size());
            return true;
        }
        if (!checkSenderIsPlayer(sender))
            return true;

        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            LangUtil.sendMsg(sender, "command.item.save.failed_save_air");
            return true;
        }

        ItemManager.addCraftorithmItem(args.get(0), args.get(1), item.clone());
        LangUtil.sendMsg(sender, "command.item.save.success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        switch (args.size()) {
            case 0:
            case 1:
                List<String> tabList = new ArrayList<>(ItemManager.getItemFileMap().keySet());
                filterTabList(tabList, args.get(0));
                return tabList;
            case 2:
            default:
                return Collections.singletonList("");
        }
    }
}