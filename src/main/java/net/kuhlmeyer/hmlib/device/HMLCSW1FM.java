package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hat.event.EventBus;
import net.kuhlmeyer.hat.job.HomematicAdapter;

public class HMLCSW1FM extends AbstractHMSwitch implements Actor, Switch {

	public void init(EventBus eventBus, String deviceId, String name, String location, HomematicAdapter hmAdapter) {
		super.init(eventBus, deviceId, name, 1, location, hmAdapter);
	}
	
	@Override
	public DeviceType getType() {
		return DeviceType.Schalter;
	}
}
