package jgate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class JGateChannelHandlerManager {
    static final Logger log = LoggerFactory.getLogger(JGateChannelHandlerManager.class);

    static final JGateChannelHandlerManager manager = new JGateChannelHandlerManager();
    public final static JGateChannelHandlerManager getInstance() {
        return manager;
    }

    private ConcurrentHashMap<String,JGateChannelHandler> channelHandlerHashMap = new ConcurrentHashMap<String,JGateChannelHandler>();

    public void addChannelHandler(JGateChannelHandler handler){
        JGateChannelHandler old = channelHandlerHashMap.get(handler.getCid());
        if (old != null){
            log.warn("addChannelHandler:find old handler,cid:" + old.getCid());
        }

        channelHandlerHashMap.put(handler.getCid(),handler);
    }

    public void removeChannelHandler(String cid){
        JGateChannelHandler old = channelHandlerHashMap.remove(cid);
        if (old == null){
            log.warn("removeChannelHandler:can't find handerl,cid:" + cid);
        }
    }

    public JGateChannelHandler getChannelHandler(String cid){
        return channelHandlerHashMap.get(cid);
    }

    public int getChannelHandlerCount(){
        return channelHandlerHashMap.size();
    }

    public void clear(){
        channelHandlerHashMap.clear();
    }

}
