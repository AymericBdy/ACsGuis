package fr.aym.acsguis.utils;

import fr.aym.acsguis.api.ACsGuiApi;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A texture loader used by {@link GuiTextureSprite}
 */
public class GuiTextureLoader extends AbstractTexture {
    protected final ResourceLocation atlasTexture;
    protected int atlasWidth, atlasHeight;

    /**
     * @param atlasTexture  The texture location
     * @param textureU      Sprite U
     * @param textureV      Sprite V
     * @param textureWidth  Sprite width
     * @param textureHeight Sprite height
     */
    public GuiTextureLoader(ResourceLocation atlasTexture) {
        this.atlasTexture = atlasTexture;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        IResource iresource = null;

        try {
            iresource = resourceManager.getResource(this.atlasTexture);
            BufferedImage bufferedImage = TextureUtil.readBufferedImage(iresource.getInputStream());
            boolean flag = false;
            boolean flag1 = false;

            if (iresource.hasMetadata()) {
                try {
                    TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                    if (texturemetadatasection != null) {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }
                } catch (RuntimeException runtimeexception) {
                    ACsGuiApi.log.warn("Failed reading metadata of: {}", this.atlasTexture, runtimeexception);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, flag, flag1);
            atlasWidth = bufferedImage.getWidth();
            atlasHeight = bufferedImage.getHeight();
        } catch (Exception e) {
            if (atlasWidth == 0 && atlasHeight == 0) {
                atlasWidth = atlasHeight = 100;
            }
            throw e;
        } finally {
            IOUtils.closeQuietly(iresource);
        }
    }

    public int getAtlasWidth() {
        return atlasWidth;
    }

    public int getAtlasHeight() {
        return atlasHeight;
    }

    @Override
    public String toString() {
        return "GuiTextureLoader{" +
                atlasTexture +
                '}';
    }
}
