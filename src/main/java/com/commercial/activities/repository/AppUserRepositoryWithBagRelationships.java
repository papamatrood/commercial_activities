package com.commercial.activities.repository;

import com.commercial.activities.domain.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface AppUserRepositoryWithBagRelationships {
    Optional<AppUser> fetchBagRelationships(Optional<AppUser> appUser);

    List<AppUser> fetchBagRelationships(List<AppUser> appUsers);

    Page<AppUser> fetchBagRelationships(Page<AppUser> appUsers);
}
