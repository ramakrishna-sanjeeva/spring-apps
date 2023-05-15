package org.rks.app.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServiceAPI {

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('SCOPE_profile.non-admin')")
    public String sayHello() {

        return "Hello World";
    }

    @GetMapping("/admin-hello")
    @PreAuthorize("hasAuthority('SCOPE_profile.admin')")
    public String sayAdminHello() {

        return "Hello World, Administrator";
    }
}
