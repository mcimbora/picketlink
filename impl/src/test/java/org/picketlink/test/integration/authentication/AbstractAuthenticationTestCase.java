/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketlink.test.integration.authentication;

import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.picketlink.Identity;
import org.picketlink.authentication.internal.IdmAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.sample.User;
import org.picketlink.test.integration.AbstractArquillianTestCase;
import static org.picketlink.idm.model.sample.SampleModel.getUser;

/**
 * <p>
 * Base class for test cases that requires authentication. By default, the {@link IdmAuthenticator} is used.
 * </p>
 * 
 * @author Pedro Igor
 * 
 */
public abstract class AbstractAuthenticationTestCase extends AbstractArquillianTestCase {

    protected static final String USER_NAME = "john";
    protected static final String USER_PASSWORD = "mypasswd";

    @Inject
    private Identity identity;

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private IdentityManager identityManager;

    private Account currentAccount;

    @Before
    public void onSetup() {
        this.currentAccount = getUser(this.identityManager, USER_NAME);

        if (this.currentAccount == null) {
            this.currentAccount = new User(USER_NAME);
            this.identityManager.add(this.currentAccount);
        }

        this.currentAccount.setEnabled(true);

        this.identityManager.update(this.currentAccount);

        Password password = new Password(USER_PASSWORD);

        this.identityManager.updateCredential(this.currentAccount, password);
    }

    @After
    public void onFinish() {
        this.identity.logout();
    }

    protected Account getCurrentAccount() {
        return this.currentAccount;
    }

    protected Identity getIdentity() {
        return this.identity;
    }

    protected DefaultLoginCredentials getCredentials() {
        return this.credentials;
    }

    protected IdentityManager getIdentityManager() {
        return this.identityManager;
    }
}
