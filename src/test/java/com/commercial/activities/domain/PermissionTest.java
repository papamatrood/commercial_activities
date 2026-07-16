package com.commercial.activities.domain;

import static com.commercial.activities.domain.AppUserTestSamples.*;
import static com.commercial.activities.domain.PermissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Permission.class);
        Permission permission1 = getPermissionSample1();
        Permission permission2 = new Permission();
        assertThat(permission1).isNotEqualTo(permission2);

        permission2.setId(permission1.getId());
        assertThat(permission1).isEqualTo(permission2);

        permission2 = getPermissionSample2();
        assertThat(permission1).isNotEqualTo(permission2);
    }

    @Test
    void appUserTest() {
        Permission permission = getPermissionRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        permission.addAppUser(appUserBack);
        assertThat(permission.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getPermissions()).containsOnly(permission);

        permission.removeAppUser(appUserBack);
        assertThat(permission.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getPermissions()).doesNotContain(permission);

        permission.appUsers(new HashSet<>(Set.of(appUserBack)));
        assertThat(permission.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getPermissions()).containsOnly(permission);

        permission.setAppUsers(new HashSet<>());
        assertThat(permission.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getPermissions()).doesNotContain(permission);
    }
}
