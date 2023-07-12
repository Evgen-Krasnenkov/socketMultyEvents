package org.kras.socket.Controller;

import org.kras.socket.model.Session;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class SessionController {

    @PostMapping("/session")
    public Session getSession(){
        UUID uuid = UUID.randomUUID();
        return new Session(uuid.toString());
    }
}
