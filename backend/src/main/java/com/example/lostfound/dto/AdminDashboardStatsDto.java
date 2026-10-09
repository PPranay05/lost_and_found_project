package com.example.lostfound.dto;

public class AdminDashboardStatsDto {

    private long totalUsers;
    private long totalLostItems;
    private long totalFoundItems;
    private long pendingClaims;
    private long matchedItems;
    private long claimedItems;
    private long returnedItems;
    private long rejectedClaims;

    public AdminDashboardStatsDto() {}

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalLostItems() {
        return totalLostItems;
    }

    public void setTotalLostItems(long totalLostItems) {
        this.totalLostItems = totalLostItems;
    }

    public long getTotalFoundItems() {
        return totalFoundItems;
    }

    public void setTotalFoundItems(long totalFoundItems) {
        this.totalFoundItems = totalFoundItems;
    }

    public long getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(long pendingClaims) {
        this.pendingClaims = pendingClaims;
    }

    public long getMatchedItems() {
        return matchedItems;
    }

    public void setMatchedItems(long matchedItems) {
        this.matchedItems = matchedItems;
    }

    public long getClaimedItems() {
        return claimedItems;
    }

    public void setClaimedItems(long claimedItems) {
        this.claimedItems = claimedItems;
    }

    public long getReturnedItems() {
        return returnedItems;
    }

    public void setReturnedItems(long returnedItems) {
        this.returnedItems = returnedItems;
    }

    public long getRejectedClaims() {
        return rejectedClaims;
    }

    public void setRejectedClaims(long rejectedClaims) {
        this.rejectedClaims = rejectedClaims;
    }
}
