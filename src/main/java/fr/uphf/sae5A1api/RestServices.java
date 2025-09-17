package fr.uphf.sae5A1api;

import fr.uphf.sae5A1api.data.users.Coach;
import fr.uphf.sae5A1api.data.managers.users.UserManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RestServices {

    @GetMapping(value = "/")
    public ResponseEntity<String> pong() {
        System.out.println("Starting services : OK.");
        return new ResponseEntity<>("Server response : " + HttpStatus.OK.name(), HttpStatus.OK);
    }

    @GetMapping("/coach")
    @ResponseBody
    public ResponseEntity<?> getCoachByMail(@RequestParam String mail) {
        Coach coach = UserManager.getByMail(mail);

        if (coach == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Coach not found a coach for email: " + mail);
        }

        return ResponseEntity.ok(coach);
    }

}
