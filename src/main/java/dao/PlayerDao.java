package dao;

import java.util.List;

import entity.Player;

public interface PlayerDao {

    void insert(Player player);

    Player selectByAccount(String account);

    Player selectByPlayerNo(int playerNo);

    Player login(String account, String password);

    List<Player> selectAll();

    void update(Player player);

    boolean deleteByPlayerNo(int playerNo);

    void updateLastLogin(int playerNo);
}
