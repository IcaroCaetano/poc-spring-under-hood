package org.myprojecticaro;


import org.myprojecticaro.component.RequestScopedBean;
import org.myprojecticaro.context.ApplicationContext;
import org.myprojecticaro.service.MessageService;
import org.myprojecticaro.service.NotificationService;
import org.myprojecticaro.service.RegistrationService;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("org.myprojecticaro");

        MessageService service = context.getBean(MessageService.class);
        service.hello();

        RegistrationService registration = context.getBean(RegistrationService.class);
        registration.register("icaro.dev");

        NotificationService notification = context.getBean(NotificationService.class);
        notification.notifyUser("icaro.dev");

        RequestScopedBean r1 = context.getBean(RequestScopedBean.class);
        RequestScopedBean r2 = context.getBean(RequestScopedBean.class);

        System.out.println(r1 == r2);
    }
}