package a.b.c.widget.dynamic;

import android.widget.BaseAdapter;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract adapter for {@link DynamicGridView} with sable items id;
 */

public abstract class AbstractDynamicGridAdapter<T> extends BaseAdapter implements DynamicGridAdapterInterface {
    public static final int INVALID_ID = -1;

    private int nextStableId = 0;

    private Map<T, Integer> mIdMap = new IdentityHashMap<T, Integer>();


    /**
     * Adapter must have stable id
     *
     * @return
     */
    @Override
    public final boolean hasStableIds() {
        return true;
    }

    /**
     * creates stable id for object
     *
     * @param item
     */
    protected void addStableId(T item) {
        mIdMap.put(item, nextStableId++);
    }

    /**
     * create stable ids for list
     *
     * @param items
     */
    protected void addAllStableId(List<? extends T> items) {
        for (T item : items) {
            addStableId(item);
        }
    }

    @Override
    public abstract T getItem(int position);

    /**
     * get id for position
     *
     * @param position
     * @return
     */
    @Override
    public final long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        return mIdMap.get(getItem(position));
    }

    /**
     * clear stable id map
     * should called when clear adapter data;
     */
    protected void clearStableIdMap() {
        mIdMap.clear();
    }

    /**
     * remove stable id for <code>item</code>. Should called on remove data item from adapter
     *
     * @param item
     */
    protected void removeStableID(T item) {
        mIdMap.remove(item);
    }

}
