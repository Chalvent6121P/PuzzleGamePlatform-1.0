package service;

import java.util.List;

import entity.GameRecordReportRow;
import entity.ReportSummary;

public interface AdminReportService {

    List<GameRecordReportRow> findAllGameRecords();

    ReportSummary loadSummary();
}
