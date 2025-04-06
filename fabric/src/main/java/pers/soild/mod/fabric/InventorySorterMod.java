package com.example.inventorysorter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;

import java.util.*;

@Environment(EnvType.CLIENT)
public class  implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof InventoryScreen inventoryScreen) {
                screen.addDrawableChild(ButtonWidget.builder(Text.literal("整理"), (button) -> sortInventory(client))
                        .dimensions(screen.width / 2 + 65, screen.height / 2 - 22, 50, 20)
                        .build());
            }
        });
    }

    private void sortInventory(MinecraftClient client) {
        if (client.player == null) return;

        List<ItemStack> inventoryItems = new ArrayList<>();

        // 获取主物品栏物品
        for (int i = 9; i < 36; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (!stack.isEmpty()) inventoryItems.add(stack.copy());
            client.player.getInventory().setStack(i, ItemStack.EMPTY);
        }

        // 按照物品类别排序
        inventoryItems.sort(Comparator.comparing(this::getItemGroupOrdinal).thenComparing(stack -> stack.getItem().toString()));

        // 放回已排序物品
        for (int i = 0; i < inventoryItems.size(); i++) {
            client.player.getInventory().setStack(i + 9, inventoryItems.get(i));
        }
    }

    // 获取物品分类的序号
    private int getItemGroupOrdinal(ItemStack stack) {
        Item item = stack.getItem();
        ItemGroup group = item.getGroup();
        return group == null ? Integer.MAX_VALUE : group.getIndex();
    }
}
