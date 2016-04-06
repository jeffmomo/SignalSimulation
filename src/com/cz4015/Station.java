package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class Station
{
    public static final int MAX_CHANNELS = 10;
    public static final int RESERVED_CHANNELS = 1;

    public int _channelsInUse = 0;
    private boolean _reservedInUse = false;

    private boolean _useAllocation = false;

    public Station(boolean useAllocation)
    {
        _useAllocation = useAllocation;
    }

    public void freeChannel()
    {
        if(_useAllocation && _reservedInUse)
            _reservedInUse = false;
        else
            _channelsInUse--;

        if (_channelsInUse < 0) throw new AssertionError();
    }

    public boolean requestChannel(boolean isHandover)
    {
        if(_useAllocation)
        {

            if(_channelsInUse < MAX_CHANNELS - RESERVED_CHANNELS)
            {
                _channelsInUse++;
                return true;
            }
            else
            {
                if(isHandover)
                {
                    if(_reservedInUse)
                        return false;
                    else
                    {
                        _reservedInUse = true;
                        return true;
                    }
                }
                else
                    return false;
            }

        }
        else
        {
            if(_channelsInUse < MAX_CHANNELS)
            {
                _channelsInUse++;
                return true;
            }
            else
                return false;

        }
    }

}
