package moreinventory.data;

import moreinventory.block.Blocks;
import moreinventory.core.MoreInventoryMOD;
import moreinventory.storagebox.StorageBox;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
        super(packOutput, lookupProvider, MoreInventoryMOD.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(Blocks.CATCHALL.get());

        for (var val : StorageBox.storageBoxMap.values()) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(val.block);
        }
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(Blocks.IMPORTER.get(), Blocks.EXPORTER.get());
    }

}
