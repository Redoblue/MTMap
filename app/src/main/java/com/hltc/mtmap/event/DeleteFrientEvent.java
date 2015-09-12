package com.hltc.mtmap.event;

public class DeleteFrientEvent {
    private long userId;

    public DeleteFrientEvent(long userId) {
        this.userId = userId;
    }
    public long getUserId(){
        return userId;
    }
}
