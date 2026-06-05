package com.pastdeathghost.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.List;

public class GhostRenderCommandQueue implements OrderedRenderCommandQueue {
    private final OrderedRenderCommandQueue delegate;
    private final int alpha;

    public GhostRenderCommandQueue(OrderedRenderCommandQueue delegate, int alpha) {
        this.delegate = delegate;
        this.alpha = alpha;
    }

    private int adjustColor(int color) {
        int originalAlpha = (color >> 24) & 0xFF;
        int targetAlpha = (originalAlpha * alpha) / 255;
        return (color & 0x00FFFFFF) | (targetAlpha << 24);
    }

    private int withAlpha(int color) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    @Override
    public RenderCommandQueue getBatchingQueue(int i) {
        RenderCommandQueue queue = delegate.getBatchingQueue(i);
        if (queue instanceof OrderedRenderCommandQueue) {
            return new GhostRenderCommandQueue((OrderedRenderCommandQueue) queue, alpha);
        }
        return queue;
    }

    @Override
    public void submitShadowPieces(MatrixStack matrixStack, float f, List<EntityRenderState.ShadowPiece> list) {
        delegate.submitShadowPieces(matrixStack, f, list);
    }

    @Override
    public void submitLabel(MatrixStack matrixStack, Vec3d vec3d, int i, Text text, boolean b, int j, double d, CameraRenderState cameraRenderState) {
        if (vec3d == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        matrixStack.push();
        matrixStack.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
        matrixStack.multiply(cameraRenderState.orientation);
        matrixStack.scale(0.025F, -0.025F, 0.025F);

        float x = -textRenderer.getWidth(text) / 2.0F;
        int backgroundColor = ((int)(client.options.getTextBackgroundOpacity(0.25F) * alpha) << 24);
        OrderedText orderedText = text.asOrderedText();
        if (b) {
            delegate.submitText(matrixStack, x, i, orderedText, false, TextRenderer.TextLayerType.NORMAL, LightmapTextureManager.applyEmission(j, 2), withAlpha(0x00FFFFFF), 0, 0);
            delegate.submitText(matrixStack, x, i, orderedText, false, TextRenderer.TextLayerType.SEE_THROUGH, j, withAlpha(0x80FFFFFF), backgroundColor, 0);
        } else {
            delegate.submitText(matrixStack, x, i, orderedText, false, TextRenderer.TextLayerType.NORMAL, j, withAlpha(0x80FFFFFF), backgroundColor, 0);
        }

        matrixStack.pop();
    }

    @Override
    public void submitText(MatrixStack matrixStack, float f, float g, OrderedText orderedText, boolean b, net.minecraft.client.font.TextRenderer.TextLayerType textLayerType, int i, int j, int k, int l) {
        delegate.submitText(matrixStack, f, g, orderedText, b, textLayerType, i, adjustColor(j), adjustColor(k), adjustColor(l));
    }

    @Override
    public void submitFire(MatrixStack matrixStack, EntityRenderState entityRenderState, Quaternionf quaternionf) {
        delegate.submitFire(matrixStack, entityRenderState, quaternionf);
    }

    @Override
    public void submitLeash(MatrixStack matrixStack, EntityRenderState.LeashData leashData) {
        delegate.submitLeash(matrixStack, leashData);
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer layer, int color, int overlay, int light, net.minecraft.client.texture.Sprite sprite, int c, ModelCommandRenderer.CrumblingOverlayCommand o) {
        delegate.submitModel(model, state, matrices, layer, color, overlay, light, sprite, c, o);
    }

    @Override
    public void submitModelPart(ModelPart modelPart, MatrixStack matrixStack, RenderLayer renderLayer, int i, int j, net.minecraft.client.texture.Sprite sprite, boolean b, boolean c, int k, ModelCommandRenderer.CrumblingOverlayCommand o, int l) {
        delegate.submitModelPart(modelPart, matrixStack, renderLayer, i, j, sprite, b, c, k, o, l);
    }

    @Override
    public void submitBlock(MatrixStack matrixStack, net.minecraft.block.BlockState blockState, int i, int j, int k) {
        delegate.submitBlock(matrixStack, blockState, i, j, k);
    }

    @Override
    public void submitMovingBlock(MatrixStack matrixStack, net.minecraft.client.render.block.MovingBlockRenderState movingBlockRenderState) {
        delegate.submitMovingBlock(matrixStack, movingBlockRenderState);
    }

    @Override
    public void submitBlockStateModel(MatrixStack matrixStack, RenderLayer renderLayer, net.minecraft.client.render.model.BlockStateModel blockStateModel, float f, float g, float h, int i, int j, int k) {
        delegate.submitBlockStateModel(matrixStack, renderLayer, blockStateModel, f, g, h, i, j, k);
    }

    @Override
    public void submitItem(MatrixStack matrixStack, net.minecraft.item.ItemDisplayContext itemDisplayContext, int i, int j, int k, int[] is, List<net.minecraft.client.render.model.BakedQuad> list, RenderLayer renderLayer, net.minecraft.client.render.item.ItemRenderState.Glint glint) {
        delegate.submitItem(matrixStack, itemDisplayContext, i, j, k, is, list, renderLayer, glint);
    }

    @Override
    public void submitCustom(MatrixStack matrixStack, RenderLayer renderLayer, OrderedRenderCommandQueue.Custom custom) {
        delegate.submitCustom(matrixStack, renderLayer, custom);
    }

    @Override
    public void submitCustom(OrderedRenderCommandQueue.LayeredCustom layeredCustom) {
        delegate.submitCustom(layeredCustom);
    }
}
