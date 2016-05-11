package a.b.c.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by FerrandTian on 2015/10/17.
 */
public class JSONManager {

    /**
     * 把一个JSONArray转化成List，其中JSONArray的子项是JSONObject
     *
     * @param array
     * @return
     */
    public static List<HashMap<String, Object>> toMapList(JSONArray array) {
        List<HashMap<String, Object>> data = new ArrayList<>();
        if (null != array && array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                HashMap<String, Object> map = new HashMap<>();
                Iterator<String> it = object.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    map.put(key, object.opt(key));
                }
                data.add(map);
            }
        }
        return data;
    }

    public static JSONArray toJSONArray(List<HashMap<String, Object>> list) throws JSONException {
        JSONArray array = new JSONArray();
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject object = new JSONObject();
                HashMap<String, Object> map = list.get(i);
                Iterator<String> it = map.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    object.put(key, map.get(key));
                }
                array.put(object);
            }
        }
        return array;
    }

}
