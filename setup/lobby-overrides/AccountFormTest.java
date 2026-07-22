package eu.kartoffelquadrat.ls.accountmanager.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Tests for the deployment's intentionally simple password policy. */
public class AccountFormTest {

    /** Any non-empty password is accepted. */
    @Test
    public void acceptsAnyNonEmptyPassword() {
        assertTrue(AccountForm.validatePasswordString("123456"));
        assertTrue(AccountForm.validatePasswordString("a"));
        assertTrue(AccountForm.validatePasswordString(" "));
        assertTrue(AccountForm.validatePasswordString("任意密码"));
    }

    /** Empty or missing passwords are rejected. */
    @Test
    public void rejectsEmptyPassword() {
        assertFalse(AccountForm.validatePasswordString(""));
        assertFalse(AccountForm.validatePasswordString(null));
    }
}
