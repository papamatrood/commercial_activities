package com.commercial.activities.service.mapper;

import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Permission;
import com.commercial.activities.domain.User;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.dto.CompanyDTO;
import com.commercial.activities.service.dto.PermissionDTO;
import com.commercial.activities.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "company", source = "company", qualifiedByName = "companyId")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionIdSet")
    AppUserDTO toDto(AppUser s);

    @Mapping(target = "removePermission", ignore = true)
    AppUser toEntity(AppUserDTO appUserDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("companyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CompanyDTO toDtoCompanyId(Company company);

    @Named("permissionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PermissionDTO toDtoPermissionId(Permission permission);

    @Named("permissionIdSet")
    default Set<PermissionDTO> toDtoPermissionIdSet(Set<Permission> permission) {
        return permission.stream().map(this::toDtoPermissionId).collect(Collectors.toSet());
    }
}
