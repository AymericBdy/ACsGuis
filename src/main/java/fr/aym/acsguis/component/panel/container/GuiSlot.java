package fr.aym.acsguis.component.panel.container;

import fr.aym.acsguis.api.GuiAPIClientHelper;
import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.GuiComponent;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class GuiSlot extends GuiComponent
{
    protected static final int DEFAULT_SLOT_WIDTH = 18;
    protected static final int DEFAULT_SLOT_HEIGHT = 18;
    
    protected final Slot slot;

    public GuiSlot(Slot slot) {
        this.slot = slot;
        style.setBackgroundColor(new Color(0,0,0,0).getRGB());
		//TODO THIS IS BAD
        style.getWidth().setAbsolute(DEFAULT_SLOT_WIDTH);
        style.getHeight().setAbsolute(DEFAULT_SLOT_HEIGHT);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.SLOT;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {

        super.drawForeground(mouseX, mouseY, partialTicks);

        GuiContainer container = ((GuiContainer) getParent());

        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == container.clickedSlot && container.draggedStack != null && !container.isRightMouseClick;
        ItemStack itemstack1 = mc.player.inventory.getItemStack();

        if (slot == container.clickedSlot && container.draggedStack != null && container.isRightMouseClick && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount()/2);
        }
        else if (container.dragSplitting && container.dragSplittingSlots.contains(slot) && itemstack1 != null)
        {
            if (container.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (net.minecraft.inventory.Container.canAddItemToSlot(slot, itemstack1, true) && container.inventorySlots.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                net.minecraft.inventory.Container.computeStackSize(container.dragSplittingSlots, container.dragSplittingLimit, itemstack, slot.getStack() == null ? 0 : slot.getStack().getCount());

                if (itemstack.getCount() > itemstack.getMaxStackSize())
                {
                    itemstack.setCount(itemstack.getMaxStackSize());
                }

                if (itemstack.getCount() > slot.getSlotStackLimit())
                {
                    itemstack.setCount(slot.getSlotStackLimit());
                }
            }
            else
            {
                container.dragSplittingSlots.remove(slot);
                container.updateDragSplitting();
            }
        }


        if (itemstack == null)
        {
            TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();

            if (textureatlassprite != null)
            { //MODIFIED FOR 1.12.2
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                this.drawTexturedModalRect(getScreenX(), getScreenY(), textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(getScreenX(), getScreenY(), getScreenX() + 16, getScreenY() + 16, -2130706433);
            }

            if(itemstack != null)
                GuiAPIClientHelper.drawItemStack(itemstack, getScreenX(), getScreenY());
        }

        if(isHovered() && slot.isEnabled()) { //MODIFIED FOR 1.12.2
            GlStateManager.disableDepth();
            drawRect(getScreenX(), getScreenY(), getScreenX() + 16, getScreenY() + 16, -2130706433);
            GlStateManager.enableDepth();
        }
    }

    public Slot getSlot() {
        return slot;
    }
}
