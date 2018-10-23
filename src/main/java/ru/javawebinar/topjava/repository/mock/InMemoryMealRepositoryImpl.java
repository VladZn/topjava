package ru.javawebinar.topjava.repository.mock;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but absent in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        if (get(id).getUserId() == SecurityUtil.authUserId()) {
            return repository.remove(id) != null;
        }

        return false;
    }

    @Override
    public Meal get(int id) {
        Meal result = repository.get(id);
        if (result.getUserId() == SecurityUtil.authUserId()) {
            return result;
        }
        return null;
    }

    @Override
    public Collection<Meal> getAll() {
        return getFiltered(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    public Collection<Meal> getFiltered(LocalDateTime start, LocalDateTime end){
        return repository.values().stream()
                .filter(user -> user.getUserId() == SecurityUtil.authUserId())
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());

    }
}

