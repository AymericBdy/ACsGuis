package fr.aym.acsguis.component.list.slot;

import fr.aym.acsguis.component.list.GuiList;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.parsing.core.objects.CssIntValue;
import fr.aym.acsguis.utils.GuiConstants;

import java.awt.*;

import static fr.aym.acsguis.cssengine.parsing.core.objects.CssValue.Unit.RELATIVE_INT;

public class GuiBasicSlot extends GuiSlot
{
	protected GuiLabel entryNameLabel;
	
	public GuiBasicSlot(GuiList list, int entryId, String entryName) {
		super(list, entryId, entryName);

		//TODO THIS IS BAD
		style.getXPos().setRelative(0.25f, RELATIVE_INT);
		style.getYPos().setAbsolute(30 * entryId + list.getListPaddingTop());
		style.getWidth().setRelative(0.5f, RELATIVE_INT);
		style.getHeight().setAbsolute(25);

		style.setBackgroundColor(new Color(0,0,0,0.3f).getRGB());
		style.setBorderSize(new CssIntValue(1));
		style.setBorderPosition(ComponentStyleManager.BORDER_POSITION.INTERNAL);
		style.setBorderColor(new Color(206, 206, 206,255).getRGB());
		
		entryNameLabel = new GuiLabel(entryName);
		//TODO THIS IS BAD
		entryNameLabel.getStyle().getYPos().setAbsolute(8);
		entryNameLabel.getStyle().getWidth().setRelative(1, RELATIVE_INT);
		entryNameLabel.getStyle().getHeight().setAbsolute(9);
		entryNameLabel.getStyle().setHorizontalTextAlignment(GuiConstants.HORIZONTAL_TEXT_ALIGNMENT.CENTER);
		
		add(entryNameLabel);
	}
	
	@Override
	public void onFocus() {
		super.onFocus();
		style.setBackgroundColor(new Color(0,0,0,0.6f).getRGB());
	}
	
	@Override
	public void onFocusLoose() {
		style.setBackgroundColor(new Color(0,0,0,0.3f).getRGB());
	}
}
