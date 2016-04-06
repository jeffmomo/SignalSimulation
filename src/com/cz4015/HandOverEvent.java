package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class HandOverEvent extends SimEvent
{
    public double speed;
    public double durationRemaining;
    public int exitingStation;



    public HandOverEvent(double initTime, double speed, double durationRemaining, int exitingStation)
    {

        type = EventType.HandOver;

        this.initTime = initTime;
        this.durationRemaining = durationRemaining;
        this.speed = speed;
        this.exitingStation = exitingStation;
    }

}
