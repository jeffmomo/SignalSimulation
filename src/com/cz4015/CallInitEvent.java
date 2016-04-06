package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class CallInitEvent extends SimEvent
{
    public double position;
    public double speed;
    public double duration;


    public CallInitEvent(double initTime, double speed, double duration, double position)
    {
        type = EventType.CallInit;

        this.initTime = initTime;
        this.speed = speed;
        this.duration = duration;
        this.position = position;
    }
}
