# basic-reusable-ap

Basic annotation processing for @Reusable

## TLDR;

1. Annotate fields with @Reusable

```java
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

```

2. Relevant build output

```bash
Note: Processing Annotation: Reusable
Note: Classname: com.bhf.marketdata.Quote
Note: Element: instrumentId, Type: long
Note: Element: bidPrice, Type: long
Note: Element: askPrice, Type: long
Note: Element: bidQty, Type: long
Note: Element: askQty, Type: long
Note: Element: timeStamp, Type: long
Note: Package: com.bhf.marketdata, simpleClassName: Quote, reusableSimpleClassName: ReusableQuote, reusableQualifiedClassName: com.bhf.marketdata.ReusableQuote
Note: Finished Processing all annotations
Note: Finished Processing all annotations
Note: Finished Processing all annotations
```

3. Get the generated ReusableQuote:

```java
package com.bhf.marketdata;

public class ReusableQuote {

    private Quote object = new Quote();

    public Quote build() {
        return object;
    }

    public void clear() {
        object.instrumentId = 0;
        object.askQty = 0;
        object.bidPrice = 0;
        object.timeStamp = 0;
        object.bidQty = 0;
        object.askPrice = 0;
    }

    public void copyFrom(ReusableQuote source) {
        object.instrumentId = source.object.instrumentId;
        object.askQty = source.object.askQty;
        object.bidPrice = source.object.bidPrice;
        object.timeStamp = source.object.timeStamp;
        object.bidQty = source.object.bidQty;
        object.askPrice = source.object.askPrice;
    }

}

```