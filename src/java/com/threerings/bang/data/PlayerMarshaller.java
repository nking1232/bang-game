//
// $Id$

package com.threerings.bang.data;

import com.threerings.bang.client.PlayerService;
import com.threerings.bang.data.Handle;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.dobj.InvocationResponseEvent;
import com.threerings.util.Name;

/**
 * Provides the implementation of the {@link PlayerService} interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
public class PlayerMarshaller extends InvocationMarshaller
    implements PlayerService
{
    /** The method id used to dispatch {@link #getPosterInfo} requests. */
    public static final int GET_POSTER_INFO = 1;

    // documentation inherited from interface
    public void getPosterInfo (Client arg1, Handle arg2, InvocationService.ResultListener arg3)
    {
        InvocationMarshaller.ResultMarshaller listener3 = new InvocationMarshaller.ResultMarshaller();
        listener3.listener = arg3;
        sendRequest(arg1, GET_POSTER_INFO, new Object[] {
            arg2, listener3
        });
    }

    /** The method id used to dispatch {@link #invitePardner} requests. */
    public static final int INVITE_PARDNER = 2;

    // documentation inherited from interface
    public void invitePardner (Client arg1, Handle arg2, String arg3, InvocationService.ConfirmListener arg4)
    {
        InvocationMarshaller.ConfirmMarshaller listener4 = new InvocationMarshaller.ConfirmMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, INVITE_PARDNER, new Object[] {
            arg2, arg3, listener4
        });
    }

    /** The method id used to dispatch {@link #noteFolk} requests. */
    public static final int NOTE_FOLK = 3;

    // documentation inherited from interface
    public void noteFolk (Client arg1, int arg2, int arg3, InvocationService.ConfirmListener arg4)
    {
        InvocationMarshaller.ConfirmMarshaller listener4 = new InvocationMarshaller.ConfirmMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, NOTE_FOLK, new Object[] {
            Integer.valueOf(arg2), Integer.valueOf(arg3), listener4
        });
    }

    /** The method id used to dispatch {@link #pickFirstBigShot} requests. */
    public static final int PICK_FIRST_BIG_SHOT = 4;

    // documentation inherited from interface
    public void pickFirstBigShot (Client arg1, String arg2, Name arg3, InvocationService.ConfirmListener arg4)
    {
        InvocationMarshaller.ConfirmMarshaller listener4 = new InvocationMarshaller.ConfirmMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, PICK_FIRST_BIG_SHOT, new Object[] {
            arg2, arg3, listener4
        });
    }

    /** The method id used to dispatch {@link #playComputer} requests. */
    public static final int PLAY_COMPUTER = 5;

    // documentation inherited from interface
    public void playComputer (Client arg1, int arg2, String[] arg3, String arg4, boolean arg5, InvocationService.InvocationListener arg6)
    {
        ListenerMarshaller listener6 = new ListenerMarshaller();
        listener6.listener = arg6;
        sendRequest(arg1, PLAY_COMPUTER, new Object[] {
            Integer.valueOf(arg2), arg3, arg4, Boolean.valueOf(arg5), listener6
        });
    }

    /** The method id used to dispatch {@link #playPractice} requests. */
    public static final int PLAY_PRACTICE = 6;

    // documentation inherited from interface
    public void playPractice (Client arg1, String arg2, InvocationService.InvocationListener arg3)
    {
        ListenerMarshaller listener3 = new ListenerMarshaller();
        listener3.listener = arg3;
        sendRequest(arg1, PLAY_PRACTICE, new Object[] {
            arg2, listener3
        });
    }

    /** The method id used to dispatch {@link #playTutorial} requests. */
    public static final int PLAY_TUTORIAL = 7;

    // documentation inherited from interface
    public void playTutorial (Client arg1, String arg2, InvocationService.InvocationListener arg3)
    {
        ListenerMarshaller listener3 = new ListenerMarshaller();
        listener3.listener = arg3;
        sendRequest(arg1, PLAY_TUTORIAL, new Object[] {
            arg2, listener3
        });
    }

    /** The method id used to dispatch {@link #removePardner} requests. */
    public static final int REMOVE_PARDNER = 8;

    // documentation inherited from interface
    public void removePardner (Client arg1, Handle arg2, InvocationService.ConfirmListener arg3)
    {
        InvocationMarshaller.ConfirmMarshaller listener3 = new InvocationMarshaller.ConfirmMarshaller();
        listener3.listener = arg3;
        sendRequest(arg1, REMOVE_PARDNER, new Object[] {
            arg2, listener3
        });
    }

    /** The method id used to dispatch {@link #respondToPardnerInvite} requests. */
    public static final int RESPOND_TO_PARDNER_INVITE = 9;

    // documentation inherited from interface
    public void respondToPardnerInvite (Client arg1, Handle arg2, boolean arg3, InvocationService.ConfirmListener arg4)
    {
        InvocationMarshaller.ConfirmMarshaller listener4 = new InvocationMarshaller.ConfirmMarshaller();
        listener4.listener = arg4;
        sendRequest(arg1, RESPOND_TO_PARDNER_INVITE, new Object[] {
            arg2, Boolean.valueOf(arg3), listener4
        });
    }

    /** The method id used to dispatch {@link #updatePosterInfo} requests. */
    public static final int UPDATE_POSTER_INFO = 10;

    // documentation inherited from interface
    public void updatePosterInfo (Client arg1, int arg2, String arg3, int[] arg4, InvocationService.ConfirmListener arg5)
    {
        InvocationMarshaller.ConfirmMarshaller listener5 = new InvocationMarshaller.ConfirmMarshaller();
        listener5.listener = arg5;
        sendRequest(arg1, UPDATE_POSTER_INFO, new Object[] {
            Integer.valueOf(arg2), arg3, arg4, listener5
        });
    }

}
