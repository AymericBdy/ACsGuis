package fr.aym.acsguis.utils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;

/**
 * Provides helper methods to draw stylized backgrounds
 */
public class CircleBackground
{
    /**
     * Renders borders with the given radius and position <br>
     *     If radius != 0 then then the whole background is rendered, ignoring borderSize <br>
     *     If radius == 0, only the borders are rendered, respecting the borderSize
     */
    public static void renderBorder(int radius, int left, int top, int right, int bottom, int borderSize, int borderColor)
    {
        if(radius != 0) {
            //Draw disks
            renderBackground(radius, left, top, right, bottom, borderColor);
        }
        else
        {
            Gui.drawRect(left, top, right, top + borderSize, borderColor);
            Gui.drawRect(left, bottom - borderSize, right, bottom, borderColor);
            Gui.drawRect(left, top + borderSize, left + borderSize, bottom - borderSize, borderColor);
            Gui.drawRect(right - borderSize, top + borderSize, right, bottom - borderSize, borderColor);
        }
    }

    /**
     * Renders a background with the given circle radius at each corner
     */
    public static void renderBackground(int radius, int left, int top, int right, int bottom, int color)
    {
        if(radius != 0) {
            //Draw disks
            drawDisk(left + radius, top + radius, radius, color, 0, (float) (-Math.PI), (float) (-Math.PI/2));
            drawDisk(right - radius, top + radius, radius, color, 0, (float) (Math.PI/2), (float) Math.PI);
            drawDisk(right - radius, bottom - radius, radius, color, 0, 0, (float) (Math.PI/2));
            drawDisk(left + radius, bottom - radius, radius, color, 0, (float) (-Math.PI/2), 0);

            Gui.drawRect(left, top + radius, left + radius, bottom - radius, color);
            Gui.drawRect(left + radius, top, right - radius, bottom, color);
            Gui.drawRect(right - radius, top + radius, right, bottom - radius, color);
        }
        else
            Gui.drawRect(left, top, right, bottom, color);
    }

    /**
     * Draws a part of a disk if lineWidth == 0, or a part of a circle
     * @param x centerX
     * @param y centerY
     * @param lineWidth circle border width, if lineWidth == 0, a disk is drawn
     * @param start angle start (in radians)
     * @param end angle end (in radians)
     */
    public static void drawDisk(double x, double y, double radius, int color, int lineWidth, float start, float end) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int i;
        int triangleAmount = 50; //# of triangles used to draw circle

        //In radians
        float twicePi = end-start;

        if (lineWidth == 0) {
            GlStateManager.glBegin(GL_TRIANGLE_FAN);
        }
        else {
            glEnable(GL_LINE_SMOOTH);
            GlStateManager.glLineWidth(lineWidth*3);
            GlStateManager.glBegin(GL_LINE_LOOP);
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GlStateManager.color(f, f1, f2, f3);

        if(lineWidth == 0)
            GlStateManager.glVertex3f((float)x, (float)y, 0); // center of circle
        for (i = 0; i <= triangleAmount; i++) {
            GlStateManager.glVertex3f(
                    (float) (x + (radius * Math.sin(start+(float)i * twicePi / (float)triangleAmount))),
                    (float) (y + (radius * Math.cos(start+(float)i * twicePi / (float)triangleAmount))), 0
            );
        }
        GlStateManager.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
