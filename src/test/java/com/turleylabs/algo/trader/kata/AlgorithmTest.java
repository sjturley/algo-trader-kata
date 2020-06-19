package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.Slice;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

import static org.powermock.api.mockito.PowerMockito.whenNew;

@UseReporter(QuietReporter.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RefactorMeAlgorithm.class)
public class AlgorithmTest {
    @Test
    public void algorithmExecutesTrades() throws Exception {
        whenNew(Slice.class).withAnyArguments().then(invocation -> {
                    LocalDate tradeDate = (LocalDate) invocation.getArguments()[0];
                    String symbol = (String) invocation.getArguments()[1];
                    return new FakeSlice(tradeDate, symbol);
                }
        );

        RefactorMeAlgorithm refactorAlgorithm = new RefactorMeAlgorithm();

        refactorAlgorithm.run();

        Approvals.verify(refactorAlgorithm);
    }

}
