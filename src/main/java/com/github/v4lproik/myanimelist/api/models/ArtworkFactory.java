package com.github.v4lproik.myanimelist.api.models;

public class ArtworkFactory {

    public ArtWork getEntity(String type, Integer id){

        ArtWork artWork;

        TypeEnum typeEnum = TypeEnum.fromValue(type);

        if (typeEnum == null){
            throw new EnumConstantNotPresentException(TypeEnum.class, type);
        }

        switch (typeEnum){
            case MANGA:
                artWork = new Manga(id);
                break;

            case ANIME:
                artWork = new Anime(id);
                break;

            default:
                throw new IllegalArgumentException();
        }

        artWork.setType(type);
        return artWork;
    }
}
