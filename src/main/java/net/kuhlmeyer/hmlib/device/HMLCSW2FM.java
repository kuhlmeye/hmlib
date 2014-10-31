package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hat.event.EventBus;
import net.kuhlmeyer.hat.job.HomematicAdapter;

public class HMLCSW2FM extends AbstractHMSwitch implements Actor, Switch {

	public void init(EventBus eventBus, String deviceId, String name, int channel, String location, HomematicAdapter hmAdapter) {
		super.init(eventBus, deviceId, name, channel, location, hmAdapter);
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.Schalter;
	}
}
