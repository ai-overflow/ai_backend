package de.hskl.ki.resource;

import de.hskl.ki.models.auth.AuthenticationRequest;
import de.hskl.ki.models.auth.AuthenticationResponse;
import de.hskl.ki.models.exceptions.AIException;
import de.hskl.ki.services.CustomUserDetailsService;
import de.hskl.ki.services.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/a/")
public class AuthResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AIException("Incorrect username or password", e);
        }

        try {
            final var userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final AuthenticationResponse jwt = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(jwt);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
