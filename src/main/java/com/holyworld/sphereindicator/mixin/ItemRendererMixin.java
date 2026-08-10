package com.holyworld.sphereindicator.mixin;

import com.holyworld.sphereindicator.SphereIndicatorMod;
import com.holyworld.sphereindicator.SphereType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    // Флаг для защиты от рекурсии: когда мы сами вызываем renderBakedItemModel,
    // повторный вход в инжект должен быть пропущен.
    private boolean sphereIndicator_rendering = false;

    // Shadow на приватный метод ItemRenderer, который рисует уже выбранную модель.
    // Yarn mapping 1.16.5+build.10: renderBakedItemModel
    @Shadow
    private void renderBakedItemModel(BakedModel model, ItemStack stack,
                                       int light, int overlay,
                                       MatrixStack matrices,
                                       VertexConsumerProvider vertexConsumers) {
        throw new AssertionError("Mixin shadow not applied");
    }

    /**
     * Перехватываем публичный renderItem (тот, что вызывается из GUI и рук).
     *
     * Yarn 1.16.5+build.10 сигнатура:
     * renderItem(ItemStack, ModelTransformation$Mode, boolean,
     *            MatrixStack, VertexConsumerProvider, int, int, BakedModel)V
     */
    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;" +
                 "Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;" +
                 "ZLnet/minecraft/client/util/math/MatrixStack;" +
                 "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                 "IILnet/minecraft/client/render/model/BakedModel;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sphereIndicator_onRenderItem(
            ItemStack stack,
            ModelTransformation.Mode renderMode,
            boolean leftHand,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BakedModel model,
            CallbackInfo ci
    ) {
        // Защита от рекурсии
        if (sphereIndicator_rendering) return;
        // Работаем только с player_head
        if (stack == null || stack.isEmpty()) return;
        if (stack.getItem() != Items.PLAYER_HEAD) return;

        SphereType type = SphereType.detect(stack);
        if (type == SphereType.NONE) return;

        // Выбираем идентификатор нашей кастомной модели
        Identifier modelId = (type == SphereType.URON3_BRONYA2)
                ? new Identifier(SphereIndicatorMod.MOD_ID, "item/sphere_uron3")
                : new Identifier(SphereIndicatorMod.MOD_ID, "item/sphere_bronya3");

        // Получаем BakedModel из ModelManager
        BakedModelManager modelManager = MinecraftClient.getInstance()
                .getItemRenderer()
                .getModels()
                .getModelManager();

        BakedModel customModel = modelManager.getModel(modelId);

        // Если модель не найдена — не ломаем рендер, просто пропускаем
        if (customModel == null || customModel == modelManager.getMissingModel()) {
            SphereIndicatorMod.LOGGER.warn(
                "[SphereIndicator] Модель не найдена: {}. " +
                "Убедись что текстуры лежат в assets/sphereindicator/textures/item/",
                modelId
            );
            return;
        }

        // Отменяем стандартный рендер
        ci.cancel();

        // Рисуем нашей моделью через shadow-метод (без рекурсии через инжект)
        sphereIndicator_rendering = true;
        try {
            renderBakedItemModel(customModel, stack, light, overlay, matrices, vertexConsumers);
        } finally {
            sphereIndicator_rendering = false;
        }
    }
}
