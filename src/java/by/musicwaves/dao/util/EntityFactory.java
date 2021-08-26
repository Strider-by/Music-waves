package by.musicwaves.dao.util;

import by.musicwaves.entity.Entity;

public interface EntityFactory<T extends Entity>
{
    T createInstance();
}
