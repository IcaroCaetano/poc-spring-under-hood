package org.myprojecticaro.events;

public interface EventListener<T> {
    void onEvent(T event);
}