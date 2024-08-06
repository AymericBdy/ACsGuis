package fr.aym.acsguis.api;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiTextArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class WorldGui
{
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected final UUID id;
    protected final GuiFrame gui;
    protected Vec3d pos;
    protected float rotationYaw;
    protected float rotationPitch;
    protected double width;
    protected double height;
    protected int guiWidth;
    protected int guiHeight;
    protected final AxisAlignedBB bounds;
    protected boolean canInteract;
    protected boolean renderDebug = false;
    protected RayTraceResult rayTraceResult;
    protected boolean isMouseClicked;

    protected boolean rendered;
    protected Framebuffer framebuffer;
    protected short renderIn;

    public WorldGui(GuiFrame gui, Vec3d pos, float rotationYaw, float rotationPitch, double width, double height, int guiWidth, int guiHeight, boolean canInteract) {
        this(UUID.randomUUID(), gui, pos, rotationYaw, rotationPitch, width, height, guiWidth, guiHeight, canInteract);
    }

    public WorldGui(UUID id, GuiFrame gui, Vec3d pos, float rotationYaw, float rotationPitch, double width, double height, int guiWidth, int guiHeight, boolean canInteract) {
        this.id = id;
        this.gui = gui;
        this.pos = pos;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.width = width;
        this.height = height;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.bounds = new AxisAlignedBB(-width/2, -height/2, 0, width/2, height/2, 0.1);
        this.canInteract = canInteract;
        //TODO FIXED RESOLUTION NOT DEPENDING ON SETTINGS !!!
        gui.getGuiScreen().setWorldAndResolution(mc, guiWidth*2, guiHeight*2);
        //TODO REMOVE *2 EVERYWHERE
        renderIn = 2;
    }

    protected void setFocused(boolean focused) {
        if(!focused) {
            gui.getGui().mouseReleased(-100, -100, 0);
            Keyboard.enableRepeatEvents(false);
            renderIn = 2;
        } else {
            KeyBinding.unPressAllKeys();
            Keyboard.enableRepeatEvents(true);
        }
        gui.getGui().setFocused(focused);
    }

    protected double getMouseX() {
        return (guiWidth/2f - (rayTraceResult.hitVec.x - pos.x) * guiWidth/width);
    }

    protected double getMouseY() {
        return (guiHeight/2f - (rayTraceResult.hitVec.y - pos.y) * guiHeight/height);
    }

    public void render(float partialTicks) {
        if(!rendered)
            return;
        GlStateManager.pushMatrix();
        Entity rootPlayer = Minecraft.getMinecraft().getRenderViewEntity();
        double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * partialTicks;
        double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * partialTicks;
        double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * partialTicks;
        GlStateManager.translate(-x+pos.x, -y+pos.y, -z+pos.z);
        GlStateManager.rotate(rotationYaw, 0, 1, 0);
        GlStateManager.rotate(rotationPitch, 1, 0, 0);

        renderDebug = true;
        if(renderDebug) {
            GlStateManager.pushMatrix();
        }
        //GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.translate(-width/2, -height/2, 0);
        GlStateManager.scale(width/guiWidth, height/guiHeight, 1f/16); //TODO TESTS
        /*GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.disableCull();*/

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        framebuffer.bindFramebufferTexture();
        float f = 1.0F / guiWidth;
        float f1 = 1.0F / guiHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0, 0 + guiHeight, 0.0D).tex(1, 1).endVertex();
        bufferbuilder.pos(0 + guiWidth, 0 + guiHeight, 0.0D).tex(0, 1).endVertex();
        bufferbuilder.pos(0 + guiWidth, 0, 0.0D).tex(0, 0).endVertex();
        bufferbuilder.pos(0, 0, 0.0D).tex(1, 0).endVertex();
        tessellator.draw();
        framebuffer.unbindFramebufferTexture();

        /*if(rayTraceResult != null) {
            double mouseX = getMouseX();
            double mouseY = getMouseY();
            gui.getGui().drawScreen((int)mouseX, (int)mouseY, 0, false);
        } else {
            gui.getGui().drawScreen(-100, -100, 0, false);
        }*/

        if(renderDebug) {
            GlStateManager.popMatrix();
            //render bounding box
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();

            if (rayTraceResult != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(rayTraceResult.hitVec.x - pos.x, rayTraceResult.hitVec.y - pos.y, rayTraceResult.hitVec.z - pos.z);
                RenderGlobal.drawBoundingBox(-0.05, -0.05, -0.05, 0.05, 0.05, 0.05, 1, 0, 0, 1);
                GlStateManager.popMatrix();
            }
            RenderGlobal.drawSelectionBoundingBox(bounds, 0, 0, 1, 1);
        }
        GlStateManager.popMatrix();
    }

    public void tick() {
        gui.getGui().updateScreen();

        if(!rendered || rayTraceResult != null || gui.getGui().isFocused() || renderIn > 0) {
            if(framebuffer == null) {
                framebuffer = new Framebuffer(guiWidth*2, guiHeight*2, true);
            }
            if(renderIn > 0)
                renderIn--;
            framebuffer.framebufferClear();
            framebuffer.bindFramebuffer(true);
            glMatrixMode(5889);
            glLoadIdentity();
            glOrtho(0.0D, guiWidth*2, guiHeight*2, 0.0D, 100.0D, 300.0D);
            glMatrixMode(5888);
            glLoadIdentity();
            glTranslated(0.0F, 0.0F, -200.0F);

            glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glDisable(GL_TEXTURE_2D);
            //glEnable(GL_BLEND);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //glEnable(GL_CULL_FACE);
            //glCullFace(GL_BACK);
            //glShadeModel(GL_SMOOTH);
            //glEnable(GL_DEPTH_TEST);
            //glDepthFunc(GL_LEQUAL);
            //glDisable(GL_MULTISAMPLE);
            glEnable(GL_ALPHA_TEST);
            glAlphaFunc(516, 0.1F);
            //glDisable(32826);
            /*glEnable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);*/
            //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            //glAlphaFunc(GL_GREATER, 0.1f);
            glDisable(GL_LIGHTING);
            glDisable(GL_LIGHT0);
            glDisable(GL_LIGHT1);
            glDisable(GL_COLOR_MATERIAL);
            //glTranslated(-guiWidth, -guiHeight, 0);
            glScaled(2, 2, 2);

            // restore blending function changed by RenderGlobal.preRenderDamagedBlocks
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            glShadeModel(7424);
            glDepthMask(true);
            glEnable(GL_CULL_FACE);
            glDisable(GL_BLEND);
            glDisable(GL_FOG);

            if(rayTraceResult != null) {
                double mouseX = getMouseX();
                double mouseY = getMouseY();
                gui.getGui().drawScreen((int)mouseX, (int)mouseY, 0, false);
            } else {
                gui.getGui().drawScreen(-100, -100, 0, false);
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_L) && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                System.out.println("Saving...");
                // Capturez le contenu du framebuffer dans un tableau de pixels (ByteBuffer)
                int width = guiWidth*2; // Remplacez par la largeur de votre framebuffer
                int height = guiHeight*2; // Remplacez par la hauteur de votre framebuffer
                ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
                GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

// Convertissez le tableau de pixels en BufferedImage
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int r = buffer.get() & 0xFF;
                        int g = buffer.get() & 0xFF;
                        int b = buffer.get() & 0xFF;
                        int a = buffer.get() & 0xFF;
                        int pixel = (a << 24) | (r << 16) | (g << 8) | b;
                        image.setRGB(x, height - y - 1, pixel);
                    }
                }

