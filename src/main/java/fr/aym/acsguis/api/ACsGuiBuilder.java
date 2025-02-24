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
import fr.aym.acsguis.component.panel.GuiTabbedPane;
import fr.aym.acsguis.component.textarea.*;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Stack;

/**
 * Build you guis faster!
 *
 * <ul>
 * <li> ACsGuiBuilder builder = ACsGuiBuilder.begin(this); </li>
 * <li> builder.anything(things, cssId, cssClasses...); </li>
 * <li> builder.end(); </li>
 * </ul>
 */
public class ACsGuiBuilder {
    private final GuiFrame buildingGui;
    private final Stack<GuiPanel> panelStack = new Stack<>();

    private ACsGuiBuilder(GuiFrame buildingGui) {
        this.buildingGui = buildingGui;
        panelStack.push(buildingGui);
    }

    public static ACsGuiBuilder begin(GuiFrame buildingGui) {
        assert buildingGui != null : "Cannot begin with no gui";
        return new ACsGuiBuilder(buildingGui);
    }

    public GuiFrame end() {
        panelStack.pop();
        if (!panelStack.isEmpty()) {
            throw new IllegalStateException("You didn't end all of the panels!");
        }
        return buildingGui;
    }

    public GuiPanel pane() {
        GuiPanel panel = new GuiPanel();
        panelStack.peek().add(panel);
        panelStack.push(panel);
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
        panelStack.peek().add(panel);
        panelStack.push(panel);
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
        if (panelStack.isEmpty()) {
            throw new IllegalStateException("No panel to end");
        }
        panelStack.pop();
    }

    public GuiLabel label(String text) {
        GuiLabel label = new GuiLabel(text);
        panelStack.peek().add(label);
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
        panelStack.peek().add(label);
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
        panelStack.peek().add(textArea);
        return textArea;
    }

    public GuiTextArea textArea(String text) {
        GuiTextArea textArea = new GuiTextArea(text);
        panelStack.peek().add(textArea);
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
        panelStack.peek().add(textArea);
        return textArea;
    }

    public GuiTextField textField(String text) {
        GuiTextField textArea = new GuiTextField(text);
        panelStack.peek().add(textArea);
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
        panelStack.peek().add(textArea);
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
        panelStack.peek().add(textArea);
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
        panelStack.peek().add(textArea);
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
        panelStack.peek().add(label);
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
        panelStack.peek().add(label);
        return label;
    }

    public GuiButtonWithItem buttonWithItem(ItemStack icon, IMouseClickListener clickListener, String cssId) {
        return (GuiButtonWithItem) buttonWithItem(icon, clickListener).setCssId(cssId);
    }

    public GuiButtonWithItem buttonWithItem(ItemStack icon, IMouseClickListener clickListener, @Nullable String cssId, String... cssClass) {
        return (GuiButtonWithItem) buttonWithItem(icon, clickListener, cssId).setCssClasses(cssClass);
    }

    public GuiCheckBox checkbox(String text, IMouseClickListener clickListener) {
        GuiCheckBox label = new GuiCheckBox(text);
        label.addClickListener(clickListener);
        panelStack.peek().add(label);
        return label;
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
        panelStack.peek().add(slider);
        return slider;
    }

    public GuiSlider slider(boolean horizontal, float min, float max, float step, float value, String cssId) {
        return (GuiSlider) slider(horizontal, min, max, step, value).setCssId(cssId);
    }

    public GuiSlider slider(boolean horizontal, float min, float max, float step, float value, String cssId, String... cssClass) {
        return (GuiSlider) slider(horizontal, min, max, step, value, cssId).setCssClasses(cssClass);
    }

    public GuiEntityRender entityRender(EntityLivingBase entity) {
        GuiEntityRender label = new GuiEntityRender(entity);
        panelStack.peek().add(label);
        return label;
    }

    public GuiEntityRender entityRender(EntityLivingBase entity, String cssId) {
        return (GuiEntityRender) entityRender(entity).setCssId(cssId);
    }

    public GuiEntityRender entityRender(EntityLivingBase entity, String cssId, String... cssClass) {
        return (GuiEntityRender) entityRender(entity, cssId).setCssClasses(cssClass);
    }

    public GuiTabbedPane tabbedPane() {
        GuiTabbedPane pane = new GuiTabbedPane();
        panelStack.peek().add(pane);
        panelStack.push(pane);
        return pane;
    }

    public GuiTabbedPane tabbedPane(String cssId) {
        return (GuiTabbedPane) tabbedPane().setCssId(cssId);
    }

    public GuiTabbedPane tabbedPane(String cssId, String... cssClass) {
        return (GuiTabbedPane) tabbedPane(cssId).setCssClasses(cssClass);
    }

    public ACsGuiBuilder nextTab(String name) {
        GuiTabbedPane.nextTabName = name;
        return this;
    }

    public GuiKeyLabel keybindingLabel(int keyCode) {
        GuiKeyLabel label = new GuiKeyLabel(keyCode);
        panelStack.peek().add(label);
        return label;
    }

    public GuiKeyLabel keybindingLabel(int keyCode, String cssId) {
        return (GuiKeyLabel) keybindingLabel(keyCode).setCssId(cssId);
    }

    public GuiKeyLabel keybindingLabel(int keyCode, String cssId, String... cssClass) {
        return (GuiKeyLabel) keybindingLabel(keyCode, cssId).setCssClasses(cssClass);
    }

    public GuiProgressBar progressBar(boolean horizontal, int min, int max, float progress) {
        GuiProgressBar slider = new GuiProgressBar(horizontal);
        slider.setMin(min);
        slider.setMax(max);
        slider.setProgress(progress);
        panelStack.peek().add(slider);
        return slider;
    }

    public GuiProgressBar progressBar(boolean horizontal, int min, int max, float progress, String cssId) {
        return (GuiProgressBar) progressBar(horizontal, min, max, progress).setCssId(cssId);
    }

    public GuiProgressBar progressBar(boolean horizontal, int min, int max, float progress, String cssId, String... cssClass) {
        return (GuiProgressBar) progressBar(horizontal, min, max, progress, cssId).setCssClasses(cssClass);
    }

    // TODO DROPDOWN LIST
    // TODO LIST
    // TODO SLOT LIST
    // TODO COMBO BOX
    // TODO SEARCH FIELD
}
