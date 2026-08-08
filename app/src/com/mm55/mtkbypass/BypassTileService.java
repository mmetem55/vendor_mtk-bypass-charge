package com.mm55.mtkbypass;

import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class BypassTileService extends TileService {

    private static final String PROP_BYPASS = "persist.sys.mtk_bypass";

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        boolean isEnabled = SystemProperties.getInt(PROP_BYPASS, 0) == 1;
        boolean newState = !isEnabled;

        SystemProperties.set(PROP_BYPASS, newState ? "1" : "0");

        updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        boolean isEnabled = SystemProperties.getInt(PROP_BYPASS, 0) == 1;

        tile.setState(isEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);

        tile.setLabel(getString(R.string.app_name));

        tile.updateTile();
    }
}
