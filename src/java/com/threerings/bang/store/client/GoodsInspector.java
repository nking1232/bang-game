//
// $Id$

package com.threerings.bang.store.client;

import com.jmex.bui.BButton;
import com.jmex.bui.BContainer;
import com.jmex.bui.BLabel;
import com.jmex.bui.BTextArea;
import com.jmex.bui.BlankIcon;
import com.jmex.bui.ImageIcon;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.GroupLayout;

import com.threerings.bang.client.BangUI;
import com.threerings.bang.client.MoneyLabel;
import com.threerings.bang.client.bui.IconPalette;
import com.threerings.bang.client.bui.SelectableIcon;
import com.threerings.bang.data.BangCodes;
import com.threerings.bang.util.BangContext;

import com.threerings.bang.avatar.client.ColorSelector;
import com.threerings.bang.avatar.util.ArticleCatalog;
import com.threerings.bang.avatar.util.AvatarMetrics;

import com.threerings.bang.store.data.ArticleGood;
import com.threerings.bang.store.data.Good;
import com.threerings.bang.store.data.StoreObject;

/**
 * Displays detailed information on a particular good.
 */
public class GoodsInspector extends BContainer
    implements IconPalette.Inspector, ActionListener
{
    public GoodsInspector (BangContext ctx, StoreView parent, BTextArea status)
    {
        _ctx = ctx;
        _parent = parent;
        _status = status;

        setLayoutManager(GroupLayout.makeHoriz(GroupLayout.LEFT));
        add(_icon = new BLabel(""));

        BContainer vert;
        add(vert = new BContainer(GroupLayout.makeVStretch()));

        vert.add(_title = new BLabel(""));
        _title.setLookAndFeel(BangUI.dtitleLNF);
        vert.add(_descrip = new BLabel(""));
        vert.add(_icont = GroupLayout.makeHBox(GroupLayout.LEFT));

        // this is only used for article goods
        _icont.add(_colors = GroupLayout.makeHBox(GroupLayout.CENTER));

        _icont.add(_cost = new MoneyLabel(ctx));
        _cost.setMoney(0, 0, false);
        _icont.add(new BLabel(new BlankIcon(25, 10))); // spacer
        BButton buy;
        _icont.add(buy = new BButton(_ctx.xlate("store", "m.buy")));
        buy.addListener(this);
    }

    /**
     * Gives us access to our store object when it is available.
     */
    public void init (StoreObject stobj)
    {
        _stobj = stobj;
    }

    // documentation inherited from interface IconPalette.Inspector
    public void iconSelected (SelectableIcon icon)
    {
        // clear out any old color selectors
        _colors.removeAll();

        _good = ((GoodsIcon)icon).getGood();
        _icon.setIcon(new ImageIcon(_ctx.loadImage(_good.getIconPath())));
        _title.setText(_ctx.xlate(BangCodes.GOODS_MSGS, _good.getName()));
        _descrip.setText(_ctx.xlate(BangCodes.GOODS_MSGS, _good.getTip()));
        _cost.setMoney(_good.getScripCost(), _good.getCoinCost(), false);

        // do some special jockeying to handle colorizations for articles
        if (_good instanceof ArticleGood) {
            ArticleCatalog.Article article =
                _ctx.getArticleCatalog().getArticle(_good.getType());
            String[] cclasses =
                _ctx.getAvatarMetrics().getColorizationClasses(article);
            _args[0] = _args[1] = _args[2] = Integer.valueOf(0);
            int index = 0;
            for (int ii = 0; ii < cclasses.length; ii++) {
                if (cclasses[ii].equals(AvatarMetrics.SKIN) ||
                    cclasses[ii].equals(AvatarMetrics.HAIR)) {
                    continue;
                }
                ColorSelector colorsel = new ColorSelector(_ctx, cclasses[ii]);
                colorsel.setProperty("index", Integer.valueOf(index));
                colorsel.addListener(_colorpal);
                _colors.add(colorsel);
                _args[index] = Integer.valueOf(colorsel.getSelectedColor());
                index++;
            }
        }
    }

    // documentation inherited from interface IconPalette.Inspector
    public void selectionCleared ()
    {
        // nada
    }

    // documentation inherited from interface ActionListener
    public void actionPerformed (ActionEvent event)
    {
        if (_good == null || _stobj == null) {
            return;
        }

        StoreService.ConfirmListener cl = new StoreService.ConfirmListener() {
            public void requestProcessed () {
                _status.setText(_ctx.xlate("store", "m.purchased"));
                _parent.goodPurchased();
            }
            public void requestFailed (String cause) {
                _status.setText(_ctx.xlate("store", cause));
            }
        };
        _stobj.service.buyGood(_ctx.getClient(), _good.getType(), _args, cl);
    }

    protected ActionListener _colorpal = new ActionListener() {
        public void actionPerformed (ActionEvent event) {
            ColorSelector colorsel = (ColorSelector)event.getSource();
            int index = (Integer)colorsel.getProperty("index");
            _args[index] = Integer.valueOf(colorsel.getSelectedColor());
            // TODO: recolor the icon image
            return;
        }
    };

    protected BangContext _ctx;
    protected StoreObject _stobj;
    protected Good _good;

    protected BLabel _icon, _title, _descrip;
    protected BContainer _icont, _colors;
    protected Object[] _args = new Object[3];
    protected MoneyLabel _cost;

    protected StoreView _parent;
    protected BTextArea _status;
}
