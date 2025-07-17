package fr.aym.acsguis.component.list;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.layout.GridLayout;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.style.InternalComponentStyle;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.cssengine.positionning.Size;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import fr.aym.acsguis.utils.GuiConstants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class GuiDropdownListSkeleton<C extends GuiComponent> extends GuiPanel implements IMouseClickListener {
    private final C component;
    private final GuiPanel panel;

    protected List<String> options;

    protected String selectedElement;
    @Nullable
    protected Consumer<String> changeCallback;

    public GuiDropdownListSkeleton(C component) {
        this.component = component;
        add(component);

        panel = new GuiScrollPane();
        panel.setLayout(new GridLayout(new Size.SizeValue(1, GuiConstants.ENUM_SIZE.RELATIVE), new Size.SizeValue(20, GuiConstants.ENUM_SIZE.ABSOLUTE), new Size.SizeValue(1, GuiConstants.ENUM_SIZE.ABSOLUTE), GridLayout.GridDirection.HORIZONTAL, 1));
        setPanelVisible(false);
        add(panel);
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;

        panel.removeAllChildren();
        for (String s : options) {
            panel.add(new GuiLabel(s).addClickListener((mouseX, mouseY, mouseButton) -> {
                setSelectedElement(s);
                setPanelVisible(false);
                if (changeCallback != null) {
                    changeCallback.accept(s);
                }
            }));
        }
    }

    public GuiDropdownListSkeleton<C> setChangeCallback(@Nullable Consumer<String> changeCallback) {
        this.changeCallback = changeCallback;
        return this;
    }

    @Nullable
    public Consumer<String> getChangeCallback() {
        return changeCallback;
    }

    public C getComponent() {
        return component;
    }

    public GuiPanel getPanel() {
        return panel;
    }

    public boolean isPanelVisible() {
        return panel.isVisible();
    }

    public void setPanelVisible(boolean visible) {
        ((InternalComponentStyle) panel.getStyle()).setVisible(visible);
    }

    public void setSelectedElement(String selectedElement) {
        this.selectedElement = selectedElement;
    }

    public String getSelectedElement() {
        return selectedElement;
    }

    public void closeDropdown() {
        ((InternalComponentStyle) panel.getStyle()).setVisible(false);
    }

    @Override
    public float getRenderMaxY() {
        return super.getRenderMaxY() + (panel.isVisible() ? panel.getHeight() : 0);
    }

    // Listen to mouse events on parent to hide the dropdown when clicking outside of it
    @Override
    public GuiComponent setParent(GuiPanel parent) {
        if (this.parent != null && parent != this.parent) {
            this.parent.getClickListeners().remove(this);
        }
        if (parent != null && parent != this.parent) {
            parent.addClickListener(this);
        }
        return super.setParent(parent);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isMouseOver(mouseX, mouseY)) {
            closeDropdown();
        }
    }
}
