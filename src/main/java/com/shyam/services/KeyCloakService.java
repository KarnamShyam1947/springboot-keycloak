package com.shyam.services;

import java.util.List;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shyam.dto.UserRequest;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeyCloakService {
    
    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;   
    
    public void addUser(UserRequest request) {
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

        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        // String userId = CreatedResponseUtil.getCreatedId(response);
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

    
}