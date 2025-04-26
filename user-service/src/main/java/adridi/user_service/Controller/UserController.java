package adridi.user_service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class UserController {

    @GetMapping
    public String test() {
        return "Hello Mission Entreprise Collabs from user-service!";
    }
}