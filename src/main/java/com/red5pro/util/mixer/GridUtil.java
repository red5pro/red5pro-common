package com.red5pro.util.mixer;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.red5pro.mixer.nodes.video.VideoSourceNode;

/**
 * Grid-related computations for VideoSourceNodes.
 *
 * @author Nate Roe
 */
public class GridUtil {

    /**
     * @param nodes
     * @param gridWidth
     * @param gridHeight
     * @param dimensions
     * @return
     */
    public static List<VideoSourceNode> gridLayout(Collection<VideoSourceNode> nodes, Integer gridCols, Integer gridRows, Rectangle pixelDimensions) {
        // for now it's a fixed grid.
        // up to width * height source nodes will be arranged into a grid.
        // the nodes will be modified in-place, and a new List will be
        // returned containing only the nodes in the grid.
        // Extra nodes will not be included
        // The original collection is not modified and the extra nodes remain
        // unchanged there.

        int elementWidth = pixelDimensions.width / gridCols;
        int elementHeight = pixelDimensions.height / gridRows;

        List<VideoSourceNode> result = new LinkedList<>();
        Iterator<VideoSourceNode> nit = nodes.iterator();
        for (int j = 0; j < gridRows; j++) {
            for (int i = 0; i < gridCols; i++) {
                if (nit.hasNext()) {
                    VideoSourceNode node = nit.next();
                    node.setDestX(i * elementWidth);
                    node.setDestY(j * elementHeight);
                    node.setDestWidth(elementWidth);
                    node.setDestHeight(elementHeight);
                    result.add(node);
                } else {
                    break;
                }
            }
        }

        return result;
    }
}
