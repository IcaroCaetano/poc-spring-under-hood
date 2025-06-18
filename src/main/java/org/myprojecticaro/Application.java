package org.myprojecticaro;


import org.myprojecticaro.context.ApplicationContext;
import org.myprojecticaro.service.ServiceDemo;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("org.myprojecticaro.service");

        ServiceDemo service = context.getBean(ServiceDemo.class);
        service.hello();
    }
}