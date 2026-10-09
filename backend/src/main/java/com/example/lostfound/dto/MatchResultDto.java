package com.example.lostfound.dto;

public class MatchResultDto {

    private LostItemDto lostItem;
    private FoundItemDto foundItem;
    private int matchPercentage;
    private String matchReason;

    public MatchResultDto() {}

    public MatchResultDto(LostItemDto lostItem, FoundItemDto foundItem, int matchPercentage, String matchReason) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchPercentage = matchPercentage;
        this.matchReason = matchReason;
    }

    public LostItemDto getLostItem() {
        return lostItem;
    }

    public void setLostItem(LostItemDto lostItem) {
        this.lostItem = lostItem;
    }

    public FoundItemDto getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItemDto foundItem) {
        this.foundItem = foundItem;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }
}
