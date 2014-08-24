package org.jboss.examples.deltaspike.expensetracker.data;

import java.math.BigDecimal;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryParam;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;
import org.jboss.examples.deltaspike.expensetracker.domain.model.Employee;
import org.jboss.examples.deltaspike.expensetracker.domain.model.ExpenseReport;
import org.jboss.examples.deltaspike.expensetracker.domain.model.ExpenseReport_;
import org.jboss.examples.deltaspike.expensetracker.domain.model.ReportStatus;

@ApplicationScoped
@Repository
public abstract class ExpenseReportRepository implements EntityRepository<ExpenseReport, Long>, CriteriaSupport<ExpenseReport> {

    public abstract List<ExpenseReport> findByReporter(Employee reporter);

    public abstract List<ExpenseReport> findByAssignee(Employee assignee);

    public List<ExpenseReport> findUnassigned() {
        return criteria()
                .eq(ExpenseReport_.status, ReportStatus.SUBMITTED)
                .isNull(ExpenseReport_.assignee)
                .getResultList();
    }

    public abstract List<ExpenseReport> findByStatus(ReportStatus status);

    public abstract List<ExpenseReport> findByReporterAndStatus(Employee reporter, ReportStatus status);

    public abstract List<ExpenseReport> findByAssigneeAndStatus(Employee assignee, ReportStatus status);

    public List<ExpenseReport> findAllUnsettledByReporter(Employee reporter) {
        return criteria()
                .eq(ExpenseReport_.reporter, reporter)
                .notEq(ExpenseReport_.status, ReportStatus.SETTLED)
                .getResultList();
    }

    public List<ExpenseReport> findAllUnsettledByAssignee(Employee assignee) {
        return criteria()
                .eq(ExpenseReport_.assignee, assignee)
                .notEq(ExpenseReport_.status, ReportStatus.SETTLED)
                .getResultList();
    }

    @Query("SELECT SUM(e.value) FROM Expense e WHERE e.report = :report")
    public abstract BigDecimal getReportExpensesTotal(@QueryParam("report") ExpenseReport report);

    @Query("SELECT SUM(r.value) FROM Reimbursement r WHERE r.report = :report")
    public abstract BigDecimal getReportReimbursementTotal(@QueryParam("report") ExpenseReport report);

    @Query("SELECT SUM(r.value)-SUM(e.value) FROM Reimbursement r, Expense e WHERE r.report = :report AND e.report = :report")
    public abstract BigDecimal getReportBalance(@QueryParam("report") ExpenseReport report);

}
