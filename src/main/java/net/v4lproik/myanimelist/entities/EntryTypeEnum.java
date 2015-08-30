package net.v4lproik.myanimelist.entities;

public enum EntryTypeEnum {
    MANGA,
    ANIME;

    public static EntryTypeEnum fromValue(String value){
        return EntryTypeEnum.valueOf(value.toUpperCase()) != null ? EntryTypeEnum.valueOf(value.toUpperCase()) : null;
    }
}
