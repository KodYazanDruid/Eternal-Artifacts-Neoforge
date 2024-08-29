package com.sonamorningstar.eternalartifacts.client.gui.widget;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WarpPageHandler {
    private final List<Warp> warpList;
    private final int warpsPerPage;
    private int pageSize;
    private int currentPage = 0;

    public WarpPageHandler(List<Warp> warpsList, int warpsPerPage) {
        this.warpList = warpsList;
        this.warpsPerPage = warpsPerPage;
        this.pageSize = warpsList.size() / warpsPerPage;
    }

    public void addWarp(Warp warp) {
        warpList.add(warp);
        //warp.deleteLogic = (button, key) -> deleteWarp(warp);
        recalculatePageSize();
    }

    public void deleteWarp(Warp warp) {
        warpList.remove(warp);
        recalculatePageSize();
    }

    public void deleteWarp(int index) {
        warpList.remove(index);
        recalculatePageSize();
    }

    public Warp getWarp(int index) {
        return warpList.get(index);
    }

    public void recalculatePageSize() {
        this.pageSize = warpList.size() / warpsPerPage;
    }

    public void turnPageRight() {
        currentPage = Math.min(currentPage + 1, pageSize);
    }
    public void turnPageLeft() {
        currentPage = Math.max(currentPage -1, 0);
    }

    public List<Warp> getCurrentWarps() {
        int startIndex = currentPage * warpsPerPage;
        int endIndex = Math.min(startIndex + warpsPerPage, warpList.size());
        List<Warp> currentPageWarps = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) currentPageWarps.add(warpList.get(i));
        return currentPageWarps;
    }



}
