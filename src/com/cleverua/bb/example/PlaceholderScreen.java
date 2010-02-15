package com.cleverua.bb.example;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

public class PlaceholderScreen extends MainScreen {

    private static final String SCREEN_TITLE = "Utils Testing";
    private static final String BUTTON_LABEL = "Test";

    private ButtonField testLoggerButton;

    public PlaceholderScreen() {
        super();
        initUI();
    }

    private void initUI() {
        setTitle(SCREEN_TITLE);

        // Set some top padding in order the testLoggerButton is not stuck to
        // the screen border.
        getMainManager().setPadding(10, 0, 0, 0);

        testLoggerButton = new ButtonField(BUTTON_LABEL, FIELD_HCENTER);

        testLoggerButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(Field f, int c) {
                Logger.debug(this, BUTTON_LABEL + " button has been fired!");
                Dialog.inform("Not implemented yet!");
            }
        });

        add(testLoggerButton);
    }

    protected boolean onSavePrompt() {
        return true;
    }
}