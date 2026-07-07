package service;

import java.util.List;

import entity.PlayerAchievementRecord;

public interface PlayerAchievementService {

    List<PlayerAchievementRecord> findAchievementRecords(int playerNo);
}
