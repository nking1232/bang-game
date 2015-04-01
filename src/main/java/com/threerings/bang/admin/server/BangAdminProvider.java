//
// $Id$

package com.threerings.bang.admin.server;

import com.threerings.bang.admin.client.BangAdminService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationProvider;

/**
 * Defines the server-side of the {@link BangAdminService}.
 */
public interface BangAdminProvider extends InvocationProvider
{
    /**
     * Handles a {@link BangAdminService#scheduleReboot} request.
     */
    void scheduleReboot (ClientObject caller, int arg1);
}