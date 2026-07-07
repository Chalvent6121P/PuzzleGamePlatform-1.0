package service.impl;

import java.util.List;

import dao.ReportDao;
import dao.impl.ReportDaoImpl;
import entity.GameRecordReportRow;
import entity.ReportSummary;
import service.AdminReportService;

public class AdminReportServiceImpl implements AdminReportService {

    private final ReportDao reportDao = new ReportDaoImpl();

    @Override
    public List<GameRecordReportRow> findAllGameRecords() {
        return reportDao.selectAllGameRecords();
    }

    @Override
    public ReportSummary loadSummary() {
        return reportDao.selectSummary();
    }
}
