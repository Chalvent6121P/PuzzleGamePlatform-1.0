package service.impl;

import java.util.List;

import dao.PlayerAchievementDao;
import dao.impl.PlayerAchievementDaoImpl;
import entity.PlayerAchievementRecord;
import service.PlayerAchievementService;

public class PlayerAchievementServiceImpl implements PlayerAchievementService {

    private final PlayerAchievementDao playerAchievementDao =
            new PlayerAchievementDaoImpl();

    @Override
    public List<PlayerAchievementRecord> findAchievementRecords(int playerNo) {
        if (playerNo <= 0) {
            throw new IllegalArgumentException("玩家編號必須大於 0。");
        }
        return playerAchievementDao.selectAchievementRecordsByPlayerNo(playerNo);
    }
}
