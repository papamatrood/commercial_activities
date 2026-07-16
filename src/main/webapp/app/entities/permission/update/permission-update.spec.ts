import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { IPermission } from '../permission.model';
import { PermissionService } from '../service/permission.service';

import { PermissionFormService } from './permission-form.service';
import { PermissionUpdate } from './permission-update';

describe('Permission Management Update Component', () => {
  let comp: PermissionUpdate;
  let fixture: ComponentFixture<PermissionUpdate>;
  let activatedRoute: ActivatedRoute;
  let permissionFormService: PermissionFormService;
  let permissionService: PermissionService;
  let appUserService: AppUserService;

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

    fixture = TestBed.createComponent(PermissionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    permissionFormService = TestBed.inject(PermissionFormService);
    permissionService = TestBed.inject(PermissionService);
    appUserService = TestBed.inject(AppUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call AppUser query and add missing value', () => {
      const permission: IPermission = { id: 17103 };
      const appUsers: IAppUser[] = [{ id: 14418 }];
      permission.appUsers = appUsers;

      const appUserCollection: IAppUser[] = [{ id: 14418 }];
      vitest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
      const additionalAppUsers = [...appUsers];
      const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
      vitest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      expect(appUserService.query).toHaveBeenCalled();
      expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(
        appUserCollection,
        ...additionalAppUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.appUsersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const permission: IPermission = { id: 17103 };
      const appUser: IAppUser = { id: 14418 };
      permission.appUsers = [appUser];

      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      expect(comp.appUsersSharedCollection()).toContainEqual(appUser);
      expect(comp.permission).toEqual(permission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPermission>();
      const permission = { id: 19932 };
      vitest.spyOn(permissionFormService, 'getPermission').mockReturnValue(permission);
      vitest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(permission);
      saveSubject.complete();

      // THEN
      expect(permissionFormService.getPermission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(permissionService.update).toHaveBeenCalledWith(expect.objectContaining(permission));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPermission>();
      const permission = { id: 19932 };
      vitest.spyOn(permissionFormService, 'getPermission').mockReturnValue({ id: null });
      vitest.spyOn(permissionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(permission);
      saveSubject.complete();

      // THEN
      expect(permissionFormService.getPermission).toHaveBeenCalled();
      expect(permissionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPermission>();
      const permission = { id: 19932 };
      vitest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(permissionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareAppUser', () => {
      it('should forward to appUserService', () => {
        const entity = { id: 14418 };
        const entity2 = { id: 16679 };
        vitest.spyOn(appUserService, 'compareAppUser');
        comp.compareAppUser(entity, entity2);
        expect(appUserService.compareAppUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
