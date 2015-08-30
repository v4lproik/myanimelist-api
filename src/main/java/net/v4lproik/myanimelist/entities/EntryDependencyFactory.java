package net.v4lproik.myanimelist.entities;

import net.v4lproik.myanimelist.api.models.TypeEnum;

public class EntryDependencyFactory {

    public EntryDependency getEntity(String type, Integer id){

        EntryDependency entryDependency;

        TypeEnum typeEnum = TypeEnum.fromValue(type);

        if (typeEnum == null){
            throw new EnumConstantNotPresentException(TypeEnum.class, type);
        }

        switch (typeEnum){
            case MANGA:
                entryDependency = new MangaDependency(id);
                break;

            case ANIME:
                entryDependency = new AnimeDependency(id);
                break;

            default:
                throw new IllegalArgumentException();
        }

        entryDependency.setType(type);
        return entryDependency;
    }
}
