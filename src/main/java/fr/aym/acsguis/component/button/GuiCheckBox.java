package fr.aym.acsguis.component.button;

import fr.aym.acsguis.component.EnumComponentType;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.TextComponent;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;

public class GuiCheckBox extends GuiPanel implements IMouseClickListener, TextComponent {
    protected GuiLabel checkLabel;
    protected GuiButton checkButton;
    protected boolean isChecked;
    protected String checkedSymbol = "x";

    public GuiCheckBox() {
        this("");
    }

    public GuiCheckBox(String text) {
        super();
        add(checkLabel = new GuiLabel(text));
        add(checkButton = new GuiButton());
        setText(text);
        addClickListener(this);
    }

    @Override
    public EnumComponentType getType() {
        return EnumComponentType.CHECKBOX;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        setChecked(!isChecked);
        checkButton.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public GuiCheckBox setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        if(isChecked)
            checkButton.setText(checkedSymbol);
        else
            checkButton.setText("");
        return this;
    }

    public GuiCheckBox setText(String text) {
        checkLabel.setText(text);
        return this;
    }

    public String getText() {
        return checkLabel.getText();
    }

    public GuiLabel getCheckLabel() {
        return checkLabel;
    }

    public GuiButton getCheckButton() {
        return checkButton;
    }

    public GuiCheckBox setCheckedSymbol(String checkedSymbol) {
        this.checkedSymbol = checkedSymbol;
        return this;
    }

    public String getCheckedSymbol() {
        return checkedSymbol;
    }
}
