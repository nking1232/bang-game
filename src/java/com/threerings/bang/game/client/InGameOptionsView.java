//
// $Id$

package com.threerings.bang.game.client;

import com.jmex.bui.BButton;
import com.jmex.bui.BContainer;
import com.jmex.bui.BDecoratedWindow;
import com.jmex.bui.BLabel;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.util.Dimension;

import com.threerings.util.MessageBundle;

import com.threerings.bang.client.OptionsView;
import com.threerings.bang.client.bui.TabbedPane;
import com.threerings.bang.data.BangBootstrapData;
import com.threerings.bang.data.BangCodes;
import com.threerings.bang.game.data.BangConfig;
import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.GameCodes;
import com.threerings.bang.util.BangContext;

/**
 * Displays options while a player is in a game.
 */
public class InGameOptionsView extends BDecoratedWindow
    implements ActionListener
{
    public InGameOptionsView (BangContext ctx, BangObject bangobj, BangConfig config)
    {
        super(ctx.getStyleSheet(), null);
        setLayer(2);
        ((GroupLayout)getLayoutManager()).setGap(15);

        _modal = true;
        _ctx = ctx;
        _bangobj = bangobj;

        MessageBundle msgs = ctx.getMessageManager().getBundle(
            BangCodes.OPTS_MSGS);
        add(_title = new BLabel("", "window_title"), GroupLayout.FIXED);

        TabbedPane tabs = new TabbedPane(false);
        tabs.setPreferredSize(new Dimension(375, 275));
        add(tabs, GroupLayout.FIXED);

        // create the Help tab
        BContainer cont = GroupLayout.makeHBox(GroupLayout.CENTER);
        cont.setStyleClass("options_tab");
        cont.add(new BLabel(msgs.get("m.game_key_help")));
        tabs.addTab(msgs.get("t.help"), cont);

        // create the General tab
        TableLayout layout = new TableLayout(2, 10, 10);
        layout.setHorizontalAlignment(TableLayout.CENTER);
        layout.setVerticalAlignment(TableLayout.CENTER);
        cont = new BContainer(layout);
        cont.setStyleClass("options_tab");

        cont.add(new BLabel(msgs.get("m.music_vol"), "right_label"));
        cont.add(OptionsView.createSoundSlider(
                     ctx, OptionsView.SoundType.MUSIC));
        cont.add(new BLabel(msgs.get("m.effects_vol"), "right_label"));
        cont.add(OptionsView.createSoundSlider(
                     ctx, OptionsView.SoundType.EFFECTS));

        tabs.addTab(msgs.get("t.general"), cont);

        BContainer box = GroupLayout.makeHBox(GroupLayout.CENTER);
        if (config.type != BangConfig.Type.TUTORIAL && 
                config.duration != BangConfig.Duration.PRACTICE) {
            box.add(new BButton(
                        msgs.get("m.to_" + _ctx.getBangClient().getPriorLocationIdent()),
                        this, "to_prior"));
        }
        box.add(new BButton(msgs.get("m.to_town"), this, "to_town"));
        box.add(new BButton(msgs.get("m.resume"), this, "dismiss"));
        add(box, GroupLayout.FIXED);
    }

    // documentation inherited from interface ActionListener
    public void actionPerformed (ActionEvent event)
    {
        String action = event.getAction();
        if ("to_town".equals(action)) {
            if (_ctx.getLocationDirector().leavePlace()) {
                _ctx.getBangClient().clearPopup(this, true);
                _ctx.getBangClient().showTownView();
            }

        } else if ("to_prior".equals(action)) {
            if (_ctx.getLocationDirector().moveTo(
                        _ctx.getBangClient().getPriorLocationOid())) {
                _ctx.getBangClient().clearPopup(this, true);
            }

        } else if ("dismiss".equals(action)) {
            _ctx.getBangClient().clearPopup(this, true);
        }
    }

    @Override // documentation inherited
    protected void wasAdded ()
    {
        super.wasAdded();

        // update our title with the current board info
        if (_bangobj != null && _bangobj.scenario != null) {
            String scen = _ctx.xlate(
                GameCodes.GAME_MSGS, _bangobj.scenario.getName());
            _title.setText(_bangobj.boardName + " - " + scen);
        }
    }

    protected BangContext _ctx;
    protected BangObject _bangobj;
    protected BLabel _title;
}
