package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class Distributions
{
    private double _lambda = 1;
    private double _mean = 0;
    private double _stdev = 1;
    private double _a = 0;
    private double _b = 1;
    private double _bias = 0;

    private static final double SQRT_2_PI = Math.sqrt(2 * Math.PI);

    public Distributions()
    {

    }

    public Distributions setBias(double bias)
    {
        _bias = bias;
        return this;
    }

    public Distributions setExpLambda(double lambda)
    {
        _lambda = lambda;
        return this;
    }

    public Distributions setNormMeanStdev(double mean, double stdev)
    {
        _mean = mean;
        _stdev = stdev;
        return this;
    }

    public Distributions setUniformAB(double a, double b)
    {
        _a = a;
        _b = b;
        return this;
    }

    public double uniform()
    {
        return uniform(Math.random());
    }

    private double bias(double val)
    {
        return val + _bias;
    }

    public double uniform(double stdUniform)
    {
        return bias(stdUniform * (_b - _a) + _a);
    }

    public double exponential(double stdUniform)
    {
        return bias(-Math.log(1 - stdUniform) / _lambda);
    }

    public double exponential()
    {
        return exponential(Math.random());
    }


    public double normal()
    {
        double sum = 0;

        for(int i = 0; i < 12; i++)
        {
            sum += Math.random();
        }

        return bias(_mean + (sum - 6) * _stdev);
    }

//    public static double normal(double stdUniform)
//    {
//        double var = Math.sqrt(2 * _stdev * _stdev * Math.log(_stdev * stdUniform * SQRT_2_PI));
//        return var + _mean;
//    }
}
