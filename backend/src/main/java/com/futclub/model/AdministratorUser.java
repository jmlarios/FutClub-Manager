package com.futclub.model;

import com.futclub.model.enums.UserRole;

/**
 * Domain representation of an administrator account.
 */
public class AdministratorUser extends User {

    public AdministratorUser() {
        super(UserRole.ADMINISTRATOR);
    }
}
