package service;

import java.util.List;

import entity.Player;

public interface PlayerService {

    boolean register(Player player);

    boolean createByAdmin(Player player);

    Player login(String account, String password);

    Player findByPlayerNo(int playerNo);

    Player findByAccount(String account);

    List<Player> findAll();

    void update(Player player);

    boolean deleteByPlayerNo(int playerNo);
}
