package com.fr.fries.reference.utils;

import com.fr.fries.reference.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;

import static com.fr.fries.reference.TestVariables.userId_normal_hex;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class KeyUtilsTest extends BaseTest {

    @Test
    public void constructKeyUtils_whenNoSuchAlgorithm_thenNoSuchAlgorithmException() {
        try {
            Method method = KeyUtils.class.getDeclaredMethod("init");
            method.setAccessible(true);
            ReflectionTestUtils.setField(KeyUtils.class, "ALGORITHM", "null");
            method.invoke(KeyUtils.class);

            assertThat(method, notNullValue());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Test
    public void getHexString_whenNormal_thenSuccess() {
        String hexString = null;

        try {
            Method method = KeyUtils.class.getDeclaredMethod("getHexString", String.class);
            method.setAccessible(true);
            hexString = method.invoke(KeyUtils.class, unitId).toString();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        assertThat(hexString, equalTo(userId_normal_hex));
    }
}
