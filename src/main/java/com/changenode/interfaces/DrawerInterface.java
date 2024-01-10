package com.changenode.interfaces;

public interface DrawerInterface {
    void onExitRequested();
    void onResetAll();
    void onChangeARState(boolean state);
    void onDrawerClose();
    void onRefreshRateChange(int val);
}
