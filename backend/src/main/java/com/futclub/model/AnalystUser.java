package com.futclub.model;

import com.futclub.model.enums.UserRole;

/**
 * Domain representation of an analyst account.
 */
public class AnalystUser extends User {

    public AnalystUser() {
        super(UserRole.ANALYST);
    }
}
