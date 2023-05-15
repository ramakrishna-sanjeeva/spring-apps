package org.rks.app.config;

import org.rks.app.auth.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;
import java.util.HashSet;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserProfileService userProfileService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                        .anyRequest().authenticated())
                        .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(this.userAuthoritiesMapper())));
        return http.build();
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            System.out.println();
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    String profile = this.userProfileService.getUserProfile(userInfo.getEmail()).getProfile();

                    mappedAuthorities.add(new GrantedAuthority() {
                        @Override
                        public String getAuthority() {
                            return String.format("SCOPE_profile.%s", profile);
                        }
                    });

                } else {
                    //Investigate to why it is not OidcUserAuthority
                }
            });
            return mappedAuthorities;
        };
    }
}
