import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IAppUser } from '../app-user.model';
import { AppUserService } from '../service/app-user.service';

import { AppUserFormService } from './app-user-form.service';
import { AppUserUpdate } from './app-user-update';

describe('AppUser Management Update Component', () => {
  let comp: AppUserUpdate;
  let fixture: ComponentFixture<AppUserUpdate>;
  let activatedRoute: ActivatedRoute;
  let appUserFormService: AppUserFormService;
  let appUserService: AppUserService;
  let userService: UserService;
  let companyService: CompanyService;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(AppUserUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appUserFormService = TestBed.inject(AppUserFormService);
    appUserService = TestBed.inject(AppUserService);
    userService = TestBed.inject(UserService);
    companyService = TestBed.inject(CompanyService);
    permissionService = TestBed.inject(PermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const appUser: IAppUser = { id: 16679 };
      const user: IUser = { id: 3944 };
      appUser.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Company query and add missing value', () => {
      const appUser: IAppUser = { id: 16679 };
      const company: ICompany = { id: 29751 };
      appUser.company = company;

      const companyCollection: ICompany[] = [{ id: 29751 }];
      vitest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
      const additionalCompanies = [company];
      const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
      vitest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(companyService.query).toHaveBeenCalled();
      expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(
        companyCollection,
        ...additionalCompanies.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.companiesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Permission query and add missing value', () => {
      const appUser: IAppUser = { id: 16679 };
      const permissions: IPermission[] = [{ id: 19932 }];
      appUser.permissions = permissions;

      const permissionCollection: IPermission[] = [{ id: 19932 }];
      vitest.spyOn(permissionService, 'query').mockReturnValue(of(new HttpResponse({ body: permissionCollection })));
      const additionalPermissions = [...permissions];
      const expectedCollection: IPermission[] = [...additionalPermissions, ...permissionCollection];
      vitest.spyOn(permissionService, 'addPermissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(permissionService.query).toHaveBeenCalled();
      expect(permissionService.addPermissionToCollectionIfMissing).toHaveBeenCalledWith(
        permissionCollection,
        ...additionalPermissions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.permissionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const appUser: IAppUser = { id: 16679 };
      const user: IUser = { id: 3944 };
      appUser.user = user;
      const company: ICompany = { id: 29751 };
      appUser.company = company;
      const permission: IPermission = { id: 19932 };
      appUser.permissions = [permission];

      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.companiesSharedCollection()).toContainEqual(company);
      expect(comp.permissionsSharedCollection()).toContainEqual(permission);
      expect(comp.appUser).toEqual(appUser);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAppUser>();
      const appUser = { id: 14418 };
      vitest.spyOn(appUserFormService, 'getAppUser').mockReturnValue(appUser);
      vitest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(appUser);
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(appUserService.update).toHaveBeenCalledWith(expect.objectContaining(appUser));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAppUser>();
      const appUser = { id: 14418 };
      vitest.spyOn(appUserFormService, 'getAppUser').mockReturnValue({ id: null });
      vitest.spyOn(appUserService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(appUser);
      saveSubject.complete();

      // THEN
      expect(appUserFormService.getAppUser).toHaveBeenCalled();
      expect(appUserService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAppUser>();
      const appUser = { id: 14418 };
      vitest.spyOn(appUserService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appUserService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCompany', () => {
      it('should forward to companyService', () => {
        const entity = { id: 29751 };
        const entity2 = { id: 7586 };
        vitest.spyOn(companyService, 'compareCompany');
        comp.compareCompany(entity, entity2);
        expect(companyService.compareCompany).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePermission', () => {
      it('should forward to permissionService', () => {
        const entity = { id: 19932 };
        const entity2 = { id: 17103 };
        vitest.spyOn(permissionService, 'comparePermission');
        comp.comparePermission(entity, entity2);
        expect(permissionService.comparePermission).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
