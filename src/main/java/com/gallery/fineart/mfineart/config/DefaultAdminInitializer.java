package com.gallery.fineart.mfineart.config;

import com.gallery.fineart.mfineart.enumeration.Role;
import com.gallery.fineart.mfineart.model.AppUser;
import com.gallery.fineart.mfineart.repository.AppUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultAdminProperties defaultAdminProperties;

    public DefaultAdminInitializer(AppUserRepository appUserRepository,
                                     PasswordEncoder passwordEncoder,
                                     DefaultAdminProperties defaultAdminProperties) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultAdminProperties = defaultAdminProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!defaultAdminProperties.isEnabled() || appUserRepository.count() > 0) {
            return;
        }

        if (StringUtils.isBlank(defaultAdminProperties.getUsername())
                || StringUtils.isBlank(defaultAdminProperties.getPassword())) {
            return;
        }

        AppUser admin = new AppUser();
        admin.setUsername(defaultAdminProperties.getUsername());
        admin.setPassword(passwordEncoder.encode(defaultAdminProperties.getPassword()));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        appUserRepository.save(admin);
    }
}
