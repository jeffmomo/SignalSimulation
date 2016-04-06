package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class SimEvent
{
    public EventType type;
    public double initTime;

    public SimEvent prev;

    public SimEvent attach(SimEvent e)
    {
        prev = e;
        return this;
    }
}
