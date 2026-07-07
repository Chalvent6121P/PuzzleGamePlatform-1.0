package entity;

public class Achievement {
	
	 private int achievementNo;
	 private Integer gameNo;
	 private String achievementName;
	 private String description;
	 private String conditionText;
	 public Achievement() {
		super();
		// TODO Auto-generated constructor stub
	 }
	 public Achievement(Integer gameNo, String achievementName, String description, String conditionText) {
		super();
		this.gameNo = gameNo;
		this.achievementName = achievementName;
		this.description = description;
		this.conditionText = conditionText;
	 }
	 public int getAchievementNo() {
		 return achievementNo;
	 }
	 public void setAchievementNo(int achievementNo) {
		 this.achievementNo = achievementNo;
	 }
	 public Integer getGameNo() {
		 return gameNo;
	 }
	 public void setGameNo(Integer gameNo) {
		 this.gameNo = gameNo;
	 }
	 public String getAchievementName() {
		 return achievementName;
	 }
	 public void setAchievementName(String achievementName) {
		 this.achievementName = achievementName;
	 }
	 public String getDescription() {
		 return description;
	 }
	 public void setDescription(String description) {
		 this.description = description;
	 }
	 public String getConditionText() {
		 return conditionText;
	 }
	 public void setConditionText(String conditionText) {
		 this.conditionText = conditionText;
	 }
	 

}
