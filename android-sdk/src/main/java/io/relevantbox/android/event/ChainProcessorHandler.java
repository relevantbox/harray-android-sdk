package io.relevantbox.android.event;

import java.util.ArrayList;
import java.util.List;

import io.relevantbox.android.model.RBEvent;

public class ChainProcessorHandler {
    private List<AfterPageViewEventHandler> handlers = new ArrayList<>();

    public void addHandler(AfterPageViewEventHandler afterPageViewEventHandler){
        this.handlers.add(afterPageViewEventHandler);
    }

    public void callAll(RBEvent event){
        for (AfterPageViewEventHandler eachHandler : handlers) {
            eachHandler.callAfter(event);
        }
    }
}
