package a.b.c.widget.dynamic;

/**
 * Any adapter used with DynamicGridView must implement DynamicGridAdapterInterface.
 * Adapter implementation also must has stable items id.
 * See {@link AbstractDynamicGridAdapter} for stable id implementation example.
 */

public interface DynamicGridAdapterInterface {

    /**
     * Determines how to reorder items dragged from <code>originalPosition</code> to <code>newPosition</code>
     */
    void reorderItems(int originalPosition, int newPosition);

    /**
     * @return return columns number for GridView. Need for compatibility
     * (@link android.widget.GridView#getNumColumns() requires api 11)
     */
    int getColumnCount();

    /**
     * Determines whether the item in the specified <code>position</code> can be reordered.
     */
    boolean canReorder(int position);

}
