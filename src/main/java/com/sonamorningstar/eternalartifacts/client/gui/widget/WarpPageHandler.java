package com.sonamorningstar.eternalartifacts.client.gui.widget;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
public class WarpPageHandler {
    private final List<Warp> warpList;
    private final int warpsPerPage;
    private int pageSize;
    private float pageSizeFloat;

    public WarpPageHandler(List<Warp> warpList, int warpsPerPage) {
        this.warpList = warpList;
        this.warpsPerPage = warpsPerPage;
        this.pageSize = warpList.size() / warpsPerPage;
        this.pageSizeFloat = (float) warpList.size() / warpsPerPage;
    }

    public void addWarp(Warp warp) {
        warpList.add(warp);
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
    
    public int getPageSizeFiltered(String filter) {
        List<Warp> filteredWarps = getWarps(filter);
        return filteredWarps.size() / warpsPerPage;
    }
    public float getPageSizeFilteredFloat(String filter) {
        List<Warp> filteredWarps = getWarps(filter);
        return (float) filteredWarps.size() / warpsPerPage;
    }

    public Warp getWarp(int index) {
        return warpList.get(index);
    }

    public void recalculatePageSize() {
        this.pageSize = warpList.size() / warpsPerPage;
        this.pageSizeFloat = (float) warpList.size() / warpsPerPage;
    }
    
    public List<Warp> getWarps(int page) {
        int startIndex = page * warpsPerPage;
        int endIndex = Math.min(startIndex + warpsPerPage, warpList.size());
        List<Warp> currentPageWarps = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) currentPageWarps.add(warpList.get(i));
        return currentPageWarps;
    }
    
    public List<Warp> getWarps(String filter) {
        List<Warp> filteredWarps = new ArrayList<>();
        for (Warp warp : warpList) {
            if (warp.getLabel().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
                filteredWarps.add(warp);
            }
        }
        return filteredWarps;
    }
    
    public List<Warp> getWarps(String filter, int page) {
        List<Warp> filteredWarps = new ArrayList<>();
        for (Warp warp : warpList) {
            if (warp.getLabel().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))) {
                filteredWarps.add(warp);
            }
        }
        
        int startIndex = page * warpsPerPage;
        int endIndex = Math.min(startIndex + warpsPerPage, filteredWarps.size());
        List<Warp> pagedFilteredWarps = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) pagedFilteredWarps.add(filteredWarps.get(i));
        return pagedFilteredWarps;
    }
}
