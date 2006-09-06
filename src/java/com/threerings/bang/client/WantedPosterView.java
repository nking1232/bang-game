//
// $Id$

package com.threerings.bang.client;

import java.util.Map;

import com.jmex.bui.BButton;
import com.jmex.bui.BComponent;
import com.jmex.bui.BContainer;
import com.jmex.bui.BImage;
import com.jmex.bui.BLabel;
import com.jmex.bui.BWindow;
import com.jmex.bui.Spacer;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.AbsoluteLayout;
import com.jmex.bui.layout.TableLayout;
import com.jmex.bui.layout.BLayoutManager;
import com.jmex.bui.layout.GroupLayout;
import com.jmex.bui.util.Dimension;
import com.jmex.bui.util.Point;
import com.jmex.bui.util.Rectangle;
import com.jme.renderer.Renderer;

import static com.threerings.bang.Log.log;

import com.threerings.bang.avatar.client.AvatarView;
import com.threerings.bang.client.bui.IconPalette;
import com.threerings.bang.data.Badge;
import com.threerings.bang.data.BangCodes;
import com.threerings.bang.data.Handle;
import com.threerings.bang.data.PosterInfo;
import com.threerings.bang.game.data.GameCodes;
import com.threerings.bang.game.data.scenario.ScenarioInfo;
import com.threerings.bang.util.BangContext;

import com.threerings.presents.client.InvocationService;