// Sauvegardez l'image en tant que fichier PNG
                try {
                    File outputFile = new File("gui_de_mort.png"); // Spécifiez le chemin de votre fichier de sortie
                    ImageIO.write(image, "png", outputFile);
                    System.out.println("L'image a été sauvegardée avec succès.");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Erreur lors de la sauvegarde de l'image en tant que fichier PNG.");
                }
            }
            framebuffer.unbindFramebuffer();
            rendered = true;
        }
        //rotationYaw = (rotationYaw + 1) % 360;
        if(!canInteract)
            return;
        //handle keyboard
        if (rayTraceResult != null && gui.getGui().isFocused() && Keyboard.isCreated()) {
            while (Keyboard.next()) {
                int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                boolean flag = Keyboard.getEventKeyState();
                if (flag && i == 1) {
                    setFocused(false);
                } else if (flag) {
                    try {
                        gui.getGui().handleKeyboardInput();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        Entity rootPlayer = Minecraft.getMinecraft().getRenderViewEntity();
        Vec3d vec3d = rootPlayer.getPositionEyes(0);
        vec3d = vec3d.subtract(pos);
        vec3d = vec3d.rotateYaw(-rotationYaw * 0.017453292F);
        vec3d = vec3d.rotatePitch(rotationPitch * 0.017453292F);
        vec3d = vec3d.add(pos);
        Vec3d vec3d1 = rootPlayer.getLook(0);
        vec3d1 = vec3d1.rotateYaw(-rotationYaw * 0.017453292F);
        vec3d1 = vec3d1.rotatePitch(rotationPitch * 0.017453292F);
        float blockReachDistance = 10;
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        AxisAlignedBB boundingBox = bounds;// new AxisAlignedBB(-guiWidth/2f, -guiHeight/2f, -0.1, guiWidth/2f, guiHeight/2f, 0.1);
        Vec3d vec3d02 = vec3d.subtract(pos.x, pos.y, pos.z);
        Vec3d vec3d12 = vec3d2.subtract(pos.x, pos.y, pos.z);
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d02, vec3d12);
        raytraceresult = raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add(pos.x, pos.y, pos.z), raytraceresult.sideHit, new BlockPos(pos));

        if(raytraceresult != null) {
            //ray trace world in front of the gui
            /*Vec3d rayVector = raytraceresult.hitVec.subtract(vec3d);
            rayVector = rayVector.rotatePitch(-rotationPitch * 0.017453292F);
            rayVector = rayVector.rotateYaw(rotationYaw * 0.017453292F);
            vec3d = rootPlayer.getPositionEyes(0);
            RayTraceResult worldRayTrace = rootPlayer.getEntityWorld().rayTraceBlocks(vec3d, vec3d.add(rayVector), false, false, true);
            if(worldRayTrace != null) {
                System.out.println("Blocked");
                raytraceresult = null;
            }*/
            RayTraceResult res = mc.objectMouseOver;
            if(res != null && res.hitVec.subtract(rootPlayer.getPositionEyes(0)).lengthSquared() < raytraceresult.hitVec.subtract(vec3d).lengthSquared()) {
                System.out.println("Blocked");
                raytraceresult = null;
            }
        }
        //System.out.println(raytraceresult);
        if(rayTraceResult != null && raytraceresult == null) {
            setFocused(false);
        }
        rayTraceResult = raytraceresult;
    }

    private boolean hasTextFocus(GuiComponent<?> component) {
        if(component instanceof GuiTextArea) {
            return ((GuiTextArea) component).isEditable() && component.isPressed();
        }
        if(!(component instanceof GuiPanel)) {
            return false;
        }
        for(GuiComponent<?> c : ((GuiPanel) component).getChildComponents()) {
            if(hasTextFocus(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleMouseEvent(MouseEvent event) {
        if(rayTraceResult == null) {
            return false;
        }
        int i = (int) (getMouseX());
        int j = (int) (getMouseY());
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            /* todo support touch screen ? if (gui.getGui().mc.gameSettings.touchscreen && gui.getGui().touchValue++ > 0)
            {
                return;
            }*/
            //gui.getGui().eventButton = k;
            //gui.getGui().lastMouseEvent = Minecraft.getSystemTime();
            gui.getGui().mouseClicked(i, j, k);
            setFocused(hasTextFocus(gui));
            isMouseClicked = true;
            event.setCanceled(true);
            return true;
        }
        else if (k != -1 && isMouseClicked)
        {
            /*if (gui.getGui().mc.gameSettings.touchscreen && --gui.getGui().touchValue > 0)
            {
                return;
            }*/
            //gui.getGui().eventButton = -1;
            gui.getGui().mouseReleased(i, j, k);
            isMouseClicked = false;
            event.setCanceled(true);
            return true;
        }
        return false;
        /*else if (gui.getGui().eventButton != -1 && gui.getGui().lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - gui.getGui().lastMouseEvent;
            gui.getGui().mouseClickMove(i, j, gui.getGui().eventButton, l);
        }*/
    }

    public boolean drawHudCrossHairs(RenderGameOverlayEvent.Pre event) {
        if (rayTraceResult == null || !gui.getGui().isFocused()) {
            return false;
        }
        int l = event.getResolution().getScaledWidth();
        int i1 = event.getResolution().getScaledHeight();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().fontRenderer.drawString("I", l / 2 - 1, i1 / 2 - 3, 0xFFFFFF);
        event.setCanceled(true);
        return true;
    }

    public UUID getId() {
        return id;
    }

    public GuiFrame getGui() {
        return gui;
    }

    public void setPosition(Vec3d pos) {
        this.pos = pos;
    }

    public Vec3d getPosition() {
        return pos;
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public void setRotationPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public boolean isCanInteract() {
        return canInteract;
    }
}
