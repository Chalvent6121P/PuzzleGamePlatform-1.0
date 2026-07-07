package dao;

import java.util.List;

import entity.Game;

public interface GameDao {

    List<Game> selectAll();

    Game selectById(int gameNo);

    void insert(Game game);

    void update(Game game);

    boolean deleteById(int gameNo);
}
