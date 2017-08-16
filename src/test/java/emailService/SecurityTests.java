package emailService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void moveUnregisteredUserToLoginPage() throws Exception {
        mockMvc.perform(post("/order"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void registered() throws Exception {
        FormLoginRequestBuilder builder = formLogin()
                .user("user")
                .password("password");

        mockMvc.perform(builder).andExpect(SecurityMockMvcResultMatchers
                .authenticated()
                .withUsername("user"));
    }
}
