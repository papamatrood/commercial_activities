package com.commercial.activities.web.rest;

import static com.commercial.activities.domain.AppUserAsserts.*;
import static com.commercial.activities.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.commercial.activities.IntegrationTest;
import com.commercial.activities.domain.AppUser;
import com.commercial.activities.domain.Company;
import com.commercial.activities.domain.Permission;
import com.commercial.activities.domain.User;
import com.commercial.activities.domain.enumeration.AppUserTypeEnum;
import com.commercial.activities.domain.enumeration.GenderEnum;
import com.commercial.activities.repository.AppUserRepository;
import com.commercial.activities.repository.UserRepository;
import com.commercial.activities.service.AppUserService;
import com.commercial.activities.service.dto.AppUserDTO;
import com.commercial.activities.service.mapper.AppUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final AppUserTypeEnum DEFAULT_TYPE = AppUserTypeEnum.SUPER_ADMIN;
    private static final AppUserTypeEnum UPDATED_TYPE = AppUserTypeEnum.COMPANY_ADMIN;

    private static final Instant DEFAULT_BIRTH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTH_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String DEFAULT_BIRTH_PLACE = "AAAAAAAAAA";
    private static final String UPDATED_BIRTH_PLACE = "BBBBBBBBBB";

    private static final GenderEnum DEFAULT_GENDER = GenderEnum.MALE;
    private static final GenderEnum UPDATED_GENDER = GenderEnum.FEMALE;

    private static final Boolean DEFAULT_DISABLED = false;
    private static final Boolean UPDATED_DISABLED = true;

    private static final Instant DEFAULT_DISABLED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DISABLED_DATE = Instant.ofEpochMilli(1784236791515L);

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    @Autowired
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserService appUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    private AppUser insertedAppUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity() {
        return new AppUser()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .type(DEFAULT_TYPE)
            .birthDate(DEFAULT_BIRTH_DATE)
            .birthPlace(DEFAULT_BIRTH_PLACE)
            .gender(DEFAULT_GENDER)
            .disabled(DEFAULT_DISABLED)
            .disabledDate(DEFAULT_DISABLED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity() {
        return new AppUser()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .type(UPDATED_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .gender(UPDATED_GENDER)
            .disabled(UPDATED_DISABLED)
            .disabledDate(UPDATED_DISABLED_DATE);
    }

    @BeforeEach
    void initTest() {
        appUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppUser != null) {
            appUserRepository.delete(insertedAppUser);
            insertedAppUser = null;
        }
    }

    @Test
    @Transactional
    void createAppUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        var returnedAppUserDTO = om.readValue(
            restAppUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppUserDTO.class
        );

        // Validate the AppUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppUser = appUserMapper.toEntity(returnedAppUserDTO);
        assertAppUserUpdatableFieldsEquals(returnedAppUser, getPersistedAppUser(returnedAppUser));

        insertedAppUser = returnedAppUser;
    }

    @Test
    @Transactional
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setPhoneNumber(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setType(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppUsers() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].birthPlace").value(hasItem(DEFAULT_BIRTH_PLACE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].disabled").value(hasItem(DEFAULT_DISABLED)))
            .andExpect(jsonPath("$.[*].disabledDate").value(hasItem(DEFAULT_DISABLED_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(appUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL_ID, appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.birthPlace").value(DEFAULT_BIRTH_PLACE))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.disabled").value(DEFAULT_DISABLED))
            .andExpect(jsonPath("$.disabledDate").value(DEFAULT_DISABLED_DATE.toString()));
    }

    @Test
    @Transactional
    void getAppUsersByIdFiltering() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        Long id = appUser.getId();

        defaultAppUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber equals to
        defaultAppUserFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber in
        defaultAppUserFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber is not null
        defaultAppUserFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber contains
        defaultAppUserFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber does not contain
        defaultAppUserFiltering("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER, "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where type equals to
        defaultAppUserFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllAppUsersByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where type in
        defaultAppUserFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllAppUsersByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where type is not null
        defaultAppUserFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthDate equals to
        defaultAppUserFiltering("birthDate.equals=" + DEFAULT_BIRTH_DATE, "birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthDate in
        defaultAppUserFiltering("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE, "birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthDate is not null
        defaultAppUserFiltering("birthDate.specified=true", "birthDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthPlaceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthPlace equals to
        defaultAppUserFiltering("birthPlace.equals=" + DEFAULT_BIRTH_PLACE, "birthPlace.equals=" + UPDATED_BIRTH_PLACE);
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthPlaceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthPlace in
        defaultAppUserFiltering("birthPlace.in=" + DEFAULT_BIRTH_PLACE + "," + UPDATED_BIRTH_PLACE, "birthPlace.in=" + UPDATED_BIRTH_PLACE);
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthPlaceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthPlace is not null
        defaultAppUserFiltering("birthPlace.specified=true", "birthPlace.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthPlaceContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthPlace contains
        defaultAppUserFiltering("birthPlace.contains=" + DEFAULT_BIRTH_PLACE, "birthPlace.contains=" + UPDATED_BIRTH_PLACE);
    }

    @Test
    @Transactional
    void getAllAppUsersByBirthPlaceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where birthPlace does not contain
        defaultAppUserFiltering("birthPlace.doesNotContain=" + UPDATED_BIRTH_PLACE, "birthPlace.doesNotContain=" + DEFAULT_BIRTH_PLACE);
    }

    @Test
    @Transactional
    void getAllAppUsersByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where gender equals to
        defaultAppUserFiltering("gender.equals=" + DEFAULT_GENDER, "gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllAppUsersByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where gender in
        defaultAppUserFiltering("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER, "gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllAppUsersByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where gender is not null
        defaultAppUserFiltering("gender.specified=true", "gender.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabled equals to
        defaultAppUserFiltering("disabled.equals=" + DEFAULT_DISABLED, "disabled.equals=" + UPDATED_DISABLED);
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabled in
        defaultAppUserFiltering("disabled.in=" + DEFAULT_DISABLED + "," + UPDATED_DISABLED, "disabled.in=" + UPDATED_DISABLED);
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabled is not null
        defaultAppUserFiltering("disabled.specified=true", "disabled.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabledDate equals to
        defaultAppUserFiltering("disabledDate.equals=" + DEFAULT_DISABLED_DATE, "disabledDate.equals=" + UPDATED_DISABLED_DATE);
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabledDate in
        defaultAppUserFiltering(
            "disabledDate.in=" + DEFAULT_DISABLED_DATE + "," + UPDATED_DISABLED_DATE,
            "disabledDate.in=" + UPDATED_DISABLED_DATE
        );
    }

    @Test
    @Transactional
    void getAllAppUsersByDisabledDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where disabledDate is not null
        defaultAppUserFiltering("disabledDate.specified=true", "disabledDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            appUserRepository.saveAndFlush(appUser);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        appUser.setUser(user);
        appUserRepository.saveAndFlush(appUser);
        Long userId = user.getId();
        // Get all the appUserList where user equals to userId
        defaultAppUserShouldBeFound("userId.equals=" + userId);

        // Get all the appUserList where user equals to (userId + 1)
        defaultAppUserShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllAppUsersByCompanyIsEqualToSomething() throws Exception {
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            appUserRepository.saveAndFlush(appUser);
            company = CompanyResourceIT.createEntity();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        em.persist(company);
        em.flush();
        appUser.setCompany(company);
        appUserRepository.saveAndFlush(appUser);
        Long companyId = company.getId();
        // Get all the appUserList where company equals to companyId
        defaultAppUserShouldBeFound("companyId.equals=" + companyId);

        // Get all the appUserList where company equals to (companyId + 1)
        defaultAppUserShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    @Test
    @Transactional
    void getAllAppUsersByPermissionIsEqualToSomething() throws Exception {
        Permission permission;
        if (TestUtil.findAll(em, Permission.class).isEmpty()) {
            appUserRepository.saveAndFlush(appUser);
            permission = PermissionResourceIT.createEntity();
        } else {
            permission = TestUtil.findAll(em, Permission.class).get(0);
        }
        em.persist(permission);
        em.flush();
        appUser.addPermission(permission);
        appUserRepository.saveAndFlush(appUser);
        Long permissionId = permission.getId();
        // Get all the appUserList where permission equals to permissionId
        defaultAppUserShouldBeFound("permissionId.equals=" + permissionId);

        // Get all the appUserList where permission equals to (permissionId + 1)
        defaultAppUserShouldNotBeFound("permissionId.equals=" + (permissionId + 1));
    }

    private void defaultAppUserFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAppUserShouldBeFound(shouldBeFound);
        defaultAppUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].birthPlace").value(hasItem(DEFAULT_BIRTH_PLACE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].disabled").value(hasItem(DEFAULT_DISABLED)))
            .andExpect(jsonPath("$.[*].disabledDate").value(hasItem(DEFAULT_DISABLED_DATE.toString())));

        // Check, that the count call also returns 1
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppUserShouldNotBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .type(UPDATED_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .gender(UPDATED_GENDER)
            .disabled(UPDATED_DISABLED)
            .disabledDate(UPDATED_DISABLED_DATE);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppUserToMatchAllProperties(updatedAppUser);
    }

    @Test
    @Transactional
    void putNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .type(UPDATED_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .gender(UPDATED_GENDER)
            .disabled(UPDATED_DISABLED)
            .disabledDate(UPDATED_DISABLED_DATE);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAppUser, appUser), getPersistedAppUser(appUser));
    }

    @Test
    @Transactional
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .type(UPDATED_TYPE)
            .birthDate(UPDATED_BIRTH_DATE)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .gender(UPDATED_GENDER)
            .disabled(UPDATED_DISABLED)
            .disabledDate(UPDATED_DISABLED_DATE);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(partialUpdatedAppUser, getPersistedAppUser(partialUpdatedAppUser));
    }

    @Test
    @Transactional
    void patchNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.saveAndFlush(appUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appUser
        restAppUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, appUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appUserRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AppUser getPersistedAppUser(AppUser appUser) {
        return appUserRepository.findById(appUser.getId()).orElseThrow();
    }

    protected void assertPersistedAppUserToMatchAllProperties(AppUser expectedAppUser) {
        assertAppUserAllPropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }

    protected void assertPersistedAppUserToMatchUpdatableProperties(AppUser expectedAppUser) {
        assertAppUserAllUpdatablePropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }
}
