package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Permission;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.dto.PermissionDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Permission} and its DTO {@link PermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper extends EntityMapper<PermissionDTO, Permission> {
    @Mapping(target = "appUsers", source = "appUsers", qualifiedByName = "appUserIdSet")
    PermissionDTO toDto(Permission s);

    @Mapping(target = "appUsers", ignore = true)
    @Mapping(target = "removeAppUser", ignore = true)
    Permission toEntity(PermissionDTO permissionDTO);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);

    @Named("appUserIdSet")
    default Set<AppUserDTO> toDtoAppUserIdSet(Set<AppUser> appUser) {
        return appUser.stream().map(this::toDtoAppUserId).collect(Collectors.toSet());
    }
}
