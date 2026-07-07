package service.impl;

import java.util.List;

import dao.PlayerDao;
import dao.impl.PlayerDaoImpl;
import entity.Player;
import service.PlayerService;

public class PlayerServiceImpl implements PlayerService {

    private final PlayerDao playerDao = new PlayerDaoImpl();

    @Override
    public boolean register(Player player) {
        validateRegistration(player);
        if (playerDao.selectByAccount(player.getAccount().trim()) != null) {
            return false;
        }
        normalize(player);
        player.setRole(Player.ROLE_PLAYER);
        player.setStatus(Player.STATUS_ACTIVE);
        playerDao.insert(player);
        return true;
    }

    @Override
    public boolean createByAdmin(Player player) {
        validateRegistration(player);
        if (playerDao.selectByAccount(player.getAccount().trim()) != null) {
            return false;
        }
        normalize(player);
        player.setRole(normalizeRole(player.getRole()));
        player.setStatus(normalizeStatus(player.getStatus()));
        playerDao.insert(player);
        return true;
    }

    @Override
    public Player login(String account, String password) {
        if (isBlank(account) || isBlank(password)) {
            return null;
        }
        Player player = playerDao.login(account.trim(), password);
        if (player != null) {
            playerDao.updateLastLogin(player.getPlayerNo());
            player.setLastLoginTime(java.time.LocalDateTime.now());
        }
        return player;
    }

    @Override
    public Player findByPlayerNo(int playerNo) {
        return playerNo <= 0 ? null : playerDao.selectByPlayerNo(playerNo);
    }

    @Override
    public Player findByAccount(String account) {
        return isBlank(account) ? null
                : playerDao.selectByAccount(account.trim());
    }

    @Override
    public List<Player> findAll() {
        return playerDao.selectAll();
    }

    @Override
    public void update(Player player) {
        validateForUpdate(player);
        Player sameAccount = playerDao.selectByAccount(player.getAccount().trim());
        if (sameAccount != null
                && sameAccount.getPlayerNo() != player.getPlayerNo()) {
            throw new IllegalArgumentException("此帳號已被其他玩家使用。");
        }
        normalize(player);
        player.setRole(normalizeRole(player.getRole()));
        player.setStatus(normalizeStatus(player.getStatus()));
        playerDao.update(player);
    }

    @Override
    public boolean deleteByPlayerNo(int playerNo) {
        if (playerNo <= 0) {
            throw new IllegalArgumentException("playerNo 必須大於 0。");
        }
        return playerDao.deleteByPlayerNo(playerNo);
    }

    private void validateRegistration(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("玩家資料不可為 null。");
        }
        validateFields(player);
    }

    private void validateForUpdate(Player player) {
        if (player == null || player.getPlayerNo() <= 0) {
            throw new IllegalArgumentException("請先選擇要修改的玩家。");
        }
        validateFields(player);
    }

    private void validateFields(Player player) {
        if (isBlank(player.getPlayerName())) {
            throw new IllegalArgumentException("玩家名稱不可空白。");
        }
        if (isBlank(player.getAccount())) {
            throw new IllegalArgumentException("帳號不可空白。");
        }
        if (player.getAccount().trim().length() < 3) {
            throw new IllegalArgumentException("帳號至少需要 3 個字元。");
        }
        if (isBlank(player.getPassword())
                || player.getPassword().length() < 4) {
            throw new IllegalArgumentException("密碼至少需要 4 個字元。");
        }
        if (player.getPlayerName().trim().length() > 50
                || player.getAccount().trim().length() > 50
                || player.getPassword().length() > 100) {
            throw new IllegalArgumentException("玩家名稱、帳號或密碼長度超過資料庫限制。");
        }
    }

    private void normalize(Player player) {
        player.setPlayerName(player.getPlayerName().trim());
        player.setAccount(player.getAccount().trim());
    }

    private String normalizeRole(String role) {
        return Player.ROLE_ADMIN.equalsIgnoreCase(role)
                ? Player.ROLE_ADMIN : Player.ROLE_PLAYER;
    }

    private String normalizeStatus(String status) {
        return Player.STATUS_INACTIVE.equalsIgnoreCase(status)
                ? Player.STATUS_INACTIVE : Player.STATUS_ACTIVE;
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
