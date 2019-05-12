package com.template.contracts;

import com.template.states.GradeState;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class GradeContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.template.contracts.GradeContract";

    @Override
    public void verify(LedgerTransaction tx) {
        requireSingleCommand(tx.getCommands(), GradeWork.class);
        requireThat(require -> {
            require.using("There cant be inputs", tx.getInputs().size() == 0);
            require.using("There should be only one grade", tx.getOutputs().size() == 1);
            require.using("output must be gradeState", (tx.getOutputs().get(0).getData()) instanceof GradeState);

            final GradeState output = (GradeState) tx.getOutputs().get(0).getData();

            require.using("Lector must not be null", output.getLector() != null);
            require.using("Student must not be null", output.getStudent() != null);
            require.using("Student must not be null", (output.getSubject() != null && !output.getSubject().equals("")));
            require.using(" Grade must be from 2 to 5", (output.getGrade() >= 2 && output.getGrade() <= 5));

            return null;
        });

    }

    // Used to indicate the transaction's intent.
    public static class GradeWork extends TypeOnlyCommandData {
    }
}