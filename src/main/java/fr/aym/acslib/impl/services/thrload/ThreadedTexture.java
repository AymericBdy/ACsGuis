package fr.aym.acslib.impl.services.thrload;

import fr.aym.acsguis.api.ACsGuiApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ThreadedTexture extends AbstractTexture
{
    protected final ResourceLocation textureLocation;

    protected BufferedImage imageData;
    protected boolean textureBlur, textureClamp;

    public ThreadedTexture(ResourceLocation textureResourceLocation)
    {
        this.textureLocation = textureResourceLocation;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        IResource iresource = null;
        try
        {
            iresource = resourceManager.getResource(this.textureLocation);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            textureBlur = false;
            textureClamp = false;

            if (iresource.hasMetadata())
            {
                try
                {
                    TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

                    if (texturemetadatasection != null)
                    {
                        textureBlur = texturemetadatasection.getTextureBlur();
                        textureClamp = texturemetadatasection.getTextureClamp();
                    }
                }
                catch (RuntimeException runtimeexception)
                {
                    ACsGuiApi.log.warn("Failed reading metadata of: {}", this.textureLocation, runtimeexception);
                }
            }
            imageData = bufferedimage;
            if(Minecraft.getMinecraft().isCallingFromMinecraftThread())
                uploadTexture();
        }
        finally
        {
            IOUtils.closeQuietly(iresource);
        }
    }

    @Override
    public int getGlTextureId() {
        if(imageData != null)
            throw new IllegalStateException("Image data of "+textureLocation+" has not been uploaded !");
        return super.getGlTextureId();
    }

    @Deprecated
    public void uploadTexture(TextureManager resourceManager) {
        uploadTexture();
    }

    public void uploadTexture()
    {
        if(imageData != null) {
            TextureUtil.uploadTextureImageAllocate(super.getGlTextureId(), imageData, textureBlur, textureClamp); //take care of the super
            imageData = null;
        }
        //else already uploaded (or errored)
    }
}