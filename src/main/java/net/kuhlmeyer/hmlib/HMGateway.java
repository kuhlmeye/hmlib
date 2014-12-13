package net.kuhlmeyer.hmlib;

import net.kuhlmeyer.hmlib.event.HMEventCallback;

import java.util.function.Consumer;

/**
 * Interfaces to be implemented by all classes acting as a gateway to the homematic world. This is used by the devices to communicate with
 * homematic.
 */
public interface HMGateway {

    /**
     * Send a certain payload as a homematic command.
     */
    int sendCommand(String payload);

    /**
     * Get the homematic id of this gateway.
     */
    String getGatewayId();

    /**
     * Uses by the devices to notify the callback about a homatic event..
     */
    void notifyCallback(final Consumer<HMEventCallback> callback);
}
