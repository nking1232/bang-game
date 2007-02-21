//
// $Id$

package com.threerings.bang.game.data.effect;

import com.jmex.bui.util.Point;
import com.threerings.util.StreamablePoint;
import com.threerings.bang.util.RenderUtil;

import com.samskivert.util.ArrayUtil;
import com.samskivert.util.ArrayIntSet;
import com.samskivert.util.IntIntMap;

import com.threerings.bang.game.client.RocketHandler;
import com.threerings.bang.game.client.EffectHandler;

import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.piece.Piece;

import static com.threerings.bang.Log.log;

/**
 * Communicates that a ballistic shot was fired from one piece to another.
 */
public class RocketEffect extends AreaEffect
{
    /** The shooter. */
    public Piece shooter;

    /** Amount of damage being applied. */
    public int baseDamage;

    /** The x coordinates of the path this shot takes before finally
     * arriving at its target (not including the starting coordinate). */
    public short[] xcoords = new short[0];

    /** The y coordinates of the path this shot takes before finally
     * arriving at its target (not including the starting coordinate). */
    public short[] ycoords = new short[0];

    public StreamablePoint[] affectedPoints = new StreamablePoint[0];

    /** Constructor used when unserializing. */
    public RocketEffect ()
    {
    }

    public RocketEffect (Piece shooter, int damage)
    {
        this.shooter = shooter;
        baseDamage = damage;
    }

    @Override // documentation inherited
    public EffectHandler createHandler (BangObject bangobj)
    {
        return new RocketHandler();
    }

    /**
     * Returns the type of shot fired, which could be a model or an effect.
     */
    public String getShotType ()
    {
        return "units/frontier_town/artillery/shell";
    }

    @Override // documentation inherited
    public void prepare (BangObject bangobj, IntIntMap dammap)
    {
        ArrayIntSet affected = new ArrayIntSet();

        // shoot in a random direction
        //int dir = RandomUtil.getInt(Piece.DIRECTIONS.length);
        for (int dir : Piece.DIRECTIONS) {
            Object obj = bangobj.getFirstAvailableTarget(shooter.x, shooter.y, dir);
            if (obj instanceof Piece) {
                Piece target = (Piece)obj;
                affected.add(target.pieceId);
                short tx = target.x;
                short ty = target.y;
                xcoords = ArrayUtil.append(xcoords, tx);
                ycoords = ArrayUtil.append(ycoords, ty);
            } else if (obj instanceof Point) {
                Point p = (Point)obj;
                affectedPoints = ArrayUtil.append(affectedPoints, new StreamablePoint(p.x, p.y));
                xcoords = ArrayUtil.append(xcoords, (short)p.x);
                ycoords = ArrayUtil.append(ycoords, (short)p.y);
            }
        }
        pieces = affected.toIntArray();
    }

    @Override // documentation inherited
    public boolean apply (BangObject bangobj, Observer obs)
    {
        return true;
    }


    @Override // documentation inherited
    public void apply (
        BangObject bangobj, Observer obs, int pidx, Piece piece, int dist)
    {
        // finally do the damage
        damage(bangobj, obs, shooter.owner, shooter, piece, baseDamage, "exploded");
    }
}