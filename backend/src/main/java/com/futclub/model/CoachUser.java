package com.futclub.model;

import com.futclub.model.enums.UserRole;

/**
 * Domain representation of a coach account.
 */
public class CoachUser extends User {

    public CoachUser() {
        super(UserRole.COACH);
    }
}
