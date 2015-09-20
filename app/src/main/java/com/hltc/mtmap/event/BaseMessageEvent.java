package com.hltc.mtmap.event;

/**
 * Created by X-MH on 2015/9/20.
 */
public class BaseMessageEvent {
    public static final int EVENT_MODIFY_USER_NAME  = 1;
    public static final int EVENT_DELETE_USER = EVENT_MODIFY_USER_NAME+1;

    public int action;

    public long userId;
    public Object tag;

}
