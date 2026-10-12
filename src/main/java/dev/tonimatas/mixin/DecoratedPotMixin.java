package dev.tonimatas.mixin;

import com.greymerk.roguelike.dungeon.Difficulty;
import com.greymerk.roguelike.editor.Coord;
import com.greymerk.roguelike.editor.IWorldEditor;
import com.greymerk.roguelike.editor.MetaBlock;
import com.greymerk.roguelike.editor.blocks.DecoratedPot;
import com.greymerk.roguelike.treasure.loot.Loot;
import com.greymerk.roguelike.util.IWeighted;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.entity.PotDecorations;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrDecoratedPotBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(DecoratedPot.class)
public class DecoratedPotMixin {
    @Shadow
    private static IWeighted<Item> getFaceRoller() {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    private static Optional<ItemStackTemplate> faceOf(Item item) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    /**
     * @author TonimatasDEV
     * @reason Decorated Pot compatibility with Lootr
     */
    @Overwrite
    public static void set(IWorldEditor editor, RandomSource rand, Difficulty diff, Coord origin, Loot type) {
        editor.setBlockEntity(origin, MetaBlock.of(LootrRegistry.getDecoratedPotBlock()), LootrDecoratedPotBlockEntity.class).ifPresent((potEntity) -> {
            IWeighted<Item> faceroll = getFaceRoller();
            PotDecorations sherds = new PotDecorations(faceOf(faceroll.get(rand)), faceOf(faceroll.get(rand)), faceOf(faceroll.get(rand)), faceOf(faceroll.get(rand)));
            DataComponentPatch.Builder changes = DataComponentPatch.builder();
            changes.set(DataComponents.POT_DECORATIONS, sherds);
            potEntity.applyComponents(potEntity.components(), changes.build());
            ItemStack loot = Loot.getLootItem(editor, type, rand, diff);
            potEntity.setDataReferenceInventory(NonNullList.withSize(1, loot));
            potEntity.setChanged();
        });
    }
}
