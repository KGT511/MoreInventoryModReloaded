package moreinventory.data;

import moreinventory.core.MoreInventoryMOD;
import moreinventory.data.lang.EnUsLanguageGenerator;
import moreinventory.data.lang.JaJpLanguageGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreInventoryMOD.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        init();

        var generator = event.getGenerator();
        var packout = generator.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new BlockStateGenerator(packout, existingFileHelper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packout, existingFileHelper));

        var blockTags = new BlockTagGenerator(packout, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), LootTablesGenerator.create(packout, lookupProvider));
        generator.addProvider(event.includeServer(), new RecipesGenerator.Runner(packout, lookupProvider));
        generator.addProvider(event.includeServer(), new EnUsLanguageGenerator(packout, MoreInventoryMOD.MOD_ID));
        generator.addProvider(event.includeServer(), new JaJpLanguageGenerator(packout, MoreInventoryMOD.MOD_ID));
    }

    private static void init() {
        MoreInventoryMOD.init();
    }
}
