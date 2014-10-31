package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hat.event.Event;
import net.kuhlmeyer.hat.event.EventBus;
import net.kuhlmeyer.hat.event.EventType;
import net.kuhlmeyer.hat.hm.HMEvent;
import net.kuhlmeyer.hat.hm.HMResponse;
import net.kuhlmeyer.hat.job.HomematicAdapter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


public class HMPB4WM extends HMDevice implements Detector {

	private static final Logger LOG = Logger.getLogger(HMPB4WM.class);
	private long stateChangeDate = 0;
	@Autowired private EventBus eventBus;
	@Autowired private HomematicAdapter hmAdapter;
	private int channel;

	
	public int getChannel() {
		return channel;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}

	
	@PostConstruct
	public void init() {
		hmAdapter.registerHMDevice(this);
	}

	
	@Override
	public DeviceType getType() {
		return DeviceType.Schalter;
	}
	
	public long getStateChangeDate() {
		return stateChangeDate;
	}
	
	public boolean wasButtonPressed() {
		return System.currentTimeMillis() - stateChangeDate < 1*60*1000;
	}

    @Override
    public boolean eventReceived(HMEvent data) {
		if(data.getPayload().length() == 22) {
			String buttonStr = data.getPayload().substring(18, 20);
			int pressedButton = Integer.parseInt(buttonStr);
			if(pressedButton == channel) {
				stateChangeDate = System.currentTimeMillis();
				LOG.info("Taste '" + pressedButton + "' an Schalter " + getName() + " gedrückt");
				eventBus.fireEvent(new Event(EventType.ButtonPressed, new Object[]{getId(), pressedButton}), "Taste '" + pressedButton + "' an Schalter " + getName() + " gedrückt", false);
			}
		}		
		return true;
    }

    @Override
    public boolean responseReceived(HMResponse data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    

	@Override
	public boolean isDetected() {
		return wasButtonPressed();
	}
}