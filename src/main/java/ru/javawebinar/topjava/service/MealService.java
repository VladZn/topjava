package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collection;

public interface MealService {
    Meal create(Meal meal);

    Meal get(int id) throws NotFoundException;

    void update(Meal meal);

    void delete(int id) throws NotFoundException;

    Collection<Meal> getAll();
}