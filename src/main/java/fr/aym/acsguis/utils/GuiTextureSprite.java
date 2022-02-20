package fr.aym.acsguis.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * A texture sprite (part of a texture)
 */
public class GuiTextureSprite implements IGuiTexture {
    private final ResourceLocation atlasTexture;
    private int atlasWidth, atlasHeight;

    private final int textureU, textureV;
    private int textureWidth;
    private int textureHeight;

    private final GuiTextureLoader loader;

    /**
     * @param atlasTexture  The texture location
     * @param textureU      Sprite U
     * @param textureV      Sprite V
     * @param textureWidth  Sprite width
     * @param textureHeight Sprite height
     */
    public GuiTextureSprite(ResourceLocation atlasTexture, int textureU, int textureV, int textureWidth, int textureHeight) {
        this.atlasTexture = atlasTexture;
        this.textureU = textureU;
        this.textureV = textureV;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        System.out.println("Texture is " + atlasTexture + " " + atlasTexture.getNamespace());
        if (atlasTexture.getNamespace().startsWith("http")) {
            loader = new HttpGuiTextureLoader(atlasTexture);
        } else {
            loader = new GuiTextureLoader(atlasTexture);
        }
    }

    /**
     * Sets u and v to 0, sets width and height to the entire texture
     *
     * @param location The texture location
     */
    public GuiTextureSprite(ResourceLocation location) {
        this(location, 0, 0, 0, 0);
    }

    /**
     * Draws this texture, multiple times if spriteWidth < width or spriteHeight < height
     *
     * @param x             Screen x coord
     * @param y             Screen y coord
     * @param spriteWidth   Width of a sprite on the screen
     * @param spriteHeight  Height of a sprite on the screen
     * @param uOffset       U offset added to the u coord of this sprite
     * @param vOffset       V offset added to the v coord of this sprite
     * @param textureWidth  Sprite width on texture
     * @param textureHeight Sprite height on texture
     * @param width         The total width to draw on screen
     * @param height        The total height to draw on screen
     */
    public void drawSprite(int x, int y, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight, int width, int height) {
        if (atlasWidth == 0 && atlasHeight == 0) {//Not loaded
            Minecraft.getMinecraft().renderEngine.loadTexture(atlasTexture, loader);
            atlasWidth = loader.atlasWidth;
            atlasHeight = loader.atlasHeight;

            //Minecraft.getMinecraft().fontRenderer.drawString("...", x, y, 0xFFFFFF);
        } else {
            if (textureWidth == 0) //Auto width
                textureWidth = atlasWidth;
            if (textureHeight == 0) //Auto height
                textureHeight = atlasHeight;

            Minecraft.getMinecraft().renderEngine.bindTexture(atlasTexture);
            double nRepeatY = height / (double) spriteHeight;
            int j = 0;

            do {
                int i = 0;
                double nRepeatX = width / (double) spriteWidth;
                double ySize = MathHelper.clamp(nRepeatY, 0, 1);

                do {
                    double xSize = MathHelper.clamp(nRepeatX, 0, 1);
                    drawTexturedModalRect(x + i * spriteWidth, y + j * spriteHeight, (int) Math.ceil(spriteWidth * xSize), (int) Math.ceil(spriteHeight * ySize), uOffset, vOffset, (int) Math.ceil(textureWidth * xSize), (int) Math.ceil(textureHeight * ySize));
                    i++;
                    nRepeatX -= 1;
                } while (nRepeatX > 0);

                j++;
                nRepeatY -= 1;
            } while (nRepeatY > 0);
        }
    }

    private void drawTexturedModalRect(int x, int y, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        Gui.drawScaledCustomSizeModalRect(x, y, textureU + uOffset, textureV + vOffset, textureWidth, textureHeight, spriteWidth, spriteHeight, atlasWidth, atlasHeight);
    }

    /**
     * @return Texture sprite width
     */
    public int getTextureWidth() {
        return textureWidth;
    }

    /**
     * @return Texture sprite height
     */
    public int getTextureHeight() {
        return textureHeight;
    }

    @Override
    public String toString() {
        return "GuiTextureSprite{" +
                atlasTexture +
                '}';
    }
}
