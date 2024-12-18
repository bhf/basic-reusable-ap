package com.bhf.marketdata;

import com.bhf.annotations.Reusable;

/**
 * A simple class representing a quote.
 */
public class Quote {

    @Reusable
    public long instrumentId;
    @Reusable
    public long bidPrice;
    @Reusable
    public long askPrice;
    @Reusable
    public long bidQty;
    @Reusable
    public long askQty;
    @Reusable
    public long timeStamp;
}
