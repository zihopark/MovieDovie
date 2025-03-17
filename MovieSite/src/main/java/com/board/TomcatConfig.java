package com.board;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//내장 톰캣 환경 설정
@Configuration
public class TomcatConfig {

	//application.properties에서 설정된 값을 가져옴
	@Value("${tomcat.ajp.protocol}")
	String ajpProtocol; //AJP/1.3

	@Value("${tomcat.ajp.port}")
	int ajpPort; //8009

	@Value("${tomcat.ajp.enabled}")
	boolean ajpEnabled; //true

	@Bean
	TomcatServletWebServerFactory servlet() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

		if (ajpEnabled) {
			Connector ajpConnector = new Connector(ajpProtocol);
			ajpConnector.setPort(ajpPort);
			ajpConnector.setSecure(false);
			ajpConnector.setAllowTrace(false);
			ajpConnector.setScheme("http");
			tomcat.addAdditionalTomcatConnectors(ajpConnector);
			//secretRequired = false
			((AbstractAjpProtocol<?>) ajpConnector.getProtocolHandler()).setSecretRequired(false);
		}

		return tomcat;
	}

}