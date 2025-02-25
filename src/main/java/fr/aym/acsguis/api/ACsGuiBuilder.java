package fr.aym.acsguis.api;

import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiButtonWithItem;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.button.GuiSlider;
import fr.aym.acsguis.component.entity.GuiEntityRender;
import fr.aym.acsguis.component.layout.PanelLayout;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.*;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Stack;

public class ACsGuiBuilder {
    private final GuiFrame buildingGui;
    private final Stack<GuiPanel> panelQueue = new Stack<>();

    private ACsGuiBuilder(GuiFrame buildingGui) {
        this.buildingGui = buildingGui;
        panelQueue.push(buildingGui);
    }

    public static ACsGuiBuilder beginGui(GuiFrame buildingGui) {
        assert buildingGui != null : "Cannot begin with no gui";
        return new ACsGuiBuilder(buildingGui);
    }

    public GuiFrame endGui() {
        panelQueue.pop();
        if (!panelQueue.isEmpty()) {
            throw new IllegalStateException("You didn't end all of the panels!");
        }
        return buildingGui;
    }

    public GuiPanel pane() {
        GuiPanel panel = new GuiPanel();
        panelQueue.peek().add(panel);
        panelQueue.push(panel);
        return panel;
    }

    public GuiPanel pane(String cssId) {
        return (GuiPanel) pane().setCssId(cssId);
    }

    public GuiPanel pane(@Nullable String cssId, String... cssClass) {
        return (GuiPanel) pane(cssId).setCssClasses(cssClass);
    }

    public GuiPanel pane(PanelLayout<?> layout) {
        return pane().setLayout(layout);
    }

    public GuiPanel pane(PanelLayout<?> layout, String cssId) {
        return (GuiPanel) pane(layout).setCssId(cssId);
    }

    public GuiPanel pane(PanelLayout<?> layout, @Nullable String cssId, String... cssClass) {
        return (GuiPanel) pane(layout, cssId).setCssClasses(cssClass);
    }

    public GuiScrollPane scrollPane() {
        GuiScrollPane panel = new GuiScrollPane();
        panelQueue.peek().add(panel);
        panelQueue.push(panel);
        return panel;
    }

    public GuiScrollPane scrollPane(String cssId) {
        return (GuiScrollPane) scrollPane().setCssId(cssId);
    }

    public GuiScrollPane scrollPane(@Nullable String cssId, String... cssClass) {
        return (GuiScrollPane) scrollPane(cssId).setCssClasses(cssClass);
    }

    public GuiScrollPane scrollPane(PanelLayout<?> layout) {
        return (GuiScrollPane) scrollPane().setLayout(layout);
    }

    public GuiScrollPane scrollPane(PanelLayout<?> layout, String cssId) {
        return (GuiScrollPane) scrollPane(layout).setCssId(cssId);
    }

    public GuiScrollPane scrollPane(PanelLayout<?> layout, @Nullable String cssId, String... cssClass) {
        return (GuiScrollPane) scrollPane(layout, cssId).setCssClasses(cssClass);
    }

    public void endPane() {
        if (panelQueue.isEmpty()) {
            throw new IllegalStateException("No panel to end");
        }
        panelQueue.pop();
    }

    public GuiLabel label(String text) {
        GuiLabel label = new GuiLabel(text);
        panelQueue.peek().add(label);
        return label;
    }

    public GuiLabel label(String text, String cssId) {
        return (GuiLabel) label(text).setCssId(cssId);
    }

    public GuiLabel label(String text, @Nullable String cssId, String... cssClass) {
        return (GuiLabel) label(text, cssId).setCssClasses(cssClass);
    }

    public GuiLabel clickableLabel(String text, IMouseClickListener clickListener) {
        return (GuiLabel) label(text).addClickListener(clickListener);
    }

    public GuiLabel clickableLabel(String text, IMouseClickListener clickListener, String cssId) {
        return (GuiLabel) clickableLabel(text, clickListener).setCssId(cssId);
    }

    public GuiLabel clickableLabel(String text, IMouseClickListener clickListener, @Nullable String cssId, String... cssClass) {
        return (GuiLabel) clickableLabel(text, clickListener, cssId).setCssClasses(cssClass);
    }

    public UpdatableGuiLabel updatableLabel(String text, UpdatableGuiLabel.LabelValueFunction valueFunction) {
        UpdatableGuiLabel label = new UpdatableGuiLabel(text, valueFunction);
        panelQueue.peek().add(label);
        return label;
    }

    public UpdatableGuiLabel updatableLabel(String text, UpdatableGuiLabel.LabelValueFunction valueFunction, String cssId) {
        return (UpdatableGuiLabel) updatableLabel(text, valueFunction).setCssId(cssId);
    }

    public UpdatableGuiLabel updatableLabel(String text, UpdatableGuiLabel.LabelValueFunction valueFunction, @Nullable String cssId, String... cssClass) {
        return (UpdatableGuiLabel) updatableLabel(text, valueFunction, cssId).setCssClasses(cssClass);
    }

