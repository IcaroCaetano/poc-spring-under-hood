package org.myprojecticaro.events;

import org.myprojecticaro.annotations.Component;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventPublisher {

    private final List<EventListener<?>> listeners = new ArrayList<>();

    public void registerListener(EventListener<?> listener) {
        listeners.add(listener);
    }

    public void publish(Object event) {
        for (EventListener<?> listener : listeners) {
            Class<?> listenerEventType = resolveListenerEventType(listener);
            if (listenerEventType != null && listenerEventType.isInstance(event)) {
                ((EventListener<Object>) listener).onEvent(event);
            }
        }
    }

    private Class<?> resolveListenerEventType(EventListener<?> listener) {
        try {
            var genericInterface = listener.getClass().getGenericInterfaces()[0];
            if (genericInterface instanceof ParameterizedType type) {
                return (Class<?>) type.getActualTypeArguments()[0];
            }
        } catch (Exception ignored) {}
        return null;
    }
}
