package com.example.englishlearningsoftware;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Request;
import okio.Buffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountApiTest {
    private final AccountApi api = new AccountApi("http://10.0.2.2:8080/");

    @Test
    public void loginWithEmailPreservesSpecialCharactersAndPasswordSpaces() {
        Request request = api.loginRequest("student+class@example.com", " p&?+# word ");
        assertEquals("GET", request.method());
        assertEquals("/api/accounts/login", request.url().encodedPath());
        assertEquals("student+class@example.com", request.url().queryParameter("email"));
        assertEquals("", request.url().queryParameter("phoneNumber"));
        assertEquals(" p&?+# word ", request.url().queryParameter("password"));
    }

    @Test
    public void loginWithPhoneUsesPhoneParameter() {
        Request request = api.loginRequest("0912345678", "secret");
        assertEquals("", request.url().queryParameter("email"));
        assertEquals("0912345678", request.url().queryParameter("phoneNumber"));
    }

    @Test
    public void registrationPostsJsonToOriginalEndpoint() throws IOException {
        String json = "{\"firstName\":\"Minh\",\"phoneNumber\":\"0912345678\"}";
        Request request = api.registerRequest(json);
        assertEquals("POST", request.method());
        assertEquals("/api/accounts/register", request.url().encodedPath());
        assertNotNull(request.body());
        assertEquals("application/json; charset=utf-8", request.body().contentType().toString());
        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        assertEquals(json, buffer.readUtf8());
    }
}
