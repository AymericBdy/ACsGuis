package fr.aym.acsguis.component.textarea;

import org.lwjgl.input.Keyboard;

public class GuiKeyLabel extends GuiLabel {
    private int keyCode;
    private boolean editMode;

    public GuiKeyLabel(int keyCode) {
        super("");
        if(keyCode >= Keyboard.KEYBOARD_SIZE) {
            throw new IllegalArgumentException("Keycode must be less than Keyboard.KEYBOARD_SIZE");
        }
        this.keyCode = keyCode;
        setText(Keyboard.getKeyName(keyCode));
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.onMouseClicked(mouseX, mouseY, mouseButton);
        if (!editMode)
            editMode = true;
        setText(">" + getText() + "<");
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (editMode) {
            this.keyCode = keyCode;
            editMode = false;
            setText(Keyboard.getKeyName(keyCode));
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public int getKeyCode() {
        return keyCode;
    }
}
