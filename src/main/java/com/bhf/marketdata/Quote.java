package com.bhf.marketdata;

import com.bhf.annotations.Reusable;

/**
 * A simple class representing a quote.
 */
public class Quote {

    @Reusable
    long instrumentId;
    @Reusable
    long bidPrice;
    @Reusable
    long askPrice;
    @Reusable
    long bidQty;
    @Reusable
    long askQty;
    @Reusable
    long timeStamp;
}
