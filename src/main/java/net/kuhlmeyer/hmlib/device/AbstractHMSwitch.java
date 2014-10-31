package net.kuhlmeyer.hmlib.device;


import net.kuhlmeyer.hmlib.HMLanAdapter;
import net.kuhlmeyer.hmlib.event.SwitchChangedEvent;
import net.kuhlmeyer.hmlib.pojo.HMEvent;
import net.kuhlmeyer.hmlib.pojo.HMResponse;
import org.apache.log4j.Logger;

/*
 * Abstract class for all homematic switches
 *
 * REQUEST:
 *  %02XA011%s%s%s%02X%s%s%s			=> Owner, DeviceId, Channel
 *  ++  A011%s%s02%sC80000  			=> Id, Dest Channel
 *
 *      ON: ++A011%s%s02%sC80000
 *     OFF: ++A011%s%s02%s000000
 *   STATE: ++A001%s%s%s0E			=> Owner, DeviceId, Channel
 *  TOGGLE: ++A03E%s%s%s40%s%02X
 *
 * RESPONSE:
 *  Example Response Off (w/o space) => 01 8002 16B341 123ABC 0101C8004C
 *  Example Response On  (w/o space) => 02 8002 16B341 123ABC 010100004B
 *
 *  00800216B341123ABC0101C8004C
 */
public abstract class AbstractHMSwitch extends HMDevice {

    private static final Logger LOG = Logger.getLogger(AbstractHMSwitch.class);
	
	private HMLanAdapter hmAdapter;

	private int channel;
    private State state = State.Unknown;
    
	public void init(String deviceId, String name, int channel, HMLanAdapter hmAdapter) {
		setId(deviceId);
		setName(name);
		this.hmAdapter = hmAdapter;
		this.channel = channel;
        this.state = State.Unknown;
	}
	
	public int getChannel() {
		return channel;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}


    protected boolean sendCommand(String command) {

        State oldState = getState();

        hmAdapter.sendCommand(command);

        for(int i = 0; i < 30; i++) {
            try {
                Thread.sleep(50);
                if(oldState != getState()) {
                    LOG.debug(String.format("Old-State=[%s], State=[%s]", oldState, getState()));
                    return true;
                }
            } catch (InterruptedException e) {
                // Intentionally left blank.
            }
        }

        return false;
    }

    public State getState() {
        return state;
    }



    public State queryState() {
        sendCommand(String.format("A001%s%s%02X0E", hmAdapter.getStatus().getOwner(), getHmId(), channel));
    }

	public State switchOn() {
        sendCommand(String.format("A011%s%s%s%02X%s%s%s", hmAdapter.getStatus().getOwner(), getHmId(), "02" /* command */, channel, "C8" /* value */, "00" /* ramp time */, "00" /* on time */));
        return getState();
    }
	
	public State switchOff() {
        sendCommand(String.format("A011%s%s%s%02X%s%s%s", hmAdapter.getStatus().getOwner(), getHmId(), "02" /* command */, channel, "00" /* value */, "00" /* ramp time */, "00" /* on time */));
        return getState();
	}

    public State trigger() {
        if(getState() == null) {
            switchOff();
            return getState();
        }

        switch(getState()) {
            case Unknown:
                switchOff();
                break;
            case On:
                switchOff();
                break;
            case Off:
                switchOn();
                break;
        }
        return getState();
    }

    public boolean eventReceived(HMEvent data) {
        return handleData(data.getStatus(), data.getPayload());
    }

    public boolean responseReceived(HMResponse data) {
    	return handleData(data.getStatus(), data.getPayload());
    }    

    private boolean handleData(String msgState, String payload) {
        String channelStr = payload.substring(20, 22);
        String value = payload.substring(22, 24);
        
        if(this.channel == Integer.parseInt(channelStr)) {
            if("0008".equals(msgState) || "0081".equals(msgState)) {
                // Status - 1-ack,8=nack,21=?,02=? 81=open
                state = State.Unknown;
                return true;
            }

            LOG.debug(String.format("Event for '%s' received. State: %s, Payload: %s", getName(), msgState, payload));

            State oldState = state;
            if("C8".equals(value)) {
                state = State.On;
                if(oldState == null || !oldState.equals(state)) {
                    LOG.debug(String.format("%s on.", getName()));
                    hmAdapter.fireEvent(new SwitchChangedEvent(this));
                }
            } else if("00".equals(value)) {
                state = State.Off;
                if(oldState == null || !oldState.equals(state)) {
                    LOG.debug(String.format("%s off.", getName()));
                    hmAdapter.fireEvent(new SwitchChangedEvent(this));
                }
            } else {
                state = State.Unknown;
            }            

            return true;
        }   
        

        return false;
    }
}
