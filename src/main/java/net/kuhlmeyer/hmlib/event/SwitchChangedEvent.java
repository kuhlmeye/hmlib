package net.kuhlmeyer.hmlib.event;

import net.kuhlmeyer.hmlib.device.AbstractHMSwitch;

public class SwitchChangedEvent {

    private AbstractHMSwitch theSwitch;

    public SwitchChangedEvent(AbstractHMSwitch theSwitch) {
        this.theSwitch = theSwitch;
    }

    public AbstractHMSwitch getSwitch() {
        return theSwitch;
    }
}
