package xyz.wendyltanpcy.jandancomment.helper;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Wendy on 2017/10/26.
 */

public class SerializableMap implements Serializable {
    private Map<String,String> map;

    public Map<String,String> getMap()
    {
        return map;
    }
    public void setMap(Map<String,String> map)
    {
        this.map=map;
    }
    public SerializableMap(){

    }
}