    public GuiTextArea textArea() {
        GuiTextArea textArea = new GuiTextArea();
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiTextArea textArea(String text) {
        GuiTextArea textArea = new GuiTextArea(text);
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiTextArea textArea(String text, String cssId) {
        return (GuiTextArea) textArea(text).setCssId(cssId);
    }

    public GuiTextArea textArea(String text, @Nullable String cssId, String... cssClass) {
        return (GuiTextArea) textArea(text, cssId).setCssClasses(cssClass);
    }

    public GuiTextField textField() {
        GuiTextField textArea = new GuiTextField();
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiTextField textField(String text) {
        GuiTextField textArea = new GuiTextField(text);
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiTextField textField(String text, String cssId) {
        return (GuiTextField) textField(text).setCssId(cssId);
    }

    public GuiTextField textField(String text, @Nullable String cssId, String... cssClass) {
        return (GuiTextField) textField(text, cssId).setCssClasses(cssClass);
    }

    public GuiPasswordField passwordField() {
        GuiPasswordField textArea = new GuiPasswordField();
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiPasswordField passwordField(String cssId) {
        return (GuiPasswordField) passwordField().setCssId(cssId);
    }

    public GuiPasswordField passwordField(@Nullable String cssId, String... cssClass) {
        return (GuiPasswordField) passwordField(cssId).setCssClasses(cssClass);
    }

    public GuiFloatField floatField(float value, float min, float max) {
        GuiFloatField textArea = new GuiFloatField(value, min, max);
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiFloatField floatField(float value, float min, float max, String cssId) {
        return (GuiFloatField) floatField(value, min, max).setCssId(cssId);
    }

    public GuiFloatField floatField(float value, float min, float max, @Nullable String cssId, String... cssClass) {
        return (GuiFloatField) floatField(value, min, max, cssId).setCssClasses(cssClass);
    }

    public GuiIntegerField integerField(int value, int min, int max) {
        GuiIntegerField textArea = new GuiIntegerField(value, min, max);
        panelQueue.peek().add(textArea);
        return textArea;
    }

    public GuiIntegerField integerField(int value, int min, int max, String cssId) {
        return (GuiIntegerField) integerField(value, min, max).setCssId(cssId);
    }

    public GuiIntegerField integerField(int value, int min, int max, @Nullable String cssId, String... cssClass) {
        return (GuiIntegerField) integerField(value, min, max, cssId).setCssClasses(cssClass);
    }

    public GuiButton button(String text, IMouseClickListener clickListener) {
        GuiButton label = new GuiButton(text);
        label.addClickListener(clickListener);
        panelQueue.peek().add(label);
        return label;
    }

    public GuiButton button(String text, IMouseClickListener clickListener, String cssId) {
        return (GuiButton) button(text, clickListener).setCssId(cssId);
    }

    public GuiButton button(String text, IMouseClickListener clickListener, @Nullable String cssId, String... cssClass) {
        return (GuiButton) button(text, clickListener, cssId).setCssClasses(cssClass);
    }

    public GuiButtonWithItem buttonWithItem(ItemStack icon, IMouseClickListener clickListener) {
        GuiButtonWithItem label = new GuiButtonWithItem(icon);
        label.addClickListener(clickListener);
        panelQueue.peek().add(label);
        return label;
    }

    public GuiButtonWithItem buttonWithItem(ItemStack icon, IMouseClickListener clickListener, String cssId) {
        return (GuiButtonWithItem) buttonWithItem(icon, clickListener).setCssId(cssId);
    }

    public GuiButtonWithItem buttonWithItem(ItemStack icon, IMouseClickListener clickListener, @Nullable String cssId, String... cssClass) {
        return (GuiButtonWithItem) buttonWithItem(icon, clickListener, cssId).setCssClasses(cssClass);
    }

    public GuiCheckBox checkbox(String text) {
        GuiCheckBox label = new GuiCheckBox(text);
        panelQueue.peek().add(label);
        return label;
    }

    public GuiCheckBox checkbox(String text, String cssId) {
        return (GuiCheckBox) checkbox(text).setCssId(cssId);
    }

    public GuiCheckBox checkbox(String text, @Nullable String cssId, String... cssClass) {
        return (GuiCheckBox) checkbox(text, cssId).setCssClasses(cssClass);
    }

    public GuiCheckBox checkbox(String text, IMouseClickListener clickListener) {
        return (GuiCheckBox) checkbox(text).addClickListener(clickListener);
    }

    public GuiCheckBox checkbox(String text, IMouseClickListener clickListener, String cssId) {
        return (GuiCheckBox) checkbox(text, clickListener).setCssId(cssId);
    }

    public GuiCheckBox checkbox(String text, IMouseClickListener clickListener, @Nullable String cssId, String... cssClass) {
        return (GuiCheckBox) checkbox(text, clickListener, cssId).setCssClasses(cssClass);
    }

    public GuiSlider slider(boolean horizontal, float min, float max, float step, float value) {
        GuiSlider slider = new GuiSlider(horizontal);
        slider.setMin(min).setMax(max).setStep(step).setValue(value);
        panelQueue.peek().add(slider);
        return slider;
    }
    // TODO ID CLASS

    public GuiEntityRender entityRender(EntityLivingBase entity) {
        GuiEntityRender label = new GuiEntityRender(entity);
        panelQueue.peek().add(label);
        return label;
    }
    // TODO ID CLASS

    // TODO DROPDOWN LIST
    // TODO LIST
    // TODO SLOT LIST
    // TODO COMBO BOX
    // TODO TABBED PANE

    // TODO KEY LABEL
    // TODO PROGRESS BAR
    // TODO SEARCH FIELD
}
