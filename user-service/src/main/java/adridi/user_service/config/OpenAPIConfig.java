package adridi.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8083");
        devServer.setDescription("Development server");

        Contact contact = new Contact();
        contact.setName("CodeSync101");
        contact.setUrl("https://github.com/CodeSync101");

        return new OpenAPI()
                .info(new Info()
                        .title("User Management Service API")
                        .version("1.0")
                        .contact(contact)
                        .description("API endpoints for managing users, organizations, classrooms, and groups"))
                .servers(List.of(devServer))
                .tags(Arrays.asList(
                        new Tag().name("Users").description("User management operations"),
                        new Tag().name("Organizations").description("Organization management operations"),
                        new Tag().name("Classrooms").description("Classroom management operations"),
                        new Tag().name("Groups").description("Group management operations"),
                        new Tag().name("Admin").description("Admin operations for user assignments")
                ));
    }
}