package com.live.main.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    public String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String sslTrust;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;
    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int timeout;

    @Bean
    public JavaMailSender mailSender(){
      JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
      mailSender.setHost(host);
      mailSender.setPort(port);
      mailSender.setUsername(username);
      mailSender.setPassword(password);

      Properties props = mailSender.getJavaMailProperties();
      props.put("mail.smtp.auth", String.valueOf(auth));
      props.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
      props.put("mail.smtp.ssl.trust", sslTrust);
      props.put("mail.smtp.timeout", String.valueOf(timeout));
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.debug", "false");

      return mailSender;
    }
}
