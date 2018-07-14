package jgate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JGateChannelListenerManager {
    private final static Logger log = LoggerFactory.getLogger(JGateChannelListenerManager.class);

    static final JGateChannelListenerManager manager = new JGateChannelListenerManager();
    public final static JGateChannelListenerManager getInstance() {
        return manager;
    }

    private HashMap<String,JGateChannelListener> channelListenerHashMap = new HashMap<>();

    public void addListener(String name,ProtocolType protocol, int port,int maxIdleTimeSeconds,boolean openDebugLogging){
        JGateChannelListener listener = new JGateChannelListener(protocol,port,maxIdleTimeSeconds,openDebugLogging);
        listener.listen();
        addListener(name,listener);
    }

    public void addListener(String name,JGateChannelListener listener){
        channelListenerHashMap.put(name,listener);
    }

    public JGateChannelListener getListener(String name){
        return channelListenerHashMap.get(name);
    }

    public void removeListener(String name){
        channelListenerHashMap.remove(name);
    }

    public int getListenerCount(){
        return channelListenerHashMap.size();
    }

    /**
     * 等待
     */
    public void awaitClose(){
        try {
            for (Map.Entry<String,JGateChannelListener> entry : channelListenerHashMap.entrySet()){
                entry.getValue().awaitClose();
            }
        }
        catch (Exception e){
            log.error("awaitClose error:" + e.toString());
        }
    }

}
