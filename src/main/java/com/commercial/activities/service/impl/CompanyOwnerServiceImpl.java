package com.commercial.activities.service.impl;

import com.commercial.activities.config.Constants;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Authority;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.User;
import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.repository.AppUserRepository;
import com.commercial.activities.repository.AuthorityRepository;
import com.commercial.activities.repository.CompanyRepository;
import com.commercial.activities.repository.UserRepository;
import com.commercial.activities.security.AuthoritiesConstants;
import com.commercial.activities.service.CompanyOwnerService;
import com.commercial.activities.service.dto.CompanyOwnerDTO;
import com.commercial.activities.service.dto.CompanyWithOwnerDTO;
import com.commercial.activities.service.mapper.CompanyMapper;
import com.commercial.activities.web.rest.errors.BadRequestAlertException;
import com.commercial.activities.web.rest.errors.EmailAlreadyUsedException;
import com.commercial.activities.web.rest.errors.LoginAlreadyUsedException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing a {@link Company} together with its owner.
 */
@Service
@Transactional
public class CompanyOwnerServiceImpl implements CompanyOwnerService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyOwnerServiceImpl.class);

    private static final String ENTITY_NAME = "company";

    private final CompanyRepository companyRepository;

    private final AppUserRepository appUserRepository;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    private final CompanyMapper companyMapper;

    public CompanyOwnerServiceImpl(
        CompanyRepository companyRepository,
        AppUserRepository appUserRepository,
        UserRepository userRepository,
        AuthorityRepository authorityRepository,
        PasswordEncoder passwordEncoder,
        CompanyMapper companyMapper
    ) {
        this.companyRepository = companyRepository;
        this.appUserRepository = appUserRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyMapper = companyMapper;
    }

    @Override
    public CompanyWithOwnerDTO create(CompanyWithOwnerDTO companyWithOwnerDTO) {
        LOG.debug("Request to create Company with owner : {}", companyWithOwnerDTO);
        if (companyWithOwnerDTO.getCompany().getId() != null) {
            throw new BadRequestAlertException("A new company cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (companyWithOwnerDTO.getOwner().getId() != null) {
            throw new BadRequestAlertException("A new owner cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Company company = companyMapper.toEntity(companyWithOwnerDTO.getCompany());
        company = companyRepository.save(company);

        User user = createOwnerUser(companyWithOwnerDTO.getOwner());
        AppUser appUser = createOwnerAppUser(companyWithOwnerDTO.getOwner(), user, company);

        return toCompanyWithOwnerDTO(company, appUser);
    }

    @Override
    public CompanyWithOwnerDTO update(Long companyId, CompanyWithOwnerDTO companyWithOwnerDTO) {
        LOG.debug("Request to update Company with owner : {}, {}", companyId, companyWithOwnerDTO);
        Company company = companyRepository
            .findById(companyId)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));

        company.setName(companyWithOwnerDTO.getCompany().getName());
        company.setLocation(companyWithOwnerDTO.getCompany().getLocation());
        company.setCreationDate(companyWithOwnerDTO.getCompany().getCreationDate());
        company.setStatus(companyWithOwnerDTO.getCompany().getStatus());
        company = companyRepository.save(company);

        Optional<AppUser> existingOwner = appUserRepository.findFirstByCompany_IdAndTypeOrderByIdAsc(
            companyId,
            AppUserTypeEnum.COMPANY_ADMIN
        );

        AppUser appUser;
        if (existingOwner.isPresent()) {
            appUser = existingOwner.get();
            updateOwnerUser(appUser.getUser(), companyWithOwnerDTO.getOwner());
            updateOwnerAppUser(appUser, companyWithOwnerDTO.getOwner());
        } else {
            User user = createOwnerUser(companyWithOwnerDTO.getOwner());
            appUser = createOwnerAppUser(companyWithOwnerDTO.getOwner(), user, company);
        }

        return toCompanyWithOwnerDTO(company, appUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyWithOwnerDTO> findOneWithOwner(Long companyId) {
        LOG.debug("Request to get Company with owner : {}", companyId);
        return companyRepository
            .findById(companyId)
            .map(company -> {
                AppUser owner = appUserRepository
                    .findFirstByCompany_IdAndTypeOrderByIdAsc(companyId, AppUserTypeEnum.COMPANY_ADMIN)
                    .orElse(null);
                return toCompanyWithOwnerDTO(company, owner);
            });
    }

    private User createOwnerUser(CompanyOwnerDTO ownerDTO) {
        userRepository
            .findOneByLogin(ownerDTO.getLogin().toLowerCase())
            .ifPresent(existing -> {
                throw new LoginAlreadyUsedException();
            });
        if (ownerDTO.getEmail() != null) {
            userRepository
                .findOneByEmailIgnoreCase(ownerDTO.getEmail())
                .ifPresent(existing -> {
                    throw new EmailAlreadyUsedException();
                });
        }

        User user = new User();
        user.setLogin(ownerDTO.getLogin().toLowerCase());
        user.setFirstName(ownerDTO.getFirstName());
        user.setLastName(ownerDTO.getLastName());
        if (ownerDTO.getEmail() != null) {
            user.setEmail(ownerDTO.getEmail().toLowerCase());
        }
        user.setLangKey(ownerDTO.getLangKey() == null ? Constants.DEFAULT_LANGUAGE : ownerDTO.getLangKey());
        user.setPassword(passwordEncoder.encode(ownerDTO.getLogin().toLowerCase() + "123"));
        user.setActivated(true);
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    private void updateOwnerUser(User user, CompanyOwnerDTO ownerDTO) {
        String newLogin = ownerDTO.getLogin().toLowerCase();
        if (!newLogin.equals(user.getLogin())) {
            userRepository
                .findOneByLogin(newLogin)
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new LoginAlreadyUsedException();
                });
        }
        if (ownerDTO.getEmail() != null) {
            userRepository
                .findOneByEmailIgnoreCase(ownerDTO.getEmail())
                .filter(existing -> !existing.getId().equals(user.getId()))
                .ifPresent(existing -> {
                    throw new EmailAlreadyUsedException();
                });
        }

        user.setLogin(newLogin);
        user.setFirstName(ownerDTO.getFirstName());
        user.setLastName(ownerDTO.getLastName());
        if (ownerDTO.getEmail() != null) {
            user.setEmail(ownerDTO.getEmail().toLowerCase());
        }
        user.setLangKey(ownerDTO.getLangKey() == null ? Constants.DEFAULT_LANGUAGE : ownerDTO.getLangKey());
        userRepository.save(user);
    }

    private AppUser createOwnerAppUser(CompanyOwnerDTO ownerDTO, User user, Company company) {
        AppUser appUser = new AppUser();
        applyOwnerFields(appUser, ownerDTO);
        appUser.setUser(user);
        appUser.setCompany(company);
        return appUserRepository.save(appUser);
    }

    private void updateOwnerAppUser(AppUser appUser, CompanyOwnerDTO ownerDTO) {
        applyOwnerFields(appUser, ownerDTO);
        appUserRepository.save(appUser);
    }

    private void applyOwnerFields(AppUser appUser, CompanyOwnerDTO ownerDTO) {
        boolean disabled = Boolean.TRUE.equals(ownerDTO.getDisabled());
        appUser.setPhoneNumber(ownerDTO.getPhoneNumber());
        appUser.setType(ownerDTO.getType());
        appUser.setBirthDate(ownerDTO.getBirthDate());
        appUser.setBirthPlace(ownerDTO.getBirthPlace());
        appUser.setGender(ownerDTO.getGender());
        appUser.setDisabled(disabled);
        appUser.setDisabledDate(disabled ? Instant.now() : null);
    }

    private CompanyWithOwnerDTO toCompanyWithOwnerDTO(Company company, AppUser appUser) {
        CompanyWithOwnerDTO dto = new CompanyWithOwnerDTO();
        dto.setCompany(companyMapper.toDto(company));
        dto.setOwner(toCompanyOwnerDTO(appUser));
        return dto;
    }

    private CompanyOwnerDTO toCompanyOwnerDTO(AppUser appUser) {
        CompanyOwnerDTO ownerDTO = new CompanyOwnerDTO();
        if (appUser == null) {
            return ownerDTO;
        }
        ownerDTO.setId(appUser.getId());
        ownerDTO.setPhoneNumber(appUser.getPhoneNumber());
        ownerDTO.setType(appUser.getType());
        ownerDTO.setBirthDate(appUser.getBirthDate());
        ownerDTO.setBirthPlace(appUser.getBirthPlace());
        ownerDTO.setGender(appUser.getGender());
        ownerDTO.setDisabled(appUser.getDisabled());

        User user = appUser.getUser();
        if (user != null) {
            ownerDTO.setUserId(user.getId());
            ownerDTO.setLogin(user.getLogin());
            ownerDTO.setFirstName(user.getFirstName());
            ownerDTO.setLastName(user.getLastName());
            ownerDTO.setEmail(user.getEmail());
            ownerDTO.setLangKey(user.getLangKey());
        }
        return ownerDTO;
    }
}
