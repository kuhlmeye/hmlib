package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hat.event.Event;
import net.kuhlmeyer.hat.event.EventBus;
import net.kuhlmeyer.hat.event.EventType;
import net.kuhlmeyer.hat.hm.HMEvent;
import net.kuhlmeyer.hat.hm.HMResponse;
import net.kuhlmeyer.hat.job.HomematicAdapter;
import net.kuhlmeyer.hat.persist.TemperatureData;
import net.kuhlmeyer.hat.persist.TemperatureSelected;
import net.kuhlmeyer.hat.service.TemperatureService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * HM-TC-IT-WM-W-EU Temperature Sensor
 *
 * @author christof
 */
public class HMTCITWMWEU extends HMDevice implements TemperatureSensor {

    private static final Logger LOG = Logger.getLogger(HMTCITWMWEU.class);

    public static final String TEMPERATURE = "Temperatur";
    public static final String HUMIDITY = "Feuchtigkeit";
    public static final String LAST_UPDATE = "Letzte Aktualisierung";


    @Autowired private TemperatureService temperatureService;
    @Autowired private EventBus eventBus;
    @Autowired private HomematicAdapter homematicAdapter;


    @Override
    public String getName() {
        return "Temperatur";
    }

    @Override
    public String[] getAvailableTypes() {
        return new String[]{TEMPERATURE, HUMIDITY, LAST_UPDATE};
    }

    @Override
    public Object get(String type, Object... params) {
        TemperatureData data = temperatureService.findLatestData(getId());
        if (data == null) {
            return null;
        }
        if (TEMPERATURE.equals(type)) {
            return data.getTemperature();
        } else if (HUMIDITY.equals(type)) {
            return data.getHumidity();
        } else if (LAST_UPDATE.equals(type)) {
            return data.getTimestamp();
        }
        throw new IllegalStateException();
    }

    @Override
    public DeviceType getType() {
        return DeviceType.TemperaturFuehler;
    }

    @Override
    public String getFormattedHumidity() {
        Double humidity = getHumidity();
        if (humidity == null) {
            return "-";
        } else {
            return String.format("%2.1f%%", humidity);
        }
    }

    @Override
    public String getFormattedTemperature() {
        Double temperature = getTemperature();
        if (temperature == null) {
            return "-";
        } else {
            return String.format("%2.1f°C", temperature);
        }
    }

    @Override
    public Double getHumidity() {
        return (Double) get(HUMIDITY);
    }

    @Override
    public Double getTemperature() {
        return (Double) get(TEMPERATURE);
    }

    @Override
    public Date getLastUpdateTime() {
        return (Date) get(LAST_UPDATE);
    }

    @Override
    public boolean responseReceived(HMResponse data) {
        return true;
    }

    @Override
    public boolean eventReceived(HMEvent data) {
        // Payload = B4865A26047600000094DC2A

        // B4865A260476000000 94DC 2A => 18,5°C SetTemp =>  22°C Temp, 42%
        //                    ---- --
        //                      A   B
        // Selected Temperature = ((A >> 10) & 0x3f) / 2.0
        // Current Temperature  = ((A & 0x3ff) / 10.0
        // Current Humidity     = B

        String payload = data.getPayload();

        if (payload.length() == 24) {

            if (Integer.valueOf(payload.substring(4, 6), 16) == 0x5A) {
                Integer sensorData = Integer.valueOf(payload.substring(18, 22), 16);

                double selTemp = ((sensorData >> 10) & 0x3f) / 2.0;
                double temperature = (sensorData & 0x3ff) / 10.0;
                double humidity = Integer.valueOf(payload.substring(22), 16);

                LOG.debug(String.format("Received temperature for %s: Selected: %f, Current: %f, Humidity: %f", getId(), selTemp, temperature, humidity));


                TemperatureData latestData = temperatureService.findLatestData(getId());

                TemperatureData temperatureData = new TemperatureData();
                temperatureData.setTimestamp(new Date());
                temperatureData.setHumidity(humidity);
                temperatureData.setTemperature(temperature);
                temperatureData.setLocation(getId());
                temperatureService.create(temperatureData);

                TemperatureSelected temperatureSelected = temperatureService.findSelectedTemperatureByLocation(getId());
                LOG.debug("Selected temperature: " + temperatureSelected);
                if (temperatureSelected == null || temperatureSelected != null && !temperatureSelected.getTemperature().equals(selTemp)) {
                    LOG.debug("Updating room temperature for: " + getId());
                    temperatureService.updateRoomTemperature(getId(), selTemp);
                }

                double latestTemperature = latestData == null ? 0d : latestData.getTemperature();
                eventBus.fireEvent(new Event(EventType.TemperatureChange, getId(), getHmId(), latestTemperature, temperature), String.format("Temperature at %s changed from %2.1f to %2.1f", getId(), latestTemperature, temperature, this), false);
            }
        }
        return true;

    }

    @PostConstruct
    public void init() {
        homematicAdapter.registerHMDevice(this);
    }
}

