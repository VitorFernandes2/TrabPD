package Rest;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Username: %s Password: %s";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/login")
    public Greeting login(@RequestParam(value="name", defaultValue="user") String name, @RequestParam(value="password", defaultValue="") String password) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name, password));
    }
}
