package moreinventory.data.lang;

import moreinventory.block.Blocks;
import moreinventory.core.MoreInventoryMOD;
import moreinventory.item.Items;
import moreinventory.item.PouchItem;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.HashMap;

public class JaJpLanguageGenerator extends LanguageProvider {
    public JaJpLanguageGenerator(PackOutput generator, String modid) {
        super(generator, modid, "ja_jp");
    }

    public static final HashMap<DyeColor, String> colorMap = new HashMap<>();

    static {
        colorMap.put(DyeColor.WHITE, "白");
        colorMap.put(DyeColor.ORANGE, "橙");
        colorMap.put(DyeColor.MAGENTA, "赤紫");
        colorMap.put(DyeColor.LIGHT_BLUE, "空");
        colorMap.put(DyeColor.YELLOW, "黄");
        colorMap.put(DyeColor.LIME, "黄緑");
        colorMap.put(DyeColor.PINK, "桃");
        colorMap.put(DyeColor.GRAY, "灰");
        colorMap.put(DyeColor.LIGHT_GRAY, "薄灰");
        colorMap.put(DyeColor.CYAN, "青緑");
        colorMap.put(DyeColor.PURPLE, "紫");
        colorMap.put(DyeColor.BLUE, "青");
        colorMap.put(DyeColor.BROWN, "茶");
        colorMap.put(DyeColor.GREEN, "緑");
        colorMap.put(DyeColor.RED, "赤");
        colorMap.put(DyeColor.BLACK, "黒");
    }

    @Override
    public String getName() {
        return MoreInventoryMOD.MOD_ID + " " + super.getName();
    }

    @Override
    protected void addTranslations() {
        add(Blocks.CATCHALL.get(), "ガラクタ入れ");
        add(Items.TRANSPORTER.get(), "トランスポーター");
        add(Items.SPANNER.get(), "スパナ");

        add(Blocks.WOOD_STORAGE_BOX.get(), "木のコンテナ");
        add(Blocks.IRON_STORAGE_BOX.get(), "鉄のコンテナ");
        add(Blocks.GOLD_STORAGE_BOX.get(), "金のコンテナ");
        add(Blocks.DIAMOND_STORAGE_BOX.get(), "ダイヤモンドのコンテナ");
        add(Blocks.EMERALD_STORAGE_BOX.get(), "エメラルドのコンテナ");
        add(Blocks.COPPER_STORAGE_BOX.get(), "銅のコンテナ");
        add(Blocks.TIN_STORAGE_BOX.get(), "錫のコンテナ");
        add(Blocks.BRONZE_STORAGE_BOX.get(), "青銅のコンテナ");
        add(Blocks.SILVER_STORAGE_BOX.get(), "銀のコンテナ");
        add(Blocks.STEEL_STORAGE_BOX.get(), "鋼鉄のコンテナ");
        add(Blocks.GLASS_STORAGE_BOX.get(), "ガラスのコンテナ");

        add(Blocks.IMPORTER.get(), "インポーター");
        add(Blocks.EXPORTER.get(), "エクスポーター");
        add(Text.importerRegister, "登録");
        add(Text.importerRegisterOn, "する");
        add(Text.importerRegisterOff, "しない");
        add(Text.importerRegisterOnDetail, "未登録のアイテムがある場合コンテナに新規登録して移動します");
        add(Text.importerRegisterOffDetail, "コンテナに登録されていないアイテムを移動しません");
        add(Text.importerMove, "搬入");
        add(Text.importerMoveWhite, "する");
        add(Text.importerMoveBlack, "しない");
        add(Text.importerMoveWhiteDetail, "リストのアイテムのみ輸送します");
        add(Text.importerMoveBlackDetail, "リスト以外のアイテムを輸送します");

        add(Items.POUCH.get(), "ポーチ");
        for (DyeColor color : DyeColor.values()) {
            add(PouchItem.byColor(color), colorMap.get(color) + "色のポーチ");
        }
        add(Items.LEATHER_PACK.get(), "革の詰め物");

        add(Text.pouchConfig, "コンフィグ");
        add(Text.pouchConfigStorageBox, "有効にすると、コンテナはこのポーチからも収集します");
        add(Text.pouchConfigHotBar, "有効にすると、このポーチはスニークしながら使用した時にホットバーから登録されたアイテムを収集します");
        add(Text.pouchConfigAutoCollect, "有効にすると、このポーチは登録されたアイテムを拾った時に自動収集します");

        add(Items.BRUSH.get(), "ブラシ");
        add(Items.IRON_PLATING.get(), "鉄メッキ");
        add(Items.GOLD_PLATING.get(), "金メッキ");
        add(Items.DIAMOND_PLATING.get(), "ダイヤモンドメッキ");
        add(Items.EMERALD_PLATING.get(), "エメラルドメッキ");
        add(Items.COPPER_PLATING.get(), "銅メッキ");
        add(Items.TIN_PLATING.get(), "錫メッキ");
        add(Items.BRONZE_PLATING.get(), "青銅メッキ");
        add(Items.SILVER_PLATING.get(), "銀メッキ");
        add(Items.STEEL_PLATING.get(), "鋼鉄メッキ");
    }
}
