//
// $Id$

package com.threerings.bang.data;

import com.threerings.crowd.data.PlaceConfig;

import com.threerings.bang.client.LobbyController;

/**
 * Defines the configuration for the Bang! lobby.
 */
public class LobbyConfig extends PlaceConfig
{
    // documentation inherited
    public Class getControllerClass ()
    {
        return LobbyController.class;
    }

    // documentation inherited
    public String getManagerClassName ()
    {
        return "com.threerings.bang.server.LobbyManager";
    }
}
