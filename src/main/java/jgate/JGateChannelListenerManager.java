package jgate;

import java.util.HashMap;

public class JGateChannelListenerManager {
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

}
