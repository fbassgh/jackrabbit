/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.rmi.client;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;

import org.apache.jackrabbit.rmi.remote.RemoteVersion;

/**
 * Local adapter for the JCR-RMI
 * {@link org.apache.jackrabbit.rmi.remote.RemoteVersion RemoteVersion}
 * interface. This class makes a remote version locally available using
 * the JCR {@link javax.jcr.version.Version Version} interface.
 *
 * @author Felix Meschberger
 * @see javax.jcr.version.Version
 * @see org.apache.jackrabbit.rmi.remote.RemoteVersion
 */
public class ClientVersion extends ClientNode implements Version {

    /** The adapted remote version. */
    private RemoteVersion remote;

    /**
     * Creates a local adapter for the given remote version.
     *
     * @param session current session
     * @param remote  remote version
     * @param factory local adapter factory
     */
    public ClientVersion(Session session, RemoteVersion remote,
        LocalAdapterFactory factory) {
        super(session, remote, factory);
        this.remote = remote;
    }

    /** {@inheritDoc} */
    public Calendar getCreated() throws RepositoryException {
        try {
            return remote.getCreated();
        } catch (RemoteException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }

    /** {@inheritDoc} */
    public Version[] getSuccessors() throws RepositoryException {
        try {
            return getVersionArray(getSession(), remote.getSuccessors());
        } catch (RemoteException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }

    /** {@inheritDoc} */
    public Version[] getPredecessors() throws RepositoryException {
        try {
            return getVersionArray(getSession(), remote.getPredecessors());
        } catch (RemoteException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }
}