/**
 * Display a player's wanted poster.
*/
public class WantedPosterView extends BContainer
{
    /**
     * Creates a new wanted poster display popup for the given handle.
     */
    public static void displayWantedPoster (
        final BangContext ctx, final Handle handle)
    {
        // make sure we can display the popup currently
        if (!ctx.getBangClient().canDisplayPopup(
                MainView.Type.POSTER_DISPLAY)) {
            BangUI.play(BangUI.FeedbackSound.INVALID_ACTION);
            return;
        }

        // first create the popup
        BLayoutManager layout = GroupLayout.makeVert(
            GroupLayout.NONE, GroupLayout.BOTTOM, GroupLayout.EQUALIZE);
        final BWindow popup = new BWindow(ctx.getStyleSheet(), layout);
        popup.setStyleClass("poster_popup");

        // add the actual poster view
        WantedPosterView view = new WantedPosterView(ctx);
        view.setHandle(handle);
        popup.add(view);

        final BContainer buttonBox = GroupLayout.makeHBox(GroupLayout.CENTER);
        ((GroupLayout )buttonBox.getLayoutManager()).setGap(30);

        // add an edit button, if we're looking at ourselves
        if (handle.equals(ctx.getUserObject().handle)) {
            final BButton editButton = new BButton(
                ctx.xlate(BangCodes.BANG_MSGS, "m.poster_edit"));
            editButton.addListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent event) {
                        if (event.getSource() == editButton) {
                            ctx.getBangClient().clearPopup(popup, false);
                            EditPosterView.editWantedPoster(ctx, handle);
                        }
                    }
                });
            buttonBox.add(editButton);
        }

        // then a button that knows how to clear the popup
        final BButton backButton = new BButton(
            ctx.xlate(BangCodes.BANG_MSGS, "m.poster_dismiss"));
        backButton.addListener(new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                if (event.getSource() == backButton) {
                    ctx.getBangClient().clearPopup(popup, true);
                }
            }
        });
        buttonBox.add(backButton);
        popup.add(buttonBox, GroupLayout.FIXED);

        ctx.getBangClient().displayPopup(popup, true);
    }

    public WantedPosterView (BangContext ctx)
    {
        super(new AbsoluteLayout());

        // our preferred size is determined by the background image
        setStyleClass("poster_view");

        _ctx = ctx;
        _avatarSepia = _ctx.loadImage("ui/wanted/sepia_avatar.png");
    }

    @Override // from BContainer
    protected void wasAdded ()
    {
        super.wasAdded();
        _avatarSepia.reference();
    }

    @Override // from BContainer
    protected void wasRemoved ()
    {
        super.wasRemoved();
        _avatarSepia.release();
    }

    /**
     * Determines the poster to view through a handle, initiating a server
     * request for the {@link PosterInfo} object to use.
     */
    public void setHandle (Handle handle)
    {
        if (_handle != null && _handle.equals(handle)) {
            return;
        }
        _handle = handle;

        // request the poster record
        InvocationService.ResultListener listener =
            new InvocationService.ResultListener() {
                public void requestProcessed(Object result) {
                    setPoster((PosterInfo) result);
                }
                public void requestFailed(String cause) {
                    log.warning("Wanted poster request failed: " + cause);
                    _ctx.getChatDirector().displayFeedback(
                        BangCodes.BANG_MSGS, "m.poster_failed");
                }
            };

        PlayerService psvc = (PlayerService)
            _ctx.getClient().requireService(PlayerService.class);
        psvc.getPosterInfo(
            _ctx.getClient(), handle, listener);
    }

    /**
     * Determines the poster to view directly through a {@link PosterInfo}
     * object.
     */
    public void setPoster (PosterInfo poster)
    {
        if (_poster != null && _poster.equals(poster)) {
            return;
        }
        _poster = poster;
        buildPoster();
    }

    protected void buildPoster ()
    {
        removeAll();

        add(buildWantedLabel(), new Rectangle(310, 560, 320, 125));
        add(buildRankingsView(), new Rectangle(340, 250, 280, 260));
        add(buildAvatarView(), new Rectangle(56, 264, 244, 300));
        add(buildStatementView(), new Rectangle(50, 220, 250, 35));
        add(buildBadgeView(), new Point(57, 33));
    }

    protected BComponent buildWantedLabel ()
    {
        BContainer box = GroupLayout.makeVBox(GroupLayout.CENTER);
        box.setStyleClass("poster_handle_box");

        box.add(new BLabel(_poster.handle.toString(), "poster_handle"));

        // TODO: disabled until there are actually gangs
        String gang = " "; // = "Member of the \"DALTON GANG\"";
        box.add(new BLabel(gang, "poster_gang"));

        return box;
    }

    protected BComponent buildRankingsView ()
    {
        BContainer box = new BContainer(new TableLayout(2, 2, 10));
        box.setStyleClass("poster_rankings_box");

        Integer oaRank = _poster.rankings.get(ScenarioInfo.OVERALL_IDENT);
        if (oaRank != null) {
            String scenario = "m.scenario_" + ScenarioInfo.OVERALL_IDENT;
            scenario = _ctx.xlate(GameCodes.GAME_MSGS, scenario);
            addRankRow(box, scenario, oaRank.intValue());
            // add a spacer row
            box.add(new Spacer(1, 12));
            box.add(new Spacer(1, 12));
        }
        for (Map.Entry<String, Integer> row : _poster.rankings.entrySet()) {
            String scenarioId = row.getKey();
            if (ScenarioInfo.OVERALL_IDENT.equals(scenarioId)) {
                continue;
            }
            
            ScenarioInfo info = ScenarioInfo.getScenarioInfo(scenarioId);
            if (info == null) {
                log.warning("Unknown scenario id [id=" + scenarioId + "]");
                continue;
            }
            addRankRow(box, _ctx.xlate(GameCodes.GAME_MSGS, info.getName()),
                       row.getValue().intValue());
        }
        return box;
    }

    protected void addRankRow (BContainer box, String name, int rank)
    {
        BLabel scenario = new BLabel(name + ":", "poster_rank_scenario");
        scenario.setPreferredSize(new Dimension(160, 20));
        box.add(scenario);

        String rankStyle;
        switch(rank) {
        default:
        case 0: case 1:
            rankStyle = "low";
            break;
        case 2: case 3: case 4:
            rankStyle = "mid";
            break;
        case 5: case 6: case 7:
            rankStyle = "high";
            break;
        }
        String rankName = "m.poster_rank_" + (rank + 1);
        rankName = _ctx.xlate(BangCodes.BANG_MSGS, rankName);
        box.add(new BLabel(rankName, "poster_rank_standing_" + rankStyle));
    }

    protected BComponent buildAvatarView ()
    {
        // overlay the avatar with the sepia at a low alpha level
        AvatarView avatar = new AvatarView(_ctx, 2, false, false) {
            @Override // from BComponent
            protected void renderComponent(Renderer renderer) {
                super.renderComponent(renderer);
                _avatarSepia.render(renderer, 0, 0, _width, _height, 0.25f);
            }
        };
        avatar.setStyleClass("poster_avatar");
        if (_poster.avatar != null) {
            // TODO: should not happen, snapshots currently broken?
            avatar.setAvatar(_poster.avatar);
        }
        return avatar;
    }

    protected BComponent buildBadgeView ()
    {
        IconPalette palette = new IconPalette(
            null, PosterInfo.BADGES, 1, ItemIcon.ICON_SIZE, 0);
        palette.setShowNavigation(false);
        for (int badgeIx = 0; badgeIx < PosterInfo.BADGES; badgeIx ++) {
            int id = _poster.badgeIds[badgeIx];
            if (id != -1) {
                Badge badge = Badge.getType(id).newBadge();
                palette.addIcon(new ItemIcon(_ctx, badge));
            }
        }
        return palette;
    }

    protected BComponent buildStatementView ()
    {
        BContainer box = GroupLayout.makeVBox(GroupLayout.CENTER);
        box.setStyleClass("poster_statement_box");

        BLabel label = new BLabel(_poster.statement != null ?
                                  "\"" + _poster.statement + "\"" : "",
                                  "poster_statement");
        box.add(label);
        return box;
    }

    protected BangContext _ctx;

    /** The handle of the poster being either displayed or requested */
    protected Handle _handle;

    /** After successul request, the PosterInfo record */
    protected PosterInfo _poster;

    /** A pointer to the sepia overlay for the avatar view */
    protected BImage _avatarSepia;
}
