package service;

import java.util.List;

import entity.Game;

public interface GameService {

    List<Game> findAll();

    Game findById(int gameNo);

    void create(Game game);

    void update(Game game);

    boolean delete(int gameNo);
}
