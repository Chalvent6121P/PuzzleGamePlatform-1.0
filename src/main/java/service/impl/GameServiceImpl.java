package service.impl;

import java.util.List;

import dao.GameDao;
import dao.impl.GameDaoImpl;
import entity.Game;
import service.GameService;

public class GameServiceImpl implements GameService {

    private final GameDao gameDao = new GameDaoImpl();

    @Override
    public List<Game> findAll() {
        return gameDao.selectAll();
    }

    @Override
    public Game findById(int gameNo) {
        if (gameNo <= 0) {
            return null;
        }
        return gameDao.selectById(gameNo);
    }

    @Override
    public void create(Game game) {
        validate(game, false);
        normalize(game);
        gameDao.insert(game);
    }

    @Override
    public void update(Game game) {
        validate(game, true);
        normalize(game);
        gameDao.update(game);
    }

    @Override
    public boolean delete(int gameNo) {
        if (gameNo <= 0) {
            throw new IllegalArgumentException("遊戲編號必須大於 0。");
        }
        return gameDao.deleteById(gameNo);
    }

    private void validate(Game game, boolean requireId) {
        if (game == null) {
            throw new IllegalArgumentException("遊戲資料不可為 null。");
        }
        if (requireId && game.getGameNo() <= 0) {
            throw new IllegalArgumentException("請先選擇要修改的遊戲。");
        }
        if (isBlank(game.getGameName())) {
            throw new IllegalArgumentException("遊戲名稱不可空白。");
        }
        if (game.getGameName().trim().length() > 100) {
            throw new IllegalArgumentException("遊戲名稱不可超過 100 個字元。");
        }
        if (!isBlank(game.getDescription())
                && game.getDescription().trim().length() > 255) {
            throw new IllegalArgumentException("遊戲描述不可超過 255 個字元。");
        }
    }

    private void normalize(Game game) {
        game.setGameName(game.getGameName().trim());
        game.setDifficulty(isBlank(game.getDifficulty())
                ? "未設定" : game.getDifficulty().trim());
        game.setDescription(isBlank(game.getDescription())
                ? null : game.getDescription().trim());
        game.setCoverImagePath(isBlank(game.getCoverImagePath())
                ? null : game.getCoverImagePath().trim());
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
