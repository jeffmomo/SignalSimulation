package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class TerminationEvent extends SimEvent
{
    public int exitingStation;
    public boolean isHandOver;



    public TerminationEvent(double initTime, int exitingStation, boolean isHandOver)
    {

        type = EventType.Termination;

        this.exitingStation = exitingStation;
        this.initTime = initTime;
        this.isHandOver = isHandOver;
    }
}
