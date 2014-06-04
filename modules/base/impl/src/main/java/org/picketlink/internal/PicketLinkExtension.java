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
package org.picketlink.internal;

import org.picketlink.Identity;
import org.picketlink.SecurityConfigurationEvent;
import org.picketlink.config.SecurityConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

/**
 * <p>{@link javax.enterprise.inject.spi.Extension} responsible for:</p>
 *
 * <ul>
 *     <li>Fire a {@link org.picketlink.SecurityConfigurationEvent} to allow the application provide its own configuration.</li>
 *     <li>Register a {@link org.picketlink.internal.IdentityBeanDefinition} to properly manage the {@link org.picketlink.Identity} bean
 *     behavior.</li>
 * </ul>
 *
 * @author Pedro Igor
 */
@ApplicationScoped
public class PicketLinkExtension implements Extension {

    private SecurityConfiguration securityConfiguration;

    public SecurityConfiguration getSecurityConfiguration() {
        return this.securityConfiguration;
    }

    /**
     * <p>Veto all {@link org.picketlink.Identity} implementations.</p>
     *
     * <p>This is necessary in order to proper install the {@link org.picketlink.internal.IdentityBeanDefinition},
     * which is responsible for defining those beans.</p>
     *
     * @param pat
     * @param <T>
     */
    <T> void vetoIdentityImplementations(@Observes ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        Class<T> javaClass = annotatedType.getJavaClass();

        if (!Identity.class.equals(javaClass) && Identity.class.isAssignableFrom(javaClass)) {
            pat.veto();
        }
    }

    /**
     * <p>Initializes the PicketLink configuration.</p>
     *
     * @param abd
     * @param beanManager
     */
    void initializeConfiguration(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        SecurityConfigurationEvent securityConfigurationEvent = new SecurityConfigurationEvent();

        beanManager.fireEvent(securityConfigurationEvent);

        this.securityConfiguration = securityConfigurationEvent.getBuilder().build();

        abd.addBean(new IdentityBeanDefinition(this.securityConfiguration, beanManager));
    }

}