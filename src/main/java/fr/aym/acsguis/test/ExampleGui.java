package fr.aym.acsguis.test;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.aym.acsguis.api.ACsGuiBuilder;
import fr.aym.acsguis.api.ACsGuiFrame;
import fr.aym.acsguis.component.button.GuiButton;
import fr.aym.acsguis.component.button.GuiCheckBox;
import fr.aym.acsguis.component.layout.GuiScaler;
import fr.aym.acsguis.component.panel.GuiFrame;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.event.listeners.mouse.IMouseClickListener;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ACsGuiFrame
public class ExampleGui extends GuiFrame {
    @ACsGuiFrame.RegisteredStyleSheet
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ACsGuiApi.RES_LOC_ID, "css/example_gui.css");

    public ExampleGui() {
        super(new GuiScaler.Identity());
        setCssId("root");
        setNeedsCssReload(true);
        setEnableDebugPanel(true);

        ACsGuiBuilder builder = ACsGuiBuilder.begin(this);
        GuiScrollPane contentPane = builder.scrollPane("content");

        /*GuiScrollPane contentPane = new GuiScrollPane();
        //contentPane.setLayout(new FlowLayout());
        contentPane.setCssId("content");*/
        contentPane.addClickListener(new IMouseClickListener() {
            @Override
            public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
                contentPane.getLayout().clear();
            }
        });

        GuiCheckBox box = builder.checkbox("CLIQUES SUR MOI STPPPP STPPPPPPPP STPPPPPPPPPPPP", (x, y, b) -> {
            System.out.println("Lol");
        });
        AtomicBoolean active = new AtomicBoolean();
        box.setCssClass("switch-button-chk-" + (active.get() ? "active" : "inactive")).addClickListener((mx, my, button) -> {
            if (!box.isChecked())
                active.set(false);
            else
                active.set(true);
            box.setCssClass("switch-button-chk-" + (active.get() ? "active" : "inactive"));
        });

        GuiLabel lab;
        for (int i = 0; i < 8; i++) {
            //lab = new GuiLabel("This is the " + i + "th label");
            //lab.setCssClasses("block_labels");
            //  contentPane.add(lab);
            builder.label("This is the " + i + "th label", null, "block_labels");
        }
        for (int i = 0; i < 12; i++) {
            //lab = new GuiLabel("This is the " + i + "th label");
            //lab.setCssClasses("inline_labels");
            // contentPane.add(lab);
            builder.label("This is the " + i + "th label", null, "inline_labels", "inline_labels2", "inline_labels_dumb");
        }
        for (int i = 0; i < 4; i++) {
            StringBuilder hack = new StringBuilder();
            for (int j = 0; j < i; j++) {
                hack.append(j);
            }
            //lab = new GuiLabel("This is the " + i + "th label: " + hack);
            //lab.setCssClasses("block_labels2");
            //contentPane.add(lab);
            builder.label("This is the " + i + "th label: " + hack, null, "block_labels2");
        }
        for (int i = 0; i < 13; i++) {
            StringBuilder hack = new StringBuilder();
            for (int j = 0; j < i; j++) {
                hack.append(j);
            }
            //lab = new GuiLabel("This is the " + i + "th label: " + hack);
            //lab.setCssClasses("inline_labels2");
            //contentPane.add(lab);
            builder.label("This is the " + i + "th label: " + hack, null, "inline_labels2");
        }
        GuiButton buttons;
        for (int i = 0; i < 8; i++) {
            //buttons = new GuiButton("This is the " + i + "th button");
            //buttons.setCssClasses("inline_buttons");
            //contentPane.add(buttons);
            builder.button("This is the " + i + "th button", (x, y, b) -> {}, null, "inline_buttons");
        }

        builder.endPane();
        builder.end();
        /*GuiButtonWithItem buttonWithItem = new GuiButtonWithItem( new ItemStack(Items.DIAMOND));
        buttonWithItem.setCssClass("inline_buttons");
        contentPane.add(buttonWithItem);
        GuiTextField field = new GuiTextField("This is a text field");
        field.setCssClass("input_field");
        contentPane.add(field);
        field = new GuiTextField("This is a text field with a regex");
        field.setCssClass("input_field");
        field.setRegex("[0-9]*");
        contentPane.add(field);
        field = new GuiIntegerField(0, 28, 10);
        field.setCssClass("input_field");
        contentPane.add(field);
        field = new GuiFloatField(-5.5f, 5.5f, -0.4f);
        field.setCssClass("input_field");
        contentPane.add(field);
        GuiKeyLabel keyLabel = new GuiKeyLabel(Keyboard.KEY_J);
        keyLabel.setCssClass("key_label");
        contentPane.add(keyLabel);
        GuiTextArea area = new GuiTextArea("This is a text area");
        area.setCssClass("input_area");
        contentPane.add(area);
        GuiPanel panel = new GuiPanel();
        panel.setCssId("big_panel");
        contentPane.add(panel);
        lab = new GuiLabel("Here is your player view");
        lab.setCssClass("player_view");
        panel.add(lab);
        GuiEntityRender render = new GuiEntityRender(mc.player);
        render.setCssClass("player_render");
        panel.add(render);
        //TODO GuiCameraView camera = new GuiCameraView(mc.player);
        GuiCheckBox checkBox = new GuiCheckBox("This is a check box");
        checkBox.setCssClass("check_box");
        contentPane.add(checkBox);
        GuiSlider slider = new GuiSlider(true);
        slider.setCssClass("slider_horizontal");
        contentPane.add(slider);
        //TODO GuiDropdownList
        //TODO GuiList
        //TODO GuiSlotList
        //TODO GuiSearchField
        GuiComboBox comboBox = new GuiComboBox("Choose your Player", Arrays.asList("Player 1", "Player 2", "Player 3"));
        comboBox.setCssClass("combo_box");
        contentPane.add(comboBox);
        //TODO GuiTabbedPane
        GuiProgressBar progressBar = new GuiProgressBar().setProgress(50);
        progressBar.setCssClass("progress_bar");
        contentPane.add(progressBar);*/

        //add(contentPane);

        setNeedsCssReload(true);
    }

    @Override
    public List<ResourceLocation> getCssStyles() {
        return Arrays.asList(RESOURCE_LOCATION);
    }
}
