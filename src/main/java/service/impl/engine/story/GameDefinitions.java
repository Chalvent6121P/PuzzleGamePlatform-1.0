package service.impl.engine.story;

import java.awt.Color;
import java.util.Arrays;

public final class GameDefinitions {

    private GameDefinitions() {
    }

    public static StoryGameDefinition clockTower() {
        return new StoryGameDefinition(
                2,
                2,
                2,
                "逆行的鐘塔",
                "THE CLOCK THAT RUNS BACKWARD",
                "午夜鐘聲響起後，整座鐘塔開始逆向運轉。你必須修正鏡面時間、齒輪比與主鐘校準碼，否則會被困在重複的午夜。",
                "最後一枚齒輪重新咬合，鐘聲回到正向。黎明穿過高塔窗戶，你成功離開時間迴圈。",
                new Color(17, 26, 40),
                new Color(61, 40, 31),
                new Color(201, 161, 86),
                "/images/games/clock_tower.png",
                Arrays.asList(
                        new StoryPuzzle(
                                3,
                                "鏡面鐘盤",
                                "鐘盤顯示 21:40，但你看到的是鏡中倒影。鐘框上刻著：『鏡面時間與真實時間相加為 12:00。』",
                                "請輸入真實時間的四位數字。",
                                "0220|02:20",
                                "以 12:00 減去鏡面顯示的 09:40。",
                                "鏡面裂開，銅片上浮現真正時間 02:20。你取得了鏡面時間便條。",
                                201,
                                "/sounds/clock_chime.wav"),
                        new StoryPuzzle(
                                4,
                                "三聯齒輪",
                                "A 齒輪有 12 齒，順時針轉 9 圈；它咬合 36 齒的 B，B 再咬合 18 齒的 C。相鄰齒輪方向相反。",
                                "C 齒輪轉幾圈、方向為何？可輸入例如：6順時針。",
                                "6順時針|順時針6圈|6",
                                "轉數依齒數反比；經過兩次咬合，方向與 A 相同。",
                                "C 齒輪順時針轉動 6 圈，隱藏槽吐出齒輪比刻片。",
                                202,
                                "/sounds/gear_turn.wav"),
                        new StoryPuzzle(
                                5,
                                "午夜校準",
                                "校準盤要求：把真實時間換算成午夜後分鐘數，再加上 C 齒輪圈數 × 15。結果以四位數輸入。",
                                "02:20 是 140 分鐘；C 齒輪為 6 圈。請輸入校準碼。",
                                "0230|230",
                                "140 + 6 × 15 = 230，四位數需補 0。",
                                "校準碼 0230 被接受，主鐘恢復正轉，你取得校準鑰匙。",
                                203,
                                "/sounds/door_unlock.wav")));
    }

    public static StoryGameDefinition hospital() {
        return new StoryGameDefinition(
                3,
                3,
                3,
                "霧鎖病棟",
                "THE WARD BEHIND THE FOG",
                "荒廢病棟被白霧封鎖，廣播反覆播放不存在的病歷。你必須解讀藥物分類卡、輪班紀錄與電梯權限碼。",
                "病房燈光依序熄滅，霧氣從走廊退去。電梯抵達一樓，你帶著完整病歷離開病棟。",
                new Color(20, 36, 38),
                new Color(42, 51, 54),
                new Color(146, 199, 177),
                "/images/games/hospital.png",
                Arrays.asList(
                        new StoryPuzzle(
                                6,
                                "封存病歷：鎮靜分類",
                                "病歷寫著：『病患因癲狂失控，院內流程卡要求找出標示為「抗焦慮／鎮靜」的藥物類別。』\n\n"
                              + "1. 盤尼西林類抗生素\n"
                              + "2. 芬太尼類鴉片止痛藥\n"
                              + "3. 苯二氮平類藥物\n"
                              + "4. 腎上腺皮質激素\n\n"
                              + "※ 這是虛構遊戲中的藥物分類題，不是現實醫療建議。",
                                "請輸入正確選項編號或類別名稱。",
                                "3|苯二氮平類|3苯二氮平類|苯二氮平類藥物",
                                "盤尼西林用於細菌感染；芬太尼是強效鴉片止痛藥；皮質激素主要抑制發炎。",
                                "分類卡亮起第 3 格，你取得鎮靜流程卡。",
                                204,
                                "/sounds/medical_cabinet.wav"),
                        new StoryPuzzle(
                                7,
                                "夜班交會日",
                                "病房輪班表顯示：A 護理師每 4 天巡房一次，B 護理師每 6 天巡房一次；兩人在第 2 天同時巡房。",
                                "兩人下一次同時巡房是第幾天？",
                                "14|第14天",
                                "4 與 6 的最小公倍數是 12，再加起始日 2。",
                                "你在第 14 天的欄位下找到病房輪班表殘頁。",
                                205,
                                "/sounds/paper_open.wav"),
                        new StoryPuzzle(
                                8,
                                "電梯權限",
                                "電梯面板寫著：『第一位線索的選項編號 × 100，再加上下一個共同巡房日。』",
                                "請輸入三位數權限碼。",
                                "314",
                                "第一題是選項 3，第二題答案是 14。",
                                "權限碼 314 通過，電梯門緩緩開啟，你取得電梯磁卡。",
                                206,
                                "/sounds/elevator_open.wav")));
    }

