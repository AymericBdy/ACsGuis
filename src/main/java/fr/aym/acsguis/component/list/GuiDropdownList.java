package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiConstants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GuiDropdownList extends GuiPanel
{
    private final GuiButton button;
    private final GuiPanel panel;
    private String selectedElement;

    public GuiDropdownList(String label, List<String> elements, @Nullable Consumer<String> changeCallback)
    {
        super();
        panel = new GuiScrollPane();
        add((button = new GuiButton(label)).addClickListener((mouseX, mouseY, mouseButton) -> panel.setVisible(!panel.isVisible())));

        panel.setLayout(new GridLayout(new Size.SizeValue(1, GuiConstants.ENUM_SIZE.RELATIVE), new Size.SizeValue(20, GuiConstants.ENUM_SIZE.ABSOLUTE), new Size.SizeValue(1, GuiConstants.ENUM_SIZE.ABSOLUTE), GridLayout.GridDirection.HORIZONTAL, 1));
        for(String s : elements)
        {
            panel.add(new GuiLabel(s).addClickListener((mouseX, mouseY, mouseButton) -> {
                selectedElement = s;
                panel.setVisible(false);
                if (changeCallback != null)
                    changeCallback.accept(s);
            }));
        }
        panel.setVisible(false);
        add(panel);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.DROPDOWN_LIST;
    }

    public GuiButton getButton()
    {
        return button;
    }

    public GuiPanel getPanel()
    {
        return panel;
    }

    public String getSelectedElement()
    {
        return selectedElement;
    }

    @Override
    public int getRenderMaxY() {
        return super.getRenderMaxY() + (panel.isVisible() ? panel.getHeight() : 0);
    }

    @Override
	public boolean isMouseOver(int mouseX, int mouseY)
	{
		return mouseX >= getScreenX() && mouseX < getScreenX() + getWidth() && mouseY >= getScreenY() && mouseY < getScreenY() + getHeight() + (panel.isVisible() ? panel.getHeight() : 0);
	}
}
