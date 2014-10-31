package net.kuhlmeyer.hmlib.event;

import net.kuhlmeyer.hmlib.device.HMTCITWMWEU;
import net.kuhlmeyer.hmlib.event.HomematicEvent;

import java.util.Date;

/**
 * Created by christof on 31.10.14.
 */
public class TemperatureChangedEvent implements HomematicEvent {
    public TemperatureChangedEvent(HMTCITWMWEU hmtcitwmweu, Double lastTemperature, Double lastHumidity, Date prevLastUpdate) {
    }
}
