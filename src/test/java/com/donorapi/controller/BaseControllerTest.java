package com.donorapi.controller;

import com.donorapi.models.AuthRequest;
import com.donorapi.models.AuthResponse;
import com.donorapi.service.BaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseControllerTest {

    private BaseService baseService;
    private BaseController baseController;

    @BeforeEach
    void setUp() {
        baseService = mock(BaseService.class);
        baseController = new BaseController(baseService, null, null);
    }

    @Test
    void login_shouldReturnAuthResponse() {
        AuthRequest request = new AuthRequest("muddy", "password");
        AuthResponse expectedResponse = new AuthResponse("dummy-token");

        when(baseService.authenticateUser(request)).thenReturn(ResponseEntity.ok(expectedResponse));

        ResponseEntity<AuthResponse> response = baseController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getToken(), response.getBody().getToken());
        verify(baseService, times(1)).authenticateUser(request);
    }
}