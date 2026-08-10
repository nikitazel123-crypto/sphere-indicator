package com.holyworld.sphereindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SphereIndicatorMod implements ClientModInitializer {

    public static final String MOD_ID = "sphereindicator";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // Текстуры для двух типов сфер.
    // Файлы должны лежать в:
    //   assets/sphereindicator/textures/item/sphere_uron3.png
    //   assets/sphereindicator/textures/item/sphere_bronya3.png
    public static final Identifier TEXTURE_URON3   = new Identifier(MOD_ID, "textures/item/sphere_uron3.png");
    public static final Identifier TEXTURE_BRONYA3 = new Identifier(MOD_ID, "textures/item/sphere_bronya3.png");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[SphereIndicator] Мод загружен. Жду сферы HolyWorld...");
    }
}
