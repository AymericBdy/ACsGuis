package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.button.GuiButton;

import java.util.List;

public class GuiDropdownList extends GuiDropdownListSkeleton<GuiButton> {
    private final boolean updateLabelOnClick;

    public GuiDropdownList(String label, List<String> options) {
        this(label, options, true);
    }

    public GuiDropdownList(String label, List<String> options, boolean updateLabelOnClick) {
        super(new GuiButton(label));
        this.updateLabelOnClick = updateLabelOnClick;

        getComponent().addClickListener((mouseX, mouseY, mouseButton) -> setPanelVisible(!isPanelVisible()));

        setOptions(options);
    }

    @Override
    public void setSelectedElement(String selectedElement) {
        super.setSelectedElement(selectedElement);
        if (updateLabelOnClick) {
            setLabel(selectedElement);
        }
    }

    public void setLabel(String label) {
        getComponent().setText(label);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.DROPDOWN_LIST;
    }
}
