package com.brentvatne.exoplayer.events;

import com.google.android.exoplayer2.Player;

public class BackgroundEnterMessage {

    private Player player;

    public BackgroundEnterMessage() {
    }

    public BackgroundEnterMessage(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
