package com.workchatia;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WorkChatIaApplication {
 public static void main(String[] args){SpringApplication.run(WorkChatIaApplication.class,args);}
}