package org.myprojecticaro.component;

import org.myprojecticaro.annotations.Component;
import org.myprojecticaro.annotations.Scope;

@Component
@Scope("prototype")
public class RequestScopedBean {

    public RequestScopedBean() {
        System.out.println("[PROTOTYPE] New instance: " + this);
    }

    public void doSomething() {
        System.out.println("[PROTOTYPE] Doing something with: " + this);
    }
}