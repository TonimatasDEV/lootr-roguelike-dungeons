package dev.tonimatas.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.interfaces.accessor.ILootrDataAccessor;
import noobanidus.mods.lootr.common.block.entity.BlockEntityTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.WeakHashMap;

@Mixin(BlockEntityTicker.class)
public class BlockEntityTickerMixin {
    @Unique
    private static final WeakHashMap<BlockPos, NonNullList<ItemStack>> LRP$CACHE = new WeakHashMap<>();

    @Inject(method = "replaceEntity", at = @At(value = "INVOKE", target = "Lnoobanidus/mods/lootr/common/api/interfaces/accessor/ILootrDataAccessor;setLootTable(Ljava/lang/Object;Lnet/minecraft/resources/ResourceKey;J)V", shift = At.Shift.AFTER))
    private static void lrd$replaceEntity$getInventory(ServerLevel level, BlockPos entityPos, ILootrDataAccessor<BlockEntity> adapter, BlockEntity be, BlockState replacement, ResourceKey<LootTable> table, long seed, CallbackInfo ci) {
        if (!table.identifier().getNamespace().equals("roguelike")) return;

        NonNullList<ItemStack> inventory = adapter.getInventoryCopy(be);

        if (inventory != null) {
            if (be instanceof RandomizableContainerBlockEntity blockEntity) {
                for(int i = 0; i < inventory.size(); ++i) {
                    blockEntity.setItem(i, ItemStack.EMPTY);
                }
            }

            LRP$CACHE.put(entityPos, inventory);
        }
    }

    @Redirect(method = "replaceEntity", at = @At(value = "INVOKE", target = "Lnoobanidus/mods/lootr/common/api/data/blockentity/ILootrBlockEntity;setLootTableInternal(Lnet/minecraft/resources/ResourceKey;J)V"))
    private static void lrd$replaceEntity$setInventory(ILootrBlockEntity instance, ResourceKey<LootTable> lootTable, long seed, ServerLevel level, BlockPos entityPos, ILootrDataAccessor<BlockEntity> adapter, BlockEntity be, BlockState replacement, ResourceKey<LootTable> table, long originalSeed) {
        if (table.identifier().getNamespace().equals("roguelike")) {
            NonNullList<ItemStack> inventory = LRP$CACHE.remove(entityPos);
            if (inventory != null && !inventory.isEmpty()) {
                instance.setDataReferenceInventory(inventory);
            }
        } else {
            instance.setLootTableInternal(lootTable, seed);
        }
    }
}
