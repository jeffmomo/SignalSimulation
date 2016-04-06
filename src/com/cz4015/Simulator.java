package com.cz4015;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.PriorityQueue;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class Simulator
{
    public static final int TOTAL_DISTANCE = 40;
    public static final int COVERAGE_DISTANCE = 2;
    public static final int FINAL_STATION = 19;

    public static final double EPSILON = 1e-3;
    public static final long Dt_milliseconds = 200;

    private double _simulationTime = 0;

    private PriorityQueue<SimEvent> _eventList;

    public int dropped = 0;
    public int blocked = 0;
    public int handovers = 0;
    public double totalCalls = 1;



    private Station[] _stations = new Station[20];

    Distributions interArrivalDist = new Distributions().setExpLambda(1 / 1.37d);
    Distributions speedDist = new Distributions().setNormMeanStdev(120 / 3600d, 9 / 3600d);
    Distributions durationDist = new Distributions().setExpLambda(1 / 100d).setBias(10);
    Distributions positionDist = new Distributions().setUniformAB(0, 40);

    private final Object _lock = new Object();

    public Simulator() throws Exception
    {
        _eventList = new PriorityQueue<>((a, b) -> Double.compare(a.initTime, b.initTime));

        for(int i = 0; i < _stations.length; i++)
        {
            _stations[i] = new Station(false);
        }

        _eventList.add(new CallInitEvent(interArrivalDist.exponential(), speedDist.normal(), durationDist.exponential(), positionDist.uniform()));

        BufferedWriter bw = new BufferedWriter(new FileWriter("out.csv"));

        for(int i = 0; i < 10000; i++)
        {
            CallInitEvent ci = new CallInitEvent(interArrivalDist.exponential(), speedDist.normal(), durationDist.exponential(), positionDist.uniform());

            bw.write(ci.initTime + "," + getStation(ci.position) + "," + ci.duration + "," + ci.speed);
            bw.newLine();
        }

        bw.close();

        new Thread(() ->
        {
            try
            {
                BufferedWriter bww = new BufferedWriter(new FileWriter("results.csv"));

                //Thread.sleep(2000);

                double diff = 10;
                double prevDeltaDropped = 0;
                double prevDeltaBlocked = 0;

                while(Math.abs(diff) > EPSILON / Dt_milliseconds)
                {
                    Thread.sleep(Dt_milliseconds);

                    diff = Math.max(dropped / totalCalls - prevDeltaDropped, blocked / totalCalls - prevDeltaBlocked);
                    System.out.println(diff);

                    prevDeltaBlocked = blocked / totalCalls;
                    prevDeltaDropped = dropped / totalCalls;
                }


                for(int i = 0; i < 100; i++)
                {
                    synchronized (_lock)
                    {
                        dropped = 0;
                        blocked = 0;
                        totalCalls = 0;
                        handovers = 0;
                    }

                    Thread.sleep(500);

                    synchronized (_lock)
                    {
                        bww.write(dropped / totalCalls + ", " + blocked / totalCalls + ", " + handovers / totalCalls);
                    }

                    bww.newLine();

                    System.out.println("Iteration " + i);
                }

                bww.close();

                System.out.println("Done!");

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }).start();


        while(!_eventList.isEmpty())
        {
            executeEvent();
        }

    }

    private void executeEvent()
    {
        SimEvent event = _eventList.remove();
        if(event == null)
            return;

        _simulationTime = event.initTime;

        synchronized (_lock)
        {
            switch (event.type)
            {
                case CallInit:
                {
                    CallInitEvent e = (CallInitEvent) event;
                    totalCalls++;

                    _eventList.add(new CallInitEvent(_simulationTime + interArrivalDist.exponential(), speedDist.normal(), durationDist.exponential(), positionDist.uniform()));

                    int station = getStation(e.position);
                    if (_stations[station].requestChannel(false))
                    {
                        double distanceLeft = e.duration * e.speed;
                        double finalPosition = (distanceLeft + e.position);

                        if (finalPosition >= getDistance(station + 1))
                        {
                            if (station + 1 > FINAL_STATION)
                            {
                                _eventList.add(new TerminationEvent(_simulationTime + (TOTAL_DISTANCE - e.position) / e.speed, FINAL_STATION, false));
                            } else
                            {
                                double timeToNextStation = (getDistance(station + 1) - e.position) / e.speed;

                                _eventList.add(new HandOverEvent(_simulationTime + timeToNextStation, e.speed, e.duration - timeToNextStation, station));
                            }
                        } else
                        {
                            _eventList.add(new TerminationEvent(_simulationTime + (finalPosition - e.position) / e.speed, station, false).attach(e));
                        }
                    } else
                    {
                        blocked++;
                    }

                    break;
                }
                case HandOver:
                {
                    HandOverEvent e = (HandOverEvent) event;

                    if (_stations[e.exitingStation]._channelsInUse <= 0)
                        System.err.println();

                    _stations[e.exitingStation].freeChannel();

                    if (_stations[e.exitingStation + 1].requestChannel(true))
                    {
                        handovers++;

                        double position = getDistance(e.exitingStation);
                        double finalPosition = e.durationRemaining * e.speed + position;

                        if (e.exitingStation + 1 >= FINAL_STATION)
                        {
                            _eventList.add(new TerminationEvent(position + e.durationRemaining * e.speed, FINAL_STATION, true).attach(e));

                        } else
                        {
                            if (finalPosition <= getDistance(e.exitingStation + 1))
                            {
                                _eventList.add(new TerminationEvent(_simulationTime + e.durationRemaining, e.exitingStation + 1, true).attach(e));

                            } else
                            {
                                double crossStationTime = (getDistance(1) / e.speed);
                                _eventList.add(new HandOverEvent(_simulationTime + crossStationTime, e.speed, e.durationRemaining - crossStationTime, e.exitingStation + 1));
                            }
                        }
                    } else
                    {
                        dropped++;
                    }

                    break;

                }
                case Termination:
                    TerminationEvent e = (TerminationEvent) event;

                    if (_stations[e.exitingStation]._channelsInUse <= 0)
                        System.err.println();

                    _stations[e.exitingStation].freeChannel();

                    break;
            }
        }
    }

    private int getStation(double position)
    {
        return (int) position / 2;
    }

    private double getDistance(int station)
    {
        return station * COVERAGE_DISTANCE;
    }

}
