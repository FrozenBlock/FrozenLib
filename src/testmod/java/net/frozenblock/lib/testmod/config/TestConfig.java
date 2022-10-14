package net.frozenblock.lib.testmod.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.FrozenConfig;
import net.frozenblock.lib.testmod.FrozenTestMain;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Config(name = FrozenTestMain.MOD_ID)
class TestConfig extends PartitioningSerializer.GlobalData implements ConfigData {

    boolean testBoolean = true;

    @ConfigEntry.Gui.CollapsibleObject
    SubMenu subMenu = new SubMenu();

    static class SubMenu {
        boolean testSubMenuBoolean = true;
    }

    static TestConfig get() {
        if (!FrozenTestMain.areConfigsInit) {
            AutoConfig.register(TestConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
            FrozenTestMain.areConfigsInit = true;
        }
        return AutoConfig.getConfigHolder(TestConfig.class).getConfig();
    }

    static Component text(String key) {
        return Component.translatable("option." + FrozenTestMain.MOD_ID + "." + key);
    }

    static Component tooltip(String key) {
        return Component.translatable("tooltip." + FrozenTestMain.MOD_ID + "." + key);
    }

    @Environment(EnvType.CLIENT)
    static Screen buildScreen(Screen parent) {
        var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
        configBuilder.setSavingRunnable(() -> AutoConfig.getConfigHolder(TestConfig.class).save());
        var general = configBuilder.getOrCreateCategory(text("general"));
        ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
        setupEntries(general, entryBuilder);
        return configBuilder.build();
    }

    @Environment(EnvType.CLIENT)
    static void setupEntries(ConfigCategory category, ConfigEntryBuilder entryBuilder) {
        var config = TestConfig.get();
        var subMenu = config.subMenu;
        category.setBackground(new ResourceLocation("textures/block/packed_mud.png"));
        var test = category.addEntry(entryBuilder.startBooleanToggle(text("test_boolean"), config.testBoolean)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.testBoolean = newValue)
                .setTooltip(tooltip("test_boolean"))
                .build()
        );

        var testSubMenuBoolean = entryBuilder.startBooleanToggle(text("test_submenu_boolean"), subMenu.testSubMenuBoolean)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> subMenu.testSubMenuBoolean = newValue)
                .setTooltip(tooltip("test_submenu_boolean"))
                .build();

        var testSubMenuCategory = FrozenConfig.createSubCategory(entryBuilder, category, text("test_subcategory"),
                false,
                tooltip("test_subcategory"),
                testSubMenuBoolean
        );
    }

}
