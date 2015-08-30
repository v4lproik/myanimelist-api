package net.v4lproik.myanimelist.entities;

public class MyAnimeListEntryFactory {

    public MyAnimeListEntry getEntity(String type, Integer id){

        MyAnimeListEntry myAnimeListEntry;

        EntryTypeEnum typeEnum = EntryTypeEnum.fromValue(type);

        if (typeEnum == null){
            throw new EnumConstantNotPresentException(EntryTypeEnum.class, type);
        }

        switch (typeEnum){
            case MANGA:
                myAnimeListEntry = new MyAnimeListManga(id);
                break;

            case ANIME:
                myAnimeListEntry = new MyAnimeListAnime(id);
                break;

            default:
                throw new IllegalArgumentException();
        }

        return myAnimeListEntry;
    }
}
