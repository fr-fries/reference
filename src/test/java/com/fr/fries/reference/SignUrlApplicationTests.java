package com.fr.fries.reference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class SignUrlApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(context, notNullValue());
    }
}

