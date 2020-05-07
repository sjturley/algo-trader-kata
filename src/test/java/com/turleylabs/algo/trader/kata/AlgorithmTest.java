package com.turleylabs.algo.trader.kata;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.UseReporter;
import org.approvaltests.reporters.intellij.IntelliJReporter;
import org.junit.Test;

import java.time.LocalDate;

@UseReporter(IntelliJReporter.class)
public class AlgorithmTest {

    private LocalDate startDate = LocalDate.of(2010, 3, 23);
    private LocalDate endDate = LocalDate.of(2020, 3, 6);

    @Test
    public void algorithmExecutesTrades() {
        RefactorMeAlgorithm refactorAlgorithm = new RefactorMeAlgorithm();

        refactorAlgorithm.run();

        Approvals.verify(refactorAlgorithm);
    }

    @Test
    public void algorithmExecutesTradesWithTestDoubles() {
        RefactorMeAlgorithm refactorAlgorithm = new RefactorMeAlgorithm() {
            @Override
            public void initialize() {
                super.initialize();
                this.setStartDate(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
                this.setEndDate(endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth());
            }

            @Override
            public void run() {
                datesUntil(startDate, endDate).forEach(date -> processData(new FakeSlice(date)));
            }
        };

        refactorAlgorithm.run();

        Approvals.verify(refactorAlgorithm);
    }


}
