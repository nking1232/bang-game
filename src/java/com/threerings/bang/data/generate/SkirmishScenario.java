//
// $Id$

package com.threerings.bang.data.generate;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.threerings.util.RandomUtil;

import com.threerings.bang.data.BangBoard;
import com.threerings.bang.data.BangConfig;
import com.threerings.bang.data.piece.Piece;
import com.threerings.bang.data.piece.StartMarker;
import com.threerings.bang.util.PieceSet;

/**
 * Generates the pieces for our test skirmish scenario.
 */
public class SkirmishScenario extends ScenarioGenerator
{
    public SkirmishScenario (PieceSet pieces)
    {
        _pieces = pieces;
    }

    @Override // documentation inherited
    public void generate (
        BangConfig config, BangBoard board, ArrayList<Piece> pieces)
    {
        ArrayList<Piece> markers = new ArrayList<Piece>();
        // extract and remove all player start markers
        for (Iterator<Piece> iter = pieces.iterator(); iter.hasNext(); ) {
            Piece p = iter.next();
            if (p instanceof StartMarker) {
                markers.add(p);
                iter.remove();
            }
        }

        // if we lack sufficient numbers, create some random ones
        for (int ii = markers.size(); ii < config.players.length; ii++) {
            StartMarker p = new StartMarker();
            p.x = (short)RandomUtil.getInt(board.getWidth());
            p.y = (short)RandomUtil.getInt(board.getHeight());
            markers.add(p);
        }

        // now shuffle the markers and assign the players to their location
        Collections.shuffle(markers);

        // each player starts in their own corner
        for (int ii = 0; ii < config.players.length; ii++) {
            Piece p = markers.remove(0);
            placePlayer(board, pieces, ii, p.x, p.y);
        }
    }

    protected void placePlayer (
        BangBoard board, ArrayList<Piece> pieces, int pidx, int sx, int sy)
    {
        // filter out this players pieces
        ArrayList<Piece> placers = new ArrayList<Piece>();
        for (Iterator<Piece> iter = _pieces.values().iterator();
             iter.hasNext(); ) {
            Piece p = iter.next();
            if (p.owner == pidx) {
                p.assignPieceId();
                placers.add(p);
                iter.remove();
            }
        }

        // search out from the starting location for a valid position
        Piece piece = placers.remove(0);
        Rectangle rect = new Rectangle(sx, sy, 1, 1);
        for (int gg = 0; gg < 10; gg++) {
            int xx, yy, lx, ly;

            xx = rect.x;
            for (yy = rect.y, ly = rect.y + rect.height; yy < ly; yy++) {
                if (!tryPlacement(board, pieces, xx, yy, piece)) {
                    continue;
                }
                if (placers.size() == 0) {
                    return;
                } else {
                    piece = placers.remove(0);
                }
            }

            xx = rect.x + rect.width - 1;
            for (yy = rect.y, ly = rect.y + rect.height; yy < ly; yy++) {
                if (!tryPlacement(board, pieces, xx, yy, piece)) {
                    continue;
                }
                if (placers.size() == 0) {
                    return;
                } else {
                    piece = placers.remove(0);
                }
            }

            yy = rect.y;
            for (xx = rect.x + 1, lx = rect.x + rect.width - 2; xx < lx; xx++) {
                if (!tryPlacement(board, pieces, xx, yy, piece)) {
                    continue;
                }
                if (placers.size() == 0) {
                    return;
                } else {
                    piece = placers.remove(0);
                }
            }

            yy = rect.y + rect.height - 1;
            for (xx = rect.x + 1, lx = rect.x + rect.width - 2; xx < lx; xx++) {
                if (!tryPlacement(board, pieces, xx, yy, piece)) {
                    continue;
                }
                if (placers.size() == 0) {
                    return;
                } else {
                    piece = placers.remove(0);
                }
            }

            rect.grow(1, 1);
        }
    }

    protected PieceSet _pieces;
}
