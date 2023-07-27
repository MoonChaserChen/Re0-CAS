package ink.akira.cas;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CommonTest {
    @Test
    public void testEncrypt () {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        // $2a$10$6gE2TgTR3SsUJrDn22LjLeDscoMjwF27ORppp8ML6.TUV7lH7rSZq
        System.out.println(pe.encode("123456"));
    }
}
