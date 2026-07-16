package com.commercial.activities.domain;

import static com.commercial.activities.domain.AppUserTestSamples.*;
import static com.commercial.activities.domain.CompanyTestSamples.*;
import static com.commercial.activities.domain.PermissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.commercial.activities.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void companyTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        appUser.setCompany(companyBack);
        assertThat(appUser.getCompany()).isEqualTo(companyBack);

        appUser.company(null);
        assertThat(appUser.getCompany()).isNull();
    }

    @Test
    void permissionTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Permission permissionBack = getPermissionRandomSampleGenerator();

        appUser.addPermission(permissionBack);
        assertThat(appUser.getPermissions()).containsOnly(permissionBack);

        appUser.removePermission(permissionBack);
        assertThat(appUser.getPermissions()).doesNotContain(permissionBack);

        appUser.permissions(new HashSet<>(Set.of(permissionBack)));
        assertThat(appUser.getPermissions()).containsOnly(permissionBack);

        appUser.setPermissions(new HashSet<>());
        assertThat(appUser.getPermissions()).doesNotContain(permissionBack);
    }
}
