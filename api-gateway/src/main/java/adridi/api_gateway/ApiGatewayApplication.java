package adridi.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.discovery.GatewayDiscoveryClientAutoConfiguration;

@SpringBootApplication(exclude = {GatewayDiscoveryClientAutoConfiguration.class})
public class ApiGatewayApplication {

	public static void main(String[] args) {

		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
