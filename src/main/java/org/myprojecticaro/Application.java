package org.myprojecticaro;


import org.myprojecticaro.context.ApplicationContext;
import org.myprojecticaro.service.MessageService;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("org.myprojecticaro.service");

        MessageService service = context.getBean(MessageService.class);
        service.hello();
    }
}