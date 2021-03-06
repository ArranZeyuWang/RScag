/*
 * Scagnostics
 *
 * Leland Wilkinson and Anushka Anand (University of Illinois at Chicago)
 * This program accompanies the following paper:
 
 * Wilkinson L., Anand, A., and Grossman, R. (2006). High-Dimensional visual analytics: 
 *   Interactive exploration guided by pairwise views of point distributions. 
 *   IEEE Transactions on Visualization and Computer Graphics, November/December 2006 (Vol. 12, No. 6) pp. 1363-1372.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software.
 * Supporting documentation must also include a citation of
 * the abovementioned article.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package scagnostics;

import java.util.*;
import java.util.List;

class Node {
    protected int x, y;          // coordinate X,Y
    protected int count;        // number of points aggregated at this node
    protected Edge anEdge;     // an edge which starts from this node
    protected List neighbors;   // nearest Delaunay neighbors list
    protected boolean onMST;
    protected boolean onHull = false;
    protected boolean isVisited = false;
    protected int mstDegree;
    protected int pointID;
    protected int nodeID;
    protected int degree;

    protected Node(int x, int y, int count, int pointID) {
        this.x = x;
        this.y = y;
        this.count = count;
        anEdge = null;
        neighbors = new ArrayList();
        this.pointID = pointID;
    }

    protected double distToNode(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    protected int getMstDegree(){
        degree=0;
        if (neighbors!=null)
        {
            Iterator it = neighbors.iterator();
            while (it.hasNext())
            {
                Edge e = (Edge)it.next();
                if (e.onMST)
                {
                    degree++;
                }
            }
        }
        return degree;
    }
    protected void setNeighbor(Edge neighbor) {
        neighbors.add(neighbor);
    }

    protected Iterator getNeighborIterator() {
        return neighbors.iterator();
    }

    protected Edge shortestEdge(boolean mst) {
        Edge emin = null;
        if (neighbors != null) {
            Iterator it = neighbors.iterator();
            double wmin = Double.MAX_VALUE;
            while (it.hasNext()) {
                Edge e = (Edge) it.next();
                if (mst || !e.otherNode(this).onMST) {
                    double wt = e.weight;
                    if (wt < wmin) {
                        wmin = wt;
                        emin = e;
                    }
                }
            }
        }
        return emin;
    }

    protected int getMSTChildren(double cutoff, double[] maxLength, Edge[] maxEdge) {
        int count = 0;
        if (isVisited)
            return count;
        isVisited = true;
        Iterator it = neighbors.iterator();
        while (it.hasNext()) {
            Edge e = (Edge) it.next();
            if (e.onMST) {
                if (e.weight < cutoff) {
                    if (!e.otherNode(this).isVisited) {
                        count += e.otherNode(this).getMSTChildren(cutoff, maxLength, maxEdge);
                        double el = e.weight;
                        if (el > maxLength[0])
                        {
                            maxLength[0] = el;
                            maxEdge[0] = e;
                        }
                    }
                }
            }
        }
        count += this.count; // add count for this node
        return count;
    }
}