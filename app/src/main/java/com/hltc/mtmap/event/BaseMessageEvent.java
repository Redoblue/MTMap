package com.hltc.mtmap.event;

/**
 * Created by X-MH on 2015/9/20.
 */
public class BaseMessageEvent {
    public static final int EVENT_MODIFY_USER_NAME  = 1;
    public static final int EVENT_DELETE_USER = EVENT_MODIFY_USER_NAME+1;
    public static final int EVENT_KILL_SELF = EVENT_DELETE_USER + 1;

    public static final int EVENT_FRIENTLIST_RED_ROT_SHOW = EVENT_KILL_SELF+1;
    public static final int EVENT_FRIENTLIST_RED_ROT_HIDE = EVENT_FRIENTLIST_RED_ROT_SHOW+1;
    public static final int EVENT_MESSAGE_CHANGE = EVENT_FRIENTLIST_RED_ROT_HIDE + 1;
    public int action;

    public long userId;
    public Object tag;

}
