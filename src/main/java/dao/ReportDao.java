package dao;

import java.util.List;

import entity.GameRecordReportRow;
import entity.ReportSummary;

public interface ReportDao {

    List<GameRecordReportRow> selectAllGameRecords();

    ReportSummary selectSummary();
}
