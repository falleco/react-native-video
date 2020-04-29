package com.brentvatne.exoplayer.events;

import com.google.android.exoplayer2.Player;

public class BackgroundLeaveMessage {

    private Player player;

    public BackgroundLeaveMessage() {
    }

    public BackgroundLeaveMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
