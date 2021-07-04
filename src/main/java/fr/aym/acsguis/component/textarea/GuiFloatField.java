package fr.aym.acsguis.component.textarea;

public class GuiFloatField extends GuiTextField
{
    private float value;
    private float min;
    private float max;

    public GuiFloatField(float min, float max) {
        super();
        this.min = min;
        this.max = max;
        setText("0");
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    @Override
    public GuiTextArea setText(String text) {
        if (!text.isEmpty()) {
            try {
                float color = Float.parseFloat(text.equals("-") ? text+"0" : text);
                if (color > getMax()) {
                    this.text = ""+getMax();
                } else if (color < getMin()) {
                    this.text = ""+getMin();
                } else {
                    this.text = text;
                }
                value = Float.parseFloat(text);
            } catch (NumberFormatException ignored) {}
        } else {
            this.text = text;
            value = 0;
        }
        return this;
    }

    public void setValue(float value) {
        this.value = value;
        this.setText(""+value);
    }

    public float getValue() {
        return value;
    }
}
