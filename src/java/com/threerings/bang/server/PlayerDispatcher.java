//
// $Id$

package com.threerings.bang.server;

import com.threerings.bang.client.PlayerService;
import com.threerings.bang.data.Handle;
import com.threerings.bang.data.PlayerMarshaller;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.server.InvocationDispatcher;
import com.threerings.presents.server.InvocationException;
import com.threerings.util.Name;

/**
 * Dispatches requests to the {@link PlayerProvider}.
 */
public class PlayerDispatcher extends InvocationDispatcher
{
    /**
     * Creates a dispatcher that may be registered to dispatch invocation
     * service requests for the specified provider.
     */
    public PlayerDispatcher (PlayerProvider provider)
    {
        this.provider = provider;
    }

    // documentation inherited
    public InvocationMarshaller createMarshaller ()
    {
        return new PlayerMarshaller();
    }

    // documentation inherited
    public void dispatchRequest (
        ClientObject source, int methodId, Object[] args)
        throws InvocationException
    {
        switch (methodId) {
        case PlayerMarshaller.GET_POSTER_INFO:
            ((PlayerProvider)provider).getPosterInfo(
                source,
                (Handle)args[0], (InvocationService.ResultListener)args[1]
            );
            return;

        case PlayerMarshaller.INVITE_PARDNER:
            ((PlayerProvider)provider).invitePardner(
                source,
                (Handle)args[0], (String)args[1], (InvocationService.ConfirmListener)args[2]
            );
            return;

        case PlayerMarshaller.NOTE_FOLK:
            ((PlayerProvider)provider).noteFolk(
                source,
                ((Integer)args[0]).intValue(), ((Integer)args[1]).intValue(), (InvocationService.ConfirmListener)args[2]
            );
            return;

        case PlayerMarshaller.PICK_FIRST_BIG_SHOT:
            ((PlayerProvider)provider).pickFirstBigShot(
                source,
                (String)args[0], (Name)args[1], (InvocationService.ConfirmListener)args[2]
            );
            return;

        case PlayerMarshaller.PLAY_COMPUTER:
            ((PlayerProvider)provider).playComputer(
                source,
                ((Integer)args[0]).intValue(), (String[])args[1], (String)args[2], ((Boolean)args[3]).booleanValue(), (InvocationService.InvocationListener)args[4]
            );
            return;

        case PlayerMarshaller.PLAY_PRACTICE:
            ((PlayerProvider)provider).playPractice(
                source,
                (String)args[0], (InvocationService.InvocationListener)args[1]
            );
            return;

        case PlayerMarshaller.PLAY_TUTORIAL:
            ((PlayerProvider)provider).playTutorial(
                source,
                (String)args[0], (InvocationService.InvocationListener)args[1]
            );
            return;

        case PlayerMarshaller.REMOVE_PARDNER:
            ((PlayerProvider)provider).removePardner(
                source,
                (Handle)args[0], (InvocationService.ConfirmListener)args[1]
            );
            return;

        case PlayerMarshaller.RESPOND_TO_PARDNER_INVITE:
            ((PlayerProvider)provider).respondToPardnerInvite(
                source,
                (Handle)args[0], ((Boolean)args[1]).booleanValue(), (InvocationService.ConfirmListener)args[2]
            );
            return;

        case PlayerMarshaller.UPDATE_POSTER_INFO:
            ((PlayerProvider)provider).updatePosterInfo(
                source,
                ((Integer)args[0]).intValue(), (String)args[1], (int[])args[2], (InvocationService.ConfirmListener)args[3]
            );
            return;

        default:
            super.dispatchRequest(source, methodId, args);
            return;
        }
    }
}
