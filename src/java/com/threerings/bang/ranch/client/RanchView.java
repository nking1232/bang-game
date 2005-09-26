//
// $Id$

package com.threerings.bang.ranch.client;

import com.jme.renderer.ColorRGBA;

import com.jmex.bui.BButton;
import com.jmex.bui.BContainer;
import com.jmex.bui.BLabel;
import com.jmex.bui.BTabbedPane;
import com.jmex.bui.BWindow;
import com.jmex.bui.border.EmptyBorder;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.BorderLayout;
import com.jmex.bui.layout.GroupLayout;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.util.MessageBundle;

import com.threerings.bang.client.TownView;
import com.threerings.bang.client.WalletLabel;
import com.threerings.bang.data.BangCodes;
import com.threerings.bang.data.BigShotItem;
import com.threerings.bang.data.UnitConfig;
import com.threerings.bang.util.BangContext;

import java.util.EnumSet;

import static com.threerings.bang.Log.log;

/**
 * Displays the main ranch interface wherein a player's Big Shot units can
 * be inspected, new Big Shots can be browsed and purchased and normal
 * units can also be inspected.
 */
public class RanchView extends BWindow
    implements ActionListener, PlaceView
{
    public RanchView (BangContext ctx)
    {
        super(ctx.getLookAndFeel(), new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        _ctx = ctx;
        _ctx.getRenderer().setBackgroundColor(ColorRGBA.gray);
        _msgs = ctx.getMessageManager().getBundle("ranch");

        String townId = _ctx.getUserObject().townId;

        // we cover the whole screen
        setBounds(0, 0, ctx.getDisplay().getWidth(),
                  ctx.getDisplay().getHeight());

        // at the top we have a box that displays text and the unit inspector
        BContainer signcont = new BContainer(new BorderLayout(0, 0));
        add(signcont, BorderLayout.NORTH);

        // the sign displays text and unit details
        _sign = new SignView(ctx);
        _sign.setText(_msgs.get("m.welcome"));
        signcont.add(_sign, BorderLayout.CENTER);

        // center panel: tabbed view with...
        _tabs = new BTabbedPane();
        add(_tabs, BorderLayout.CENTER);

        // ...recruited big shots...
        _bigshots = new UnitPalette(ctx, _sign, 4);
        _bigshots.setUser(_ctx.getUserObject());
        _tabs.addTab(_msgs.get("t.bigshots"), _bigshots);

        // ...recruitable big shots...
        _recruits = new UnitPalette(ctx, _sign, 4);
        _recruits.setUnits(UnitConfig.getTownUnits(
                               townId, UnitConfig.Rank.BIGSHOT));
        _tabs.addTab(_msgs.get("t.recruits"), _recruits);

        // ...normal units...
        _units = new UnitPalette(ctx, _sign, 4);
        EnumSet<UnitConfig.Rank> ranks = EnumSet.of(
            UnitConfig.Rank.NORMAL, UnitConfig.Rank.SPECIAL);
        _units.setUnits(UnitConfig.getTownUnits(townId, ranks));
        _tabs.addTab(_msgs.get("t.units"), _units);

        // TODO: and special units?

        // add a row displaying our cash on hand and the back button
        BContainer bottom = new BContainer(GroupLayout.makeHStretch());
        add(bottom, BorderLayout.SOUTH);

        bottom.add(new BLabel(_msgs.get("m.cash_on_hand")), GroupLayout.FIXED);
        bottom.add(new WalletLabel(ctx));
        BButton btn;
        bottom.add(btn = new BButton(_msgs.get("m.back_to_town"), "back"),
                   GroupLayout.FIXED);
        btn.addListener(this);

//         // side panel: unit inspector, customize/recruit and back button
//         BContainer side = new BContainer(GroupLayout.makeVStretch());
// //         side.add(_sign, GroupLayout.FIXED);
//         side.add(_uaction = new BButton(_msgs.get("m.recruit"), "recruit"),
//                  GroupLayout.FIXED);
//         _uaction.addListener(this);
//         side.add(new BLabel("")); // absorb space
//         add(side, BorderLayout.EAST);

//         add(_status = new BLabel(""), BorderLayout.SOUTH);
    }

    // documentation inherited from interface ActionListener
    public void actionPerformed (ActionEvent event)
    {
        if ("back".equals(event.getAction())) {
            _ctx.clearPlaceView(this);

        } else if ("recruit".equals(event.getAction())) {
            UnitConfig config = _sign.getConfig();
            // TODO: disable the button when this is not the case
            if (config != null && _sign.getItemId() == -1 &&
                config.rank == UnitConfig.Rank.BIGSHOT) {
                recruit(config);
            }
        }
    }

    // documentation inherited from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
        // this is never actually called; the ranch is not a real place
    }

    // documentation inherited from interface PlaceView
    public void didLeavePlace (PlaceObject plobj)
    {
        // this is never actually called; the ranch is not a real place
    }

    @Override // documentation inherited
    protected void wasRemoved ()
    {
        super.wasRemoved();

        // shut down our palettes
        _bigshots.shutdown();
        _units.shutdown();
        _recruits.shutdown();
    }

    protected void recruit (UnitConfig config)
    {
        RanchService rsvc = (RanchService)
            _ctx.getClient().requireService(RanchService.class);
        RanchService.ResultListener rl = new RanchService.ResultListener() {
            public void requestProcessed (Object result) {
                _sign.setText(_msgs.get("m.recruited_bigshot"));
                _tabs.selectTab(0);
                BigShotItem unit = (BigShotItem)result;
                _bigshots.selectUnit(unit.getItemId());
            }
            public void requestFailed (String cause) {
                _sign.setText(_msgs.xlate(cause));
            }
        };
        rsvc.recruitBigShot(_ctx.getClient(), config.type, rl);
    }

    protected BangContext _ctx;
    protected MessageBundle _msgs;

    protected SignView _sign;

    protected BTabbedPane _tabs;
    protected UnitPalette _bigshots, _units, _recruits;
    protected BButton _uaction;
}
