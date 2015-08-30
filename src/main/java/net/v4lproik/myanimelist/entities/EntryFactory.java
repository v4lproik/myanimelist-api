package net.v4lproik.myanimelist.entities;

import net.v4lproik.myanimelist.api.models.TypeEnum;

public class EntryFactory {

    public Entry getEntity(String type, Integer id){

        Entry entry;

        TypeEnum typeEnum = TypeEnum.fromValue(type);

        if (typeEnum == null){
            throw new EnumConstantNotPresentException(TypeEnum.class, type);
        }

        switch (typeEnum){
            case MANGA:
                entry = new Manga(id);
                break;

            case ANIME:
                entry = new Anime(id);
                break;

            default:
                throw new IllegalArgumentException();
        }

        return entry;
    }
}
