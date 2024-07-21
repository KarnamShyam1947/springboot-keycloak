package com.shyam.services;

import java.util.List;

import org.jboss.resteasy.core.ExceptionAdapter;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shyam.dto.requests.LoginRequest;
import com.shyam.dto.requests.UserRequest;
import com.shyam.dto.responses.LoginResponse;
import com.shyam.dto.responses.UserResponse;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeyCloakService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.client.client-id}")
    private String keycloakClientId;

    private final Keycloak keycloak;   
    
    public UserResponse addUser(UserRequest request) {
        UserRepresentation  userRepresentation= new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setUsername(request.getUserName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(false);
        
        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(request.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
        // userRepresentation.isEmailVerified();
        UsersResource user = getUsersResource();
        Response response = user.create(userRepresentation);

        UserResponse userResponse = new UserResponse();
        if (response.getStatus() >= 400) {
            String userId = CreatedResponseUtil.getCreatedId(response);

            userResponse.setUserId(userId);
            userResponse.setStatusCode(response.getStatus());
            userResponse.setMessage("User created successfully");
            userResponse.setStatus(String.format("%s", response.getStatusInfo()));

            return userResponse;
        }

        userResponse.setStatusCode(response.getStatus());
        userResponse.setMessage("Failed to create user");
        userResponse.setStatus(String.format("%s", response.getStatusInfo()));

        return userResponse;
    }

    private UsersResource getUsersResource(){
        return keycloak.realm(realm).users();
    }

     public UserResource getUser(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    public UserRepresentation getUserByEmail(String email) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(email, true);
        return userRepresentations.get(0);
    }

    public void sendVerificationEmail(String userId) {

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();

    }

    public LoginResponse loginUser(LoginRequest loginRequest) {

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", keycloakClientId);
            body.add("grant_type", "password");
            body.add("password", loginRequest.getPassword());
            body.add("username", loginRequest.getUsername());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<LoginResponse> requestEntity = restTemplate.postForEntity("/token", entity, LoginResponse.class);
            return requestEntity.getBody();
        }
        catch(Exception e) {
            System.out.println(e);
            return LoginResponse
                    .builder()
                    .message("Invalid user details")
                    .build();
        }
    }
    
}