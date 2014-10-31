package net.kuhlmeyer.hmlib.event;

import net.kuhlmeyer.hmlib.device.AbstractHMSwitch;
import net.kuhlmeyer.hmlib.device.HMPB4WM;

/**
 * Created by christof on 31.10.14.
 */
public class ButtonPressedEvent implements HomematicEvent {
    private final HMPB4WM button;
    private final int pressedButton;

    public ButtonPressedEvent(HMPB4WM button, int pressedButton) {
        super();
        this.button = button;
        this.pressedButton = pressedButton;
    }

    public HMPB4WM getButton() {
        return button;
    }

    public int getPressedButton() {
        return pressedButton;
    }
}