    public static StoryGameDefinition laboratory() {
        return new StoryGameDefinition(
                4,
                4,
                4,
                "沉沒實驗室",
                "THE SUBMERGED LABORATORY",
                "海底實驗室的防水艙逐一失效。你必須利用氣體定律、聲納回波與多步運算重啟逃生系統。",
                "逃生艙脫離海底基地，穿過黑暗水域向海面上升。破曉時，你看見第一道陽光。",
                new Color(7, 27, 44),
                new Color(11, 70, 84),
                new Color(74, 196, 212),
                "/images/games/laboratory.png",
                Arrays.asList(
                        new StoryPuzzle(
                                9,
                                "艙壓平衡",
                                "氧氣艙在恆溫下原本為 240 kPa、3 L。維修活塞將體積擴張到 5 L。控制板標示：P1 x V1 = P2 x V2。",
                                "新壓力 P2 是多少 kPa？請輸入整數。",
                                "144|144kpa",
                                "P2 = 240 x 3 / 5。",
                                "壓力穩定在 144 kPa，你取得艙壓公式卡。",
                                207,
                                "/sounds/pressure_release.wav"),
                        new StoryPuzzle(
                                10,
                                "聲納回波",
                                "聲納發出脈衝後 2.4 秒收到海床回波。控制台指定水中聲速採 1500 m/s；時間包含去程與回程。",
                                "實驗室距離海床多少公尺？",
                                "1800|1800m|1800公尺",
                                "距離 = 聲速 x 回波時間 / 2。",
                                "計算結果為 1800 公尺，聲納紀錄被寫入資料片。",
                                208,
                                "/sounds/sonar_ping.wav"),
                        new StoryPuzzle(
                                11,
                                "逃生艙校驗碼",
                                "校驗規則：把艙壓 144 的各位數相加，再乘以海床距離除以 100。",
                                "請輸入校驗碼。",
                                "162",
                                "(1 + 4 + 4) x (1800 / 100) = 9 x 18。",
                                "校驗碼 162 正確，逃生艙授權晶片彈出並啟動發射程序。",
                                209,
                                "/sounds/hatch_open.wav")));
    }

    public static StoryGameDefinition mirrorHotel() {
        return new StoryGameDefinition(
                5,
                5,
                5,
                "鏡廳旅館",
                "THE HOTEL OF A THOUSAND REFLECTIONS",
                "旅館中的每面鏡子都映出不同年代的住客。你必須反向閱讀數字、破解 Atbash 字母鏡像，並組合主鏡封印碼。",
                "主鏡碎裂成銀色粉塵，旅館恢復寂靜。清晨的旋轉門再次轉動，你保住了自己的名字。",
                new Color(31, 20, 43),
                new Color(78, 48, 69),
                new Color(201, 170, 218),
                "/images/games/mirror_hotel.png",
                Arrays.asList(
                        new StoryPuzzle(
                                12,
                                "倒映房名",
                                "門牌只剩數列 13, 15, 15, 18。旁邊寫著 A = 1、B = 2，且『鏡中順序必須反轉』。",
                                "反轉後對應的四個英文字母是什麼？",
                                "ROOM",
                                "先反轉為 18、15、15、13，再換成英文字母。",
                                "字母組成 ROOM，816 號房門自行開啟，你取得旅客登記殘頁。",
                                210,
                                "/sounds/room_door.wav"),
                        new StoryPuzzle(
                                13,
                                "鏡像姓名",
                                "留言簿寫著 XLFMG。旁邊的鏡像字母表標示：A 對應 Z、B 對應 Y、C 對應 X，依此類推。",
                                "依照鏡像字母表解碼後的英文單字是什麼？",
                                "COUNT",
                                "這是 Atbash：每個字母替換成字母表另一端對稱的字母。",
                                "XLFMG 被還原為 COUNT，主鏡中央出現第二道裂縫。",
                                211,
                                "/sounds/mirror_whisper.wav"),
                        new StoryPuzzle(
                                14,
                                "主鏡封印",
                                "封印規則：把 ROOM 的字母序號總和作為前兩位，把 COUNT 的字母序號總和作為後兩位。",
                                "A = 1、B = 2，依此類推。請輸入四位封印碼。",
                                "6173",
                                "ROOM=18+15+15+13=61；COUNT=3+15+21+14+20=73。",
                                "封印碼 6173 生效，你取得主鏡銀鑰；所有倒影同時閉上眼睛。",
                                212,
                                "/sounds/mirror_shatter.wav")));
    }
}
