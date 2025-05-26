package adridi.user_service.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8083");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("CodeSync101");
        contact.setUrl("https://github.com/CodeSync101");

        Info info = new Info()
                .title("User Service API")
                .version("1.0")
                .contact(contact)
                .description("This API provides endpoints for managing users, organizations, classrooms, and group repositories.